package io.dropwizard.pinot.repository.pinot.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.dropwizard.pinot.healthcheck.configs.Endpoint;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotClusterErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotClusterException;
import io.dropwizard.pinot.healthcheck.configs.exception.QueryErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.QueryProcessingException;
import io.dropwizard.pinot.repository.pinot.request.InstanceTagUpdateRequest;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotSchemaConfig;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableConfig;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableType;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pinot.client.ConnectionFactory;
import org.apache.pinot.client.PinotClientException;
import org.apache.pinot.client.ResultSetGroup;
import io.dropwizard.pinot.utils.HttpUtils;
import io.dropwizard.pinot.utils.Scheme;
import io.dropwizard.pinot.utils.SerDe;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static io.dropwizard.pinot.utils.SerDe.writeValueAsString;

/**
 * We have decided to use pinot java client as the choice of tool to create connections with pinot.
 * It will provide known request / response models and reduce the effort in understanding constructs
 * as well as provide a ready store of utility methods in order to read and marshall information into
 * usable internal formats.
 */
@Slf4j
public class PinotClient {

    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    private static final String SQL_QUERY_FORMAT_KEY = "sql";
    // pinot connection

    //todo: convert to pool of brokers
    private String brokerEndpointWithoutScheme;

    //used for all admin tasks
    private String httpControllerEndpoint;

    private String httpBrokerEndpoint;

    private OkHttpClient client = new OkHttpClient.Builder()
            .build();

    public PinotClient(Endpoint brokerEndpoint, Endpoint controllerEndpoint) {
        this.brokerEndpointWithoutScheme = HttpUtils.endpoint(brokerEndpoint, Optional.empty());
        this.httpControllerEndpoint = HttpUtils.endpoint(controllerEndpoint,
                Optional.of(Scheme.HTTP));
        this.httpBrokerEndpoint = HttpUtils.endpoint(brokerEndpoint, Optional.of(Scheme.HTTP));

        //create a connection pool
    }

    //admin paths

    private static final String VALIDATE_SCHEMA_PATH = "/schemas/validate";

    private static final String CREATE_TABLE_PATH = "/tables";

    private static final String INSTANCES_PATH = "/instances";

    private static final String INSTANCES_TAGS_UPDATE_VALIDATION_PATH = "/instances/updateTags/validate";


    private static final String CREATE_SCHEMA_PATH = "/schemas";

    private static final String VALIDATE_TABLE_PATH = "/tables/validate";

    private static final String GET_TABLE_CONFIG_PATH = "/tables";

    private static final String GET_SCHEMA_TABLE_CONFIG_PATH = "/schemas";

    private static final String HEALTH_CHECK_API_PATH = "/health";


    //query paths

    private static final String QUERY_API_PATH = "/io/dropwizard/pinot/query/sql";

    private static final String zkUrl = "localhost:2181";
    private static final String pinotClusterName = "PinotCluster";
//
//    public RawResponse query(RawQuery pinotRawQuery) {
//        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(endpoint).path(QUERY_API_PATH).build());
//        RequestBody body = RequestBody.create(Objects.requireNonNull(SerDe.writeValueAsString(pinotRawQuery)),
//                okhttp3.MediaType.get(javax.ws.rs.core.MediaType.APPLICATION_JSON));
//
//        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.POST.name(), Headers.of(),
//                body, Collections.emptyMap());
//
//        //checks admin health
//        try {
//            Response response = client.newCall(request).execute();
//            if (! response.isSuccessful()) {
//                throw ScribeRealtimeReportException.error(ErrorCode.PINOT_DAO_ERROR,
//                        Map.of("httpstatus", response.code(),
//                                "message", response.message()));
//            }
//
//            return SerDe.readValue(Objects.requireNonNull(response.body()).bytes(), RawResponse.class);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public List<String> getLiveInstanceNames() {
        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpControllerEndpoint)
                .path(INSTANCES_PATH)
                .build());

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.GET.name(), Headers.of(), null,
                Collections.emptyMap());
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw PinotClusterException.error(PinotClusterErrorCode.GET_INSTANCES_LIST_FAILED,
                        "Error while creating table: " + response.body().string(), true);
            }

            ObjectNode node = SerDe.readValue(response.body().bytes(), ObjectNode.class);
            ObjectReader listReader = SerDe.readerFor(new TypeReference<List<String>>() {});
            return listReader.readValue(node.get("instances"));
        } catch (IOException e) {
            String error = String.format("Error while getting list of live instances, error %s",
                    ExceptionUtils.getStackTrace(e));
            log.error(error);
            throw PinotClusterException.error(PinotClusterErrorCode.TABLE_CREATION_FAILED, error, false);
        }
    }

    public ObjectNode getInstanceInfo(String instanceName) {
        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpControllerEndpoint)
                .path(INSTANCES_PATH)
                .path(instanceName)
                .build());

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.GET.name(), Headers.of(), null,
                Collections.emptyMap());
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw PinotClusterException.error(PinotClusterErrorCode.GET_INSTANCE_INFO_FAILED,
                        "Error while getting info about instance: " + response.body().string(), true);
            }
            return SerDe.readValue(response.body().bytes(), ObjectNode.class);
        } catch (IOException e) {
            String error = String.format("Error while getting list of live instances, error %s",
                    ExceptionUtils.getStackTrace(e));
            log.error(error);
            throw PinotClusterException.error(PinotClusterErrorCode.TABLE_CREATION_FAILED, error, false);
        }
    }

    /**
     * [
     *   {
     *     "instanceName": "Server_a.b.com_20000",
     *     "newTags": [
     *       "string"
     *     ]
     *   }
     * ]
     */
    public boolean validateUpdateTagsRequest(List<InstanceTagUpdateRequest> requests) {

        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpControllerEndpoint)
                .path(INSTANCES_TAGS_UPDATE_VALIDATION_PATH)
                .build());

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.POST.name(), Headers.of(),
                RequestBody.create(writeValueAsString(requests), MediaType.parse(CONTENT_TYPE_APPLICATION_JSON)),
                Collections.emptyMap());
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw PinotClusterException.error(PinotClusterErrorCode.UPDATE_TAGS_VALIDATION_FAILED,
                        "Error while validating update tags: " + response.body().string(), true);
            }
            List<ObjectNode> validationResults = SerDe.readerFor(new TypeReference<List<ObjectNode>>(){}).readValue(response.body().bytes());

            return validationResults.stream().allMatch(result -> result.get("isSafe").asBoolean());

        } catch (IOException e) {
            String error = String.format("Error while getting list of live instances, error %s",
                    ExceptionUtils.getStackTrace(e));
            log.error(error);
            throw PinotClusterException.error(PinotClusterErrorCode.TABLE_CREATION_FAILED, error, false);
        }
    }

    public ObjectNode updateTagsForInstance(@NotBlank String instanceName, List<String> newTags) {
        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpControllerEndpoint)
                .path(INSTANCES_PATH)
                .path(instanceName)
                .path("updateTags")
                .queryParam("tags", String.join(",", newTags))
                .build());

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.PUT.name(), Headers.of(),
                null, Collections.emptyMap());
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw PinotClusterException.error(PinotClusterErrorCode.UPDATE_TAGS_VALIDATION_FAILED,
                        String.format("Error while update tags for instance: %s:, response: %s", instanceName,
                                response.body().string()), true);
            }
            return SerDe.readValue(response.body().bytes(), ObjectNode.class);
        } catch (IOException e) {
            String error = String.format("Error while getting list of live instances, error %s",
                    ExceptionUtils.getStackTrace(e));
            log.error(error);
            throw PinotClusterException.error(PinotClusterErrorCode.TABLE_CREATION_FAILED, error, false);
        }
    }


    public TableConfig createTable(TableConfig tableConfig) {
        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpControllerEndpoint)
                .path(CREATE_TABLE_PATH)
                .build());

        String serialized = writeValueAsString(tableConfig);

        log.info("serialized" + serialized);

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.POST.name(), Headers.of(),
                RequestBody.create(Objects.requireNonNull(writeValueAsString(tableConfig)),
                        MediaType.parse(CONTENT_TYPE_APPLICATION_JSON)), Collections.emptyMap());
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw PinotClusterException.error(PinotClusterErrorCode.TABLE_CREATION_FAILED,
                        "Error while creating table: " + response.body().string(), true);
            }

            ObjectNode tableNode = getTableConfig(tableConfig.getTableName());
            return SerDe.convertValue(tableNode.get(TableType.REALTIME.name()), TableConfig.class);
        } catch (IOException e) {
            String error = String.format("Error while creating table with config %s, error %s", tableConfig,
                    ExceptionUtils.getStackTrace(e));
            log.error(error);
            throw PinotClusterException.error(PinotClusterErrorCode.TABLE_CREATION_FAILED, error, false);
        }
    }

    public ObjectNode createSchema(PinotSchemaConfig schemaConfig) {
        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpControllerEndpoint)
                .path(CREATE_SCHEMA_PATH)
                .build());

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.POST.name(), Headers.of(),
                RequestBody.create(Objects.requireNonNull(writeValueAsString(schemaConfig)),
                        MediaType.parse(CONTENT_TYPE_APPLICATION_JSON)), Collections.emptyMap());
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw PinotClusterException.error(PinotClusterErrorCode.SCHEMA_CREATION_FAILED,
                        "Error while creating table: " + response.body().string(), true);
            }

            return getSchema(schemaConfig.getSchemaName());
        } catch (Exception e) {
            String error = String.format("Error while creating schema with config %s, error %s", schemaConfig,
                    ExceptionUtils.getStackTrace(e));
            log.error(error);
            throw PinotClusterException.error(PinotClusterErrorCode.TABLE_CREATION_FAILED, error, false);
        }
    }

    public ObjectNode validateSchemaConfig(PinotSchemaConfig schemaConfig) {
        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpControllerEndpoint)
                .path(VALIDATE_SCHEMA_PATH)
                .build());

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.POST.name(), Headers.of(),
                RequestBody.create(Objects.requireNonNull(writeValueAsString(schemaConfig)),
                        MediaType.parse(CONTENT_TYPE_APPLICATION_JSON)), Collections.emptyMap());

        //checks admin health
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw QueryProcessingException.error(QueryErrorCode.DAO_CONNECTION_ERROR,
                        "Error while validating schema config", true);
            }

            return SerDe.readValue(response.body().bytes(), ObjectNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectNode validateTableConfig(TableConfig tableConfig) {
        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpControllerEndpoint)
                .path(VALIDATE_TABLE_PATH)
                .build());

        String serialized = writeValueAsString(tableConfig);
        System.out.println("serialized table config: " + serialized);

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.POST.name(), Headers.of(),
                RequestBody.create(Objects.requireNonNull(writeValueAsString(tableConfig)),
                        MediaType.parse(CONTENT_TYPE_APPLICATION_JSON)), Collections.emptyMap());

        //checks admin health
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                String error = response.body().string();
                log.error("Error in validating table config {}, error {} ", tableConfig, error);
                throw PinotClusterException.error(PinotClusterErrorCode.TABLE_CREATION_FAILED,
                        "Error while validating table config, error: " + error, true);
            }

            return SerDe.readValue(response.body().bytes(), ObjectNode.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectNode getTableConfig(String tableId) {

        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpControllerEndpoint)
                .path(GET_TABLE_CONFIG_PATH)
                .path(tableId)
                .build());

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.GET.name(), Headers.of(),
                null, Collections.emptyMap());

        //checks admin health
        try {
            Response response = client.newCall(request).execute();
            if (! response.isSuccessful()) {
                throw QueryProcessingException.error(QueryErrorCode.DAO_CONNECTION_ERROR, "Error while getting" +
                        "table config: " + tableId, true);
            }

            return SerDe.readValue(response.body().bytes(), ObjectNode.class);

        } catch (Exception e) {
            String error = String.format("Error while creating schema with config %s, error %s", tableId,
                    ExceptionUtils.getStackTrace(e));
            log.error(error);
            throw PinotClusterException.error(PinotClusterErrorCode.TABLE_CREATION_FAILED, error, false);
        }
    }

    public ObjectNode getSchema(String schemaName) {

        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpControllerEndpoint)
                .path(GET_SCHEMA_TABLE_CONFIG_PATH)
                .path(schemaName)
                .build());

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.GET.name(), Headers.of(),
                null, Collections.emptyMap());

        //checks admin health
        try {
            Response response = client.newCall(request).execute();
            if (! response.isSuccessful()) {
                throw QueryProcessingException.error(QueryErrorCode.DAO_CONNECTION_ERROR, "Error while getting" +
                        "schema config: " + schemaName, true);
            }

            return SerDe.readValue(response.body().bytes(), ObjectNode.class);

        } catch (Exception e) {
            String error = String.format("Error while Getting schema config %s, error %s", schemaName,
                    ExceptionUtils.getStackTrace(e));
            log.error(error);
            throw PinotClusterException.error(PinotClusterErrorCode.DAO_ERROR, error, false);
        }
    }

    public ResultSetGroup query(String sql) throws PinotClientException {

        org.apache.pinot.client.Connection pinotConnection = ConnectionFactory.fromHostList(brokerEndpointWithoutScheme);

        // set queryType=sql for querying the sql endpoint
        try {
            return pinotConnection.execute(new org.apache.pinot.client.Request("sql", sql));
        } catch (PinotClientException ex) {
            if (ex.getCause() instanceof ExecutionException) {
                log.error("Host connectivity issue, please raising alarm, [error {}]", ex.getLocalizedMessage());
            }
            throw ex;
        }
    }

    public Boolean ping() {

        HttpUrl httpUrl = HttpUrl.get(UriBuilder.fromPath(httpBrokerEndpoint).path(HEALTH_CHECK_API_PATH).build());

        okhttp3.Request request = new okhttp3.Request(httpUrl, MethodType.GET.name(), Headers.of(),
                null, Collections.emptyMap());

        //checks admin health
        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        PinotClient client = new PinotClient(
                Endpoint.builder()
                                .host("localhost")
                                .port("8099")
                                .build(),
                        Endpoint.builder()
                                .host("localhost")
                                .port("9000")
                                .build());
        System.out.println("ping result: " + client.ping());
    }

}
