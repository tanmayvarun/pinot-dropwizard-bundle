package io.dropwizard.pinot.utils;

import com.google.common.base.Strings;
import io.dropwizard.pinot.healthcheck.configs.Endpoint;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class HttpUtils {

    public String endpoint(Endpoint endpoint, Optional<Scheme> schemeOptional) {
        StringBuilder endpointBuilder = new StringBuilder();

        if (schemeOptional.isPresent()) {
            endpointBuilder.append(String.format("%s://", schemeOptional.get().getScheme()));
        }

        endpointBuilder.append(String.format(endpoint.getHost()));

        if (!Strings.isNullOrEmpty(endpoint.getPort())) {
            endpointBuilder.append(String.format(":%s", endpoint.getPort()));
        }

        if (!Strings.isNullOrEmpty(endpoint.getUriPrefix())) {
            endpointBuilder.append(String.format("/%s", endpoint.getUriPrefix()));
        }

        return endpointBuilder.toString();
    }

}
