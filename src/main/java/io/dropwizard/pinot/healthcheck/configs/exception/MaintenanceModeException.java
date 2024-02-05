package io.dropwizard.pinot.healthcheck.configs.exception;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class MaintenanceModeException extends RuntimeException {

    @Builder
    private MaintenanceModeException() {
        super("Dao Bundle is started in maintenance mode");
    }
}
