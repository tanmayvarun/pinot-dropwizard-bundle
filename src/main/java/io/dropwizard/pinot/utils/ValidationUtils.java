package io.dropwizard.pinot.utils;

import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;

import java.util.Collections;
import java.util.Objects;

public class ValidationUtils {

    public static String isNotBlank(String s) {
        if (org.apache.commons.lang3.StringUtils.isBlank(s)) {
            throw PinotDaoException.error(ErrorCode.NULL_PARAM_NOT_ALLOWED,
                    Collections.emptyMap());
        }

        return s;
    }

    public static <T> T isNotNull(T t) {
        if (Objects.isNull(t)) {
            throw PinotDaoException.error(ErrorCode.NULL_PARAM_NOT_ALLOWED,
                    Collections.emptyMap());
        }

        return t;
    }

}
