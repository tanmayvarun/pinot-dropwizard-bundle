package io.dropwizard.pinot.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.InputStream;

@UtilityClass
@Slf4j
public class SerDe {

    private static final String VALUE_TYPE_REF = "valueTypeRef";
    private ObjectMapper mapper;

    public void init(final ObjectMapper objectMapper) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        objectMapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        mapper = objectMapper;
    }

    public static ObjectReader readerFor(TypeReference typeReference) {
        return SerDe.mapper().readerFor(typeReference);
    }

    @Nullable
    public static String writeValueAsString(Object value) {
        return writeValueAsString(mapper(), value);
    }

    @Nullable
    public static String writeValueAsString(ObjectMapper objectMapper, Object value) {
        try {
            if (value == null) {
                return null;
            }
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("Error while serializing object" + value, e);
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static byte[] writeValueAsBytes(Object value) {
        return writeValueAsBytes(mapper(), value);
    }

    @Nullable
    public static byte[] writeValueAsBytes(ObjectMapper objectMapper, Object value) {
        try {
            if (value == null) {
                return null;
            }
            return objectMapper.writeValueAsBytes(value);
        } catch (Exception e) {
            log.error("Error while serializing object" + value, e);
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <T> T readValue(String value, Class<T> valueType) {
        return readValue(mapper(), value, valueType);
    }

    @Nullable
    public static <T> T readValue(ObjectMapper objectMapper, String value, Class<T> valueType) {
        try {
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, valueType);
        } catch (Exception e) {
            log.error("Error while deserializing object" + value + " class:" + valueType, e);
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <T> T readValue(byte[] value, Class<T> valueType) {
        return readValue(mapper(), value, valueType);
    }

    @Nullable
    public static <T> T readValue(ObjectMapper objectMapper, byte[] value, Class<T> valueType) {
        try {
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, valueType);
        } catch (Exception e) {
            log.error("Error while deserializing byte[]" + new String(value) + " class:" + valueType, e);
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <T> T readValue(byte[] value, TypeReference<T> valueTypeRef) {
        return readValue(mapper(), value, valueTypeRef);
    }

    @Nullable
    public static <T> T readValue(ObjectMapper objectMapper, byte[] value, TypeReference<T> valueTypeRef) {
        try {
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, valueTypeRef);
        } catch (Exception e) {
            log.error("Error while deserializing byte[]" + new String(value) + " " + VALUE_TYPE_REF + " : " + valueTypeRef, e);
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <T> T readValue(String value, TypeReference<T> valueTypeRef) {
        return readValue(mapper(), value, valueTypeRef);
    }

    @Nullable
    public static <T> T readValue(ObjectMapper objectMapper, String value, TypeReference<T> valueTypeRef) {
        try {
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, valueTypeRef);
        } catch (Exception e) {
            log.error("Error while deserializing value" + value + " " + VALUE_TYPE_REF + " : " + valueTypeRef, e);
            throw new RuntimeException(e);
        }
    }


    @Nullable
    public static <T> T readValue(InputStream src, Class<T> valueType) {
        return readValue(mapper(), src, valueType);
    }

    @Nullable
    public static <T> T readValue(ObjectMapper objectMapper, InputStream src, Class<T> valueType) {
        try {
            if (src == null) {
                return null;
            }
            return objectMapper.readValue(src, valueType);
        } catch (Exception e) {
            log.error("Error while deserializing inputStream valueType:" + valueType, e);
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return convertValue(mapper(), fromValue, toValueType);
    }

    @Nullable
    public static <T> T convertValue(Object fromValue, TypeReference<T> valueTypeRef) {
        return convertValue(mapper(), fromValue, valueTypeRef);
    }

    @Nullable
    public static <T> T convertValue(ObjectMapper objectMapper, Object fromValue, Class<T> toValueType) {
        try {
            if (fromValue == null) {
                return null;
            }
            return objectMapper.convertValue(fromValue, toValueType);
        } catch (Exception e) {
            log.error("Error while deserializing fromValue" + fromValue + " " + VALUE_TYPE_REF + " : " + toValueType, e);
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <T> T convertValue(ObjectMapper objectMapper, Object fromValue, TypeReference<T> valueTypeRef) {
        try {
            if (fromValue == null) {
                return null;
            }
            return objectMapper.convertValue(fromValue, valueTypeRef);
        } catch (Exception e) {
            log.error("Error while deserializing fromValue" + fromValue + " " + VALUE_TYPE_REF + " : " + valueTypeRef, e);
            throw new RuntimeException(e);
        }
    }

    public static ObjectMapper mapper() {
        Preconditions.checkNotNull(mapper, "Please call SerDe.init(mapper) to set mapper");
        return mapper;
    }

}
