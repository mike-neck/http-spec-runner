package org.mikeneck.httpspec.impl;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.mikeneck.httpspec.HttpRequestMethodSpec;
import org.mikeneck.httpspec.HttpRequestSpec;
import org.mikeneck.httpspec.HttpResponseSpec;
import org.mikeneck.httpspec.HttpSpec;

public class HttpSpecImpl implements HttpSpec {

  private final int id;
  private @NotNull final Specs specs;
  private @Nullable String specName = null;
  private @Nullable GetRequestBuilder request = null;

  public HttpSpecImpl(int id) {
    this(id, new Specs());
  }

  HttpSpecImpl(int id, @NotNull Specs specs) {
    this.id = id;
    this.specs = specs;
  }

  @Override
  public void name(@NotNull String specName) {
    this.specName = specName;
  }

  @SuppressWarnings("Convert2Lambda")
  @Override
  public @NotNull HttpRequestMethodSpec request() {
    return new HttpRequestMethodSpec() {
      @Override
      public void get(
          @NotNull String url, @NotNull Consumer<@NotNull ? super HttpRequestSpec> configuration) {
        request = new GetRequestBuilder(url);
        configuration.accept(request);
      }
    };
  }

  @Override
  public void response(@NotNull Consumer<@NotNull ? super HttpResponseSpec> configuration) {
    HttpResponseSpecImpl specs = new HttpResponseSpecImpl();
    configuration.accept(specs);
    this.specs.add(specs);
  }

  private boolean requestSpecConfigured() {
    return request != null;
  }

  @UnmodifiableView
  public List<HttpResponseAssertion<?>> run(@NotNull Client provider) {
    if (requestSpecConfigured() && specs.isConfigured()) {
      return specs.httpExchange(request, provider);
    }
    throw unconfigured();
  }

  private RuntimeException unconfigured() {
    StringBuilder sb = new StringBuilder(String.format("http-spec-%d not configured", id));
    if (specName != null) {
      sb.append('[').append(specName).append(']');
    }
    if (request != null) {
      sb.append('(').append(request).append(')');
    }
    if (!specs.responseSpecs.isEmpty()) {
      sb.append(' ');
      String expecting =
          specs.responseSpecs.stream()
              .map(HttpElementSpec::description)
              .collect(Collectors.joining("/"));
      sb.append(expecting);
    }
    return new IllegalStateException(sb.toString());
  }
}
