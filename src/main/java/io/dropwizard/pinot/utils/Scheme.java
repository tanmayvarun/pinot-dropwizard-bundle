package io.dropwizard.pinot.utils;

import lombok.Getter;

@Getter
public enum Scheme {
    HTTP("http"),
    HTTPS("https")
    ;

    private String scheme;

    Scheme(String scheme) {
        this.scheme = scheme;
    }
}
