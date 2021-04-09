package org.mikeneck.httpspec.impl;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpRequestMethodSpec;
import org.mikeneck.httpspec.HttpRequestSpec;
import org.mikeneck.httpspec.HttpResponseSpec;
import org.mikeneck.httpspec.HttpSpec;

public class HttpSpecBuilder implements HttpSpec {
  @Override
  public void name(@NotNull String specName) {}

  @SuppressWarnings("Convert2Lambda")
  @Override
  public @NotNull HttpRequestMethodSpec request() {
    return new HttpRequestMethodSpec() {
      @Override
      public void get(
          @NotNull String url, @NotNull Consumer<@NotNull ? super HttpRequestSpec> configuration) {
        GetRequestBuilder builder = new GetRequestBuilder(url);
        configuration.accept(builder);
      }
    };
  }

  @Override
  public void response(@NotNull Consumer<@NotNull ? super HttpResponseSpec> configuration) {}
}
