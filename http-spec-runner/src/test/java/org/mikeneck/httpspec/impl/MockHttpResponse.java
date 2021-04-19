package org.mikeneck.httpspec.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import javax.net.ssl.SSLSession;
import org.jetbrains.annotations.NotNull;

public class MockHttpResponse implements HttpResponse<byte[]> {

  private final int status;
  private final HttpHeaders headers;

  public MockHttpResponse(int status) {
    this.status = status;
    this.headers = HttpHeaders.of(Map.of(), (name, value) -> true);
  }

  public MockHttpResponse(@NotNull Multimap multimap) {
    this.status = 200;
    this.headers = HttpHeaders.of(multimap.map, (name, value) -> true);
  }

  @Override
  public int statusCode() {
    return status;
  }

  @Override
  public HttpRequest request() {
    return null;
  }

  @Override
  public Optional<HttpResponse<byte[]>> previousResponse() {
    return Optional.empty();
  }

  @Override
  public HttpHeaders headers() {
    return headers;
  }

  @Override
  public byte[] body() {
    return new byte[0];
  }

  @Override
  public Optional<SSLSession> sslSession() {
    return Optional.empty();
  }

  @Override
  public URI uri() {
    return null;
  }

  @Override
  public HttpClient.Version version() {
    return null;
  }
}
