package io.dropwizard.pinot.repository.pinot.helper;

import io.dropwizard.pinot.config.Mode;
import io.dropwizard.pinot.config.PinotDaoBundleConfig;
import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;
import io.dropwizard.pinot.repository.pinot.PinotDao;
import io.dropwizard.pinot.repository.pinot.PinotDaoImpl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DaoRegistry {

    private final Map<Class, PinotDao> daoRegistry = new ConcurrentHashMap<>();

    public PinotDao register(Class entityClass, PinotDao pinotDao) {
        if (daoRegistry.containsKey(entityClass)) {
            throw PinotDaoException.error(ErrorCode.DAO_ALREADY_EXISTS, Map.of("entity", entityClass.getSimpleName()));
        }
        //todo: add validation that type of pinotdao matches entityclass.

        return daoRegistry.put(entityClass, pinotDao);
    }

    public PinotDao get(Class entityClass) {
        if (!daoRegistry.containsKey(entityClass)) {
            throw PinotDaoException.error(ErrorCode.DAO_NOT_CREATED, Map.of("entity", entityClass.getSimpleName()));
        }
        return daoRegistry.get(entityClass);
    }

    public Optional<PinotDao> optionalGet(Class entityClass) {
        if (daoRegistry.containsKey(entityClass)) {
            return Optional.empty();
        }
        return Optional.ofNullable(daoRegistry.get(entityClass));
    }

}
