package io.dropwizard.pinot.repository.pinot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import io.dropwizard.pinot.healthcheck.Health;
import io.dropwizard.pinot.healthcheck.configs.exception.*;
import io.dropwizard.pinot.housekeeping.RealtimeServerTaggingRequest;
import io.dropwizard.pinot.models.kafka.Topic;
import io.dropwizard.pinot.repository.pinot.clients.PinotClient;
import io.dropwizard.pinot.repository.pinot.request.InstanceTagUpdateRequest;
import io.dropwizard.pinot.storage.pinot.pinotspec.cluster.InstanceType;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotSchemaConfig;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableConfig;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableType;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TierConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.dropwizard.pinot.TopicService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import io.dropwizard.pinot.utils.SerDe;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@javax.inject.Inject}))
public class PinotAdminDaoImpl implements PinotAdminDao {

    private static final String DAYS_KEY = "DAYS";

    private static final String SERVER_INSTANCE_PREFIX = "Server";
    private static final String BROKER_INSTANCE_PREFIX = "Broker";
    private static final String CONTROLLER_INSTANCE_PREFIX = "Controller";

    private static final String DEFAULT_TENANT_NAME = "DefaultTenant";

    private static final String INSTANCE_NAME = "instanceName";

    private final TopicService topicService;

    private final PinotClient pinotClient;

    @Override
    public PinotSchemaConfig createSchema(PinotSchemaConfig schemaConfig) {
        Optional<PinotSchemaConfig> configOptional = getSchemaSilently(schemaConfig.getSchemaName());
        if (configOptional.isPresent()) {
            throw PinotClusterException.error(PinotClusterErrorCode.SCHEMA_ALREADY_EXISTS,
                    "Schema of same name already exist: " + schemaConfig.getSchemaName(), false);
        }

        try {
            ObjectNode objectNode = pinotClient.createSchema(schemaConfig);

            return SerDe.convertValue(objectNode, PinotSchemaConfig.class);
        } catch (Exception ex) {
            log.error("Exception in creating schema {} for config {} ", ex.getLocalizedMessage(), schemaConfig);
            throw PinotClusterException.error(PinotClusterErrorCode.SCHEMA_CREATION_FAILED, ex.getLocalizedMessage(), false);
        }
    }

    @Override
    public TableConfig createTable(TableConfig tableConfig) {


//        TableConfig tableConfigg = getTableConfig(JsonPathHelper.get(tableConfig, "$.tableName"),
//                JsonPathHelper.get(tableConfig, "$.tableType"));
//        if (Objects.nonNull(tableConfigg)) {
//            throw PinotClusterException.error(PinotClusterErrorCode.SCHEMA_ALREADY_EXISTS,
//                    "Table of same name already exist: " + tableConfigg.getTableName(), false);
//        }

        //check table already doesn't exist - either in RDBMS or Pinot cluster.


        PinotSchemaConfig existingSchema = getSchema(tableConfig.getSegmentsConfig().getSchemaName());

        //preparation
        //validateTaggedServersExist();

        return pinotClient.createTable(tableConfig);
    }

    @Override
    public Object validateSchemaConfig(PinotSchemaConfig schemaConfig) {
        return pinotClient.validateSchemaConfig(schemaConfig);
    }

    @Override
    public Object validateTableConfig(TableConfig tableConfig) {
        return pinotClient.validateTableConfig(tableConfig);
    }

    @Override
    public PinotSchemaConfig getSchema(String schemaName) {
        try {
            ObjectNode objectNode = pinotClient.getSchema(schemaName);

            if (Objects.isNull(objectNode)) {
                throw PinotClusterException.error(PinotClusterErrorCode.INVALID_SCHEMA_ID,
                        "Schema name: " + schemaName, false);
            }

            return SerDe.convertValue(objectNode, PinotSchemaConfig.class);
        } catch (Exception ex) {
            log.error("Exception in Getting schemaconfig {} for schema {} ", ex.getLocalizedMessage(), schemaName);
            throw PinotClusterException.error(PinotClusterErrorCode.SCHEMA_CREATION_FAILED, ex.getLocalizedMessage(), false);
        }
    }

    @Override
    public Optional<PinotSchemaConfig> getSchemaSilently(String schemaName) {
        try {
            return Optional.of(getSchema(schemaName));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public Health ping() {
        try {
            boolean ping = pinotClient.ping();
            if (ping) {
                return Health.OK;
            }

            return Health.DEGRADED;
        } catch (Exception ex) {
            log.error("Exception while pinging Pinot cluster {}", ex.getLocalizedMessage());
        }
        return Health.CONNECTIVITY_LOST;
    }

    @Override
    public Set<String> getAllTopics() {
        try {
            return topicService.getTopicsSync();
        } catch (Exception ex) {
            log.error("Exception while fetching topics {}", ex.getLocalizedMessage());
            throw PinotDaoException.propagate(ErrorCode.PINOT_KAFKA_DAO_ERROR, ex);
        }
    }

    @Override
    public Topic getTopic(String topicName) {
        try {
            return (Topic) topicService.getTopic(topicName).get();
        } catch (Exception ex) {
            log.error("Exception while getting details of kafka topic: " + topicName);
            throw PinotDaoException.propagate(ErrorCode.PINOT_TABLE_INGESTION_TOPIC_GET_ERROR, ex);
        }
    }

    @Override
    public void validateTopicExists(String topic) {
        if (! getAllTopics().contains(topic)) {
            throw PinotClusterException.error(PinotClusterErrorCode.TABLE_INGESTION_TOPIC_NOT_EXIST,
                    "Kafka topic not exist", false);
        }
    }

    @Override
    public TableConfig getTableConfig(String tableId, TableType tableType) {

        ObjectNode objectNode = pinotClient.getTableConfig(tableId);


        if (Objects.isNull(objectNode)) {
            throw PinotClusterException.error(PinotClusterErrorCode.INVALID_SCHEMA_ID,
                    "Table name: " + tableId, false);
        }

        return SerDe.convertValue(objectNode.get(tableType.name()), TableConfig.class);
    }

    @Override
    public Long getTableDataRetentionInDays(String tableId, TableType tableType) {
        TableConfig tableConfig = getTableConfig(tableId, tableType);

        String retentionTimeunit = tableConfig.getSegmentsConfig().getRetentionTimeUnit();

        if (retentionTimeunit.equalsIgnoreCase(DAYS_KEY)) {
            return Long.parseLong(tableConfig.getSegmentsConfig().getRetentionTimeValue());
        }

        throw QueryProcessingException.error(QueryErrorCode.UNSUPPORTED_OPERATION_ERROR, "Parsing retentionTimeunit" +
                ": " + retentionTimeunit + " is not supported", false);
    }

    /**
     * Implicit assumption here is that all servers are of identical configuration.
     * Otherwise, this API cannot be used in present form and it will require server spec
     * as part of tagging request.
     * @param realtimeServerTaggingRequest
     * @return
     */
    @Override
    public Map<String, List<String>> tagServers(RealtimeServerTaggingRequest realtimeServerTaggingRequest) {

        List<ObjectNode> liveServers = getLiveInstancesOfType(InstanceType.SERVER);

        Map<String, List<String>> taggedServers = new HashMap<>();

        //tag servers for pending count
        List<ObjectNode> untaggedServers = getUntaggedInstances(liveServers);

        //preprocess
        for (String tenant : realtimeServerTaggingRequest.getTenantToTaggingCountMap().keySet()) {

            List<ObjectNode> serversTaggedWithTenant = getInstancesTaggedWithTenant(tenant, Optional.of(liveServers));

            if (CollectionUtils.isNotEmpty(serversTaggedWithTenant)) {
                taggedServers.put(tenant, serversTaggedWithTenant.stream().map(server -> server.get(INSTANCE_NAME).asText())
                        .collect(Collectors.toList()));
            } else {
                taggedServers.put(tenant, new ArrayList<>());
            }

            Integer pendingTagging = realtimeServerTaggingRequest.getTenantToTaggingCountMap().get(tenant) - taggedServers.get(tenant).size();

            if (untaggedServers.size() < pendingTagging) {
                throw PinotClusterException.error(PinotClusterErrorCode.NO_SERVERS_AVAILABLE_FOR_TAGGING,
                        String.format("Required untagged servers: %s for tier: %s, found: %s", pendingTagging,
                                tenant, untaggedServers), false);
            }

            List<ObjectNode> toBeTagged = untaggedServers.subList(0, pendingTagging);

            String tenantTag = String.format("%s_REALTIME", tenant);

            toBeTagged.stream()
                    .forEach(server -> {
                        InstanceTagUpdateRequest request = InstanceTagUpdateRequest.builder()
                                .instanceName(server.get(INSTANCE_NAME).asText())
                                .newTags(Lists.newArrayList(tenantTag))
                                .build();
                        pinotClient.validateUpdateTagsRequest(Lists.newArrayList(request));
                        pinotClient.updateTagsForInstance(request.getInstanceName(), request.getNewTags());
                        taggedServers.get(tenant).add(request.getInstanceName());
                    });

            untaggedServers = ListUtils.subtract(untaggedServers, toBeTagged);
        }

        return taggedServers;
    }

    private List<ObjectNode> getInstancesTaggedWithTenant(String tenant, Optional<List<ObjectNode>> allInstances) {
        List<ObjectNode> liveInstances = allInstances.orElse(getLiveInstances());

        return liveInstances.stream()
                .filter(instance -> {
                    List<String> tags = instance.findValuesAsText("tags");
                    return tags.stream().anyMatch(tag -> tag.equalsIgnoreCase(tenant));
                }).collect(Collectors.toList());
    }

    private List<ObjectNode> getUntaggedInstances(List<ObjectNode> instances) {

        return instances.stream()
                .filter(instance -> {
                    List<String> tags = null;
                    try {
                        tags = SerDe.readerFor(new TypeReference<List<String>>(){})
                                .readValue(instance.get("tags"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return (CollectionUtils.isEmpty(tags) ||
                            tags.stream().allMatch(tag -> tag.equalsIgnoreCase(DEFAULT_TENANT_NAME)));
        }).collect(Collectors.toList());
    }

    private List<ObjectNode> getLiveInstancesOfType(InstanceType instanceType) {
        if (instanceType.equals(InstanceType.SERVER)) {
            return getLiveInstances().stream().filter(instance -> instance.get("instanceName").asText()
                    .startsWith(SERVER_INSTANCE_PREFIX)).collect(Collectors.toList());
        }
        if (instanceType.equals(InstanceType.BROKER)) {
            return getLiveInstances().stream().filter(instance -> instance.get("instanceName").asText()
                    .startsWith(BROKER_INSTANCE_PREFIX)).collect(Collectors.toList());
        }
        if (instanceType.equals(InstanceType.CONTROLLER)) {
            return getLiveInstances().stream().filter(instance -> instance.get("instanceName").asText()
                    .startsWith(CONTROLLER_INSTANCE_PREFIX)).collect(Collectors.toList());
        }

        throw new IllegalArgumentException(String.format("%s: get live instances not supported", instanceType.name()));
    }

    private List<ObjectNode> getLiveInstances() {
        List<String> liveInstances = pinotClient.getLiveInstanceNames();

        //get info of instances.
        return liveInstances.stream().map(pinotClient::getInstanceInfo)
                .collect(Collectors.toList());
    }

    /**
     * 1. Get list of Servers.
     * 2. Check if you can update server tag.
     *  2.1 If you can update, update server tag to desired tenant.
     *  2.3 Number of servers to be tagged per tenant - number of replicas + X
     */
    private void tagServers(TableConfig tableConfig) {
        List<String> instances = pinotClient.getLiveInstanceNames();
        List<String> servers = null;

        if (CollectionUtils.isEmpty(instances)) {
            throw PinotClusterException.error(PinotClusterErrorCode.NO_SERVERS_AVAILABLE_FOR_TAGGING,
                    "Tagging servers failed", false);
        }

        servers = instances.stream().filter(instance -> instance.startsWith(SERVER_INSTANCE_PREFIX))
                .collect(Collectors.toList());

        //create batch requests for different tiers of servers
        //validate a batch and then tag.

        String tenantNameForTierOne = tableConfig.getTenants().getServer();

        //other tiers
        List<String> tenantNameForOtherTiers = tableConfig.getTierConfigs().stream().map(TierConfig::getServerTag)
                .collect(Collectors.toList());

        //dry run update tags request.
        List<InstanceTagUpdateRequest> requests = new ArrayList<>();

        servers.forEach(server -> {
            requests.add(InstanceTagUpdateRequest.builder()
                    .instanceName(server)
                    .newTags(Lists.newArrayList())
                    .build());
        });

    }
}
