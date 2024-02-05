package io.dropwizard.pinot;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.pinot.cache.PinotTableCache;
import io.dropwizard.pinot.config.KafkaProducerConfig;
import io.dropwizard.pinot.config.Mode;
import io.dropwizard.pinot.config.PinotDaoBundleConfig;
import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;
import io.dropwizard.pinot.repository.pinot.*;
import io.dropwizard.pinot.repository.pinot.clients.PinotClient;
import io.dropwizard.pinot.repository.pinot.helper.DaoRegistry;
import io.dropwizard.pinot.repository.pinot.helper.EntityRegistry;
import io.dropwizard.pinot.serde.PinotTableEntitySerializer;
import io.dropwizard.pinot.serde.StringSerDe;
import io.dropwizard.pinot.utils.SerDe;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.*;

@Getter
public abstract class PinotDaoBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private TopicService topicService;

    private ProducerService producerService;

    private PinotClient pinotClient;

    private EntityRegistry entityRegistry;

    private DaoRegistry daoRegistry;

    private EntityMapperImpl entityMapper;

    private PinotAdminDao pinotAdminDao;

    private PinotDaoBundleConfig config;

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        this.config = getConfig(configuration);

        SerDe.init(environment.getObjectMapper());
        KafkaProducerConfig kafkaProducerConfig = config.getKafkaProducerConfig();
        if (Objects.equals(kafkaProducerConfig.getSecurityProtocol(), "SASL_PLAINTEXT")) {
            fetchKeytab(configuration);
        }
        Properties kafkaProducerProperties = kafkaProducerConfig.toProperties();
        AdminClient adminClient = AdminClient.create(kafkaProducerProperties);
        KafkaProducer<String, Map<String, Object>> kafkaProducer = new KafkaProducer<>(
                kafkaProducerProperties,
                new StringSerDe(),
                new PinotTableEntitySerializer());

        this.producerService = new PinotKafkaProducerServiceImpl(kafkaProducer);
        this.topicService = new PinotKafkaTopicServiceImpl(adminClient, producerService);
        this.pinotClient = new PinotClient(config.getBrokerEndpoint(), config.getControllerEndpoint());
        this.pinotAdminDao = new PinotAdminDaoImpl(topicService, pinotClient);
        this.entityRegistry = new EntityRegistry(new PinotTableCache(pinotAdminDao));
        this.daoRegistry = new DaoRegistry();
        this.entityMapper = new EntityMapperImpl();

        if (config.getMode().equals(Mode.EXECUTION)) {
            registerEntitiesInternal(configuration);
        }

    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    protected abstract PinotDaoBundleConfig getConfig(T configuration);

    protected abstract List<Class> registerEntities();

    private void registerEntitiesInternal(T configuration) {

        List<Class> entities = registerEntities();

        if (CollectionUtils.isEmpty(entities)) {
            throw PinotDaoException.error(ErrorCode.NO_ENTITY_REGISTERED, null);
        }
        entityRegistry.registerEntities(entities);
        entities.forEach(this::createPinotDao);

    }
    protected abstract void fetchKeytab(T configuration);

    public <U> PinotDao<U> createPinotDao(Class entityClass) {

        if (config.getMode().equals(Mode.MAINTENANCE)) {
            throw PinotDaoException.error(ErrorCode.DAO_CREATION_IN_MAINTENANCE_MODE_NOT_ALLOWED,
                    Map.of("mode", "maintenance mode"));
        }

        return getPinotDaoOptionally(entityClass)
                .orElse(
                        daoRegistry.register(entityClass,
                                new PinotDaoImpl(topicService, pinotClient, entityRegistry, entityMapper, entityClass)));
    }

    public PinotDao getPinotDao(Class entityClass) {
        return daoRegistry.get(entityClass);
    }

    public Optional<PinotDao> getPinotDaoOptionally(Class entityClass) {
        return daoRegistry.optionalGet(entityClass);
    }
}
