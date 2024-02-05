package io.dropwizard.pinot.serde;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class StringSerDe implements Deserializer<String>, Serializer<String> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public String deserialize(String s, byte[] bytes) {
        return new String(bytes);
    }

    @Override
    public String deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    @Override
    public void close() {
    }

    @Override
    public byte[] serialize(String s, String s2) {
        return s2.getBytes();
    }
}
