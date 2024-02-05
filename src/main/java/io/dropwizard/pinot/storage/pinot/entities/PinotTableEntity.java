package io.dropwizard.pinot.storage.pinot.entities;

import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PinotTableEntity {

    String tableName();

    TableType type();

    String ingestionKafkaTopicName();
}
