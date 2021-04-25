package org.mikeneck.httpspec;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.GetRequestBuilder;

public interface Client {

  @NotNull
  HttpClient get();

  default @NotNull HttpResponse<byte[]> exchange(@NotNull GetRequestBuilder request)
      throws IOException {
    try {
      HttpClient httpClient = get();
      HttpRequest httpRequest = request.build();
      return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    } catch (InterruptedException e) {
      throw new RuntimeException("interrupted", e);
    }
  }
}
