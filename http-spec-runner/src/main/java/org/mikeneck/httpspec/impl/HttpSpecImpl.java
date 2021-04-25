package org.mikeneck.httpspec.impl;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.HttpRequestMethodSpec;
import org.mikeneck.httpspec.HttpRequestSpec;
import org.mikeneck.httpspec.HttpResponseSpec;
import org.mikeneck.httpspec.HttpSpec;
import org.mikeneck.httpspec.HttpSpecVerifier;
import org.mikeneck.httpspec.VerificationResult;

public class HttpSpecImpl implements HttpSpec, HttpSpecVerifier {

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

  @Override
  public @NotNull VerificationResult invokeOn(@NotNull Client client) {
    if (requestSpecConfigured() && specs.isConfigured()) {
      @NotNull String specName = this.specName == null ? defaultName() : this.specName;
      List<HttpResponseAssertion<?>> assertions = specs.httpExchange(request, client);
      return new VerificationResultImpl(specName, assertions);
    }
    throw unconfigured();
  }

  private @NotNull String defaultName() {
    return String.format("http-spec-%d", id);
  }

  private RuntimeException unconfigured() {
    StringBuilder sb = new StringBuilder(String.format("%s not configured", defaultName()));
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
