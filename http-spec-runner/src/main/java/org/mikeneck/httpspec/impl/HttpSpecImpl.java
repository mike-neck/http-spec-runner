package org.mikeneck.httpspec.impl;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.HttpRequestMethodSpec;
import org.mikeneck.httpspec.HttpRequestSpec;
import org.mikeneck.httpspec.HttpResponseSpec;
import org.mikeneck.httpspec.HttpSpec;

public class HttpSpecImpl implements HttpSpec {

  private final int id;
  private @Nullable String specName = null;
  private @Nullable GetRequestBuilder request = null;
  private @NotNull final List<HttpElementSpec> responseSpecs = new ArrayList<>();

  public HttpSpecImpl(int id) {
    this.id = id;
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
    responseSpecs.addAll(specs.getSpecs());
  }

  private boolean requestSpecConfigured() {
    return request != null;
  }

  private boolean responseSpecsConfigured() {
    return !responseSpecs.isEmpty();
  }

  public List<HttpResponseAssertion<?>> run(@NotNull HttpClientProvider provider) {
    if (requestSpecConfigured() && responseSpecsConfigured()) {
      try {
        HttpClient httpClient = provider.get();
        HttpRequest httpRequest = request.build();
        HttpResponse<byte[]> httpResponse =
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        List<HttpResponseAssertion<?>> assertions = new ArrayList<>(responseSpecs.size());
        for (HttpElementSpec responseSpec : responseSpecs) {
          assertions.add(responseSpec.apply(httpResponse));
        }
        return Collections.unmodifiableList(assertions);
      } catch (IOException e) {
        List<HttpResponseAssertion<?>> assertions = new ArrayList<>(responseSpecs.size());
        for (HttpElementSpec spec : responseSpecs) {
          assertions.add(HttpResponseAssertion.exception(spec.description(), e));
        }
        return Collections.unmodifiableList(assertions);
      } catch (InterruptedException e) {
        throw new RuntimeException("interrupted", e);
      }
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
    if (!responseSpecs.isEmpty()) {
      sb.append(' ');
      String expecting =
          responseSpecs.stream().map(HttpElementSpec::description).collect(Collectors.joining("/"));
      sb.append(expecting);
    }
    return new IllegalStateException(sb.toString());
  }
}
