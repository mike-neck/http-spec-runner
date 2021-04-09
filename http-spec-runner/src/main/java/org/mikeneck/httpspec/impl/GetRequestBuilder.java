package org.mikeneck.httpspec.impl;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpRequestSpec;

public class GetRequestBuilder implements HttpRequestSpec {

  private final String url;
  private final Multimap queries;
  private final Multimap headers;

  public GetRequestBuilder(String url) {
    this.url = url;
    this.queries = new Multimap();
    this.headers = new Multimap();
  }

  public HttpRequest build() {
    String url;
    if (this.queries.isEmpty()) {
      url = this.url;
    } else {
      url =
          String.format(
              "%s?%s",
              this.url,
              this.queries
                  .mapNameAndValue((name, value) -> name + "=" + value)
                  .collect(Collectors.joining("&")));
    }

    HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).GET();

    this.headers
        .nameAndValues()
        .forEach(nameAndValue -> builder.header(nameAndValue.name, nameAndValue.value));

    return builder.build();
  }

  @Override
  public @NotNull HttpRequestSpec query(@NotNull String name, @NotNull String value) {
    String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
    this.queries.add(name, encodedValue);
    return this;
  }

  @Override
  public @NotNull HttpRequestSpec query(@NotNull String name, int value) {
    return query(name, Integer.toString(value));
  }

  @Override
  public @NotNull HttpRequestSpec query(@NotNull String name, long value) {
    return query(name, Long.toString(value));
  }

  @Override
  public @NotNull HttpRequestSpec header(@NotNull String name, @NotNull String... value) {
    for (String v : value) {
      headers.add(name, v);
    }
    return this;
  }
}
