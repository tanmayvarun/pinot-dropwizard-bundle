package io.dropwizard.pinot.services.auth;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

public interface GoLucyClient {
  void fetchKeytabToPath(final String keytabPath) throws IOException;
}