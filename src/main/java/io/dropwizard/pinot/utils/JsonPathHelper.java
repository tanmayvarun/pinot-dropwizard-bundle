package io.dropwizard.pinot.utils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class JsonPathHelper {

    private Configuration jsonPathConfiguration;

    public static void init() {
        jsonPathConfiguration = Configuration.defaultConfiguration();
    }

    public static Object parse(String jsonString) {
        return jsonPathConfiguration.jsonProvider().parse(jsonString);
    }

    public static <T> T get(Object json, String jsonPath) {
        try {
            return JsonPath.using(jsonPathConfiguration).parse(json).read(jsonPath);
        } catch (Exception ex) {
            throw PinotDaoException.error(ErrorCode.EVENT_PARSING_ERROR,
                    Map.of("json", json,
                            "path", jsonPath,
                            "error", ex.getLocalizedMessage())
            );
        }
    }

    public static <T> T getWithDefault(Object json, String jsonPath, T defaultValue) {
        try {
            return JsonPath.using(jsonPathConfiguration).parse(json).read(jsonPath);
        } catch (Exception ex) {
                return defaultValue;
        }
    }
}
