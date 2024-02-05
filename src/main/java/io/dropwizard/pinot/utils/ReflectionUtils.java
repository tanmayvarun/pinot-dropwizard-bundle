package io.dropwizard.pinot.utils;

import io.dropwizard.pinot.storage.pinot.entities.PartitionKey;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

@UtilityClass
public class ReflectionUtils {

    public <T> Optional<Field> annotatedWith(Class entityClass, Class annotation) {
        return Arrays.stream(entityClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(annotation))
                .findAny();
    }

    public static void main(String[] args) {
        annotatedWith(TestClass.class, PartitionKey.class);
    }
}
