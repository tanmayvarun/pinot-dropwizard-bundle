package io.dropwizard.pinot.auth;

import io.dropwizard.pinot.healthcheck.configs.GoLucyConfig;
import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;
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

@Slf4j
public class GoLucyClient {

  private final OkHttpClient client;

  private final String authToken;

  private final String uri;

  @Builder
  public GoLucyClient(final GoLucyConfig goLucyConfig) {
    this.authToken = goLucyConfig.getAuthToken();
    this.client = new OkHttpClient();
    this.uri = String.format("http://%s/kerberos/%s/keytab", goLucyConfig.getHost(), goLucyConfig.getPrincipal());
  }

  public void fetchKeytabToPath(final String keytabPath) throws IOException {
    log.info("Fetching keytab from go lucy");

    System.out.println("URI used for fetching keytab: " + uri);

    Request request = new Request.Builder()
      .url(uri)
      .header("Authorization", authToken)
      .build();
    try (Response response = client.newCall(request).execute()) {
      if (! response.isSuccessful()) {
        log.error("Keytab fetch response is not successful, Response code: " + response.code());
        throw PinotDaoException.error(ErrorCode.KEYTAB_FETCH_ERROR,
                Map.of("fetch uri", uri));
      }
      byte[] data = Objects.requireNonNull(response.body()).bytes();

      if (data.length == 0) {
        log.error("Response is successful but response body is empty");
        throw PinotDaoException.error(ErrorCode.KEYTAB_FETCH_ERROR,
                Map.of("fetch uri", uri));
      }
      log.info("keytab size in bytes: {}", data.length);
      Files.write(Paths.get(keytabPath), data);
      System.out.println("Keytab has been successfully fetched to path: " + keytabPath);

      //try to read file immediately after writing.


    }
  }
}