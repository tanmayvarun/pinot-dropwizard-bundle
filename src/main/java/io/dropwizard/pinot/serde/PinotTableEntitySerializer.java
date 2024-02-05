package io.dropwizard.pinot.serde;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;
import io.dropwizard.pinot.utils.SerDe;

import java.util.Map;

@RequiredArgsConstructor
public class PinotTableEntitySerializer implements Serializer<Map<String, Object>> {


    @Override
    public byte[] serialize(String s, Map<String, Object> o) {
        byte[] retVal = null;
        try {
            retVal = SerDe.writeValueAsBytes(o);
        } catch (Exception e) {
            throw PinotDaoException.error(ErrorCode.JSON_SERIALIZATION_ERROR,
                    ImmutableMap.of("error", "Exception while serializing object"));
        }
        return retVal;
    }
}
