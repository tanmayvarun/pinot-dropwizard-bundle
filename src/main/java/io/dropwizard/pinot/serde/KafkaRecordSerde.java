package io.dropwizard.pinot.serde;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;
import lombok.Getter;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import io.dropwizard.pinot.utils.SerDe;

import java.util.Collections;
import java.util.Map;

@Getter
public class KafkaRecordSerde implements Serializer<Record>, Deserializer<Record> {

  @Override
  public Record deserialize(String s, byte[] bytes) {
    return SerDe.readValue(bytes, Record.class);
  }

  @Override
  public Record deserialize(String topic, Headers headers, byte[] data) {
    return Deserializer.super.deserialize(topic, headers, data);
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    throw PinotDaoException.error(ErrorCode.UNSUPPORTED_OPERATION_ERROR, Collections.emptyMap());
  }

  @Override
  public byte[] serialize(String s, Record o) {
    byte[] retVal = null;
    try {
      retVal = SerDe.writeValueAsBytes(o);
    } catch (Exception e) {
      throw PinotDaoException.error(ErrorCode.JSON_SERIALIZATION_ERROR,
              ImmutableMap.of("error", "Exception while serializing object"));
    }
    return retVal;
  }

  @Override
  public byte[] serialize(String topic, Headers headers, Record data) {
    throw PinotDaoException.error(ErrorCode.UNSUPPORTED_OPERATION_ERROR, Collections.emptyMap());
  }

  @Override
  public void close() {
  }
}