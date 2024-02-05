package io.dropwizard.pinot.cache;

import com.google.common.cache.CacheLoader;
import io.dropwizard.pinot.PinotDaoBundle;
import io.dropwizard.pinot.repository.pinot.PinotAdminDao;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotTableDetails;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotTableKey;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableConfig;
import lombok.Getter;

import javax.inject.Provider;

@Getter
public class PinotTableCache extends Cache<PinotTableKey, PinotTableDetails> {

    public PinotTableCache(PinotAdminDao pinotAdminDao) {

        super(86400L, 1000, new CacheLoader<PinotTableKey, PinotTableDetails>() {
            @Override
            public PinotTableDetails load(PinotTableKey key) throws Exception {
                TableConfig tableConfig = pinotAdminDao.getTableConfig(key.getTableName(), key.getTableType());
                return PinotTableDetails.builder()
                        .tableConfig(tableConfig)
                        .schemaConfig(pinotAdminDao.getSchema(tableConfig.getSegmentsConfig().getSchemaName()))
                        .build();
            }
        });
    }
}
