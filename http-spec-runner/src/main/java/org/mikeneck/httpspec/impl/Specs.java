package org.mikeneck.httpspec.impl;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.HttpResponseAssertion;

public class Specs {

  @NotNull final List<HttpElementSpec> responseSpecs;

  Specs() {
    this(new ArrayList<>());
  }

  Specs(@NotNull List<HttpElementSpec> responseSpecs) {
    this.responseSpecs = responseSpecs;
  }

  @UnmodifiableView
  @NotNull
  List<HttpResponseAssertion<?>> httpExchange(
      @NotNull GetRequestBuilder request, @NotNull Client provider) {
    try {
      HttpResponse<byte[]> httpResponse = provider.exchange(request);
      return composeResults(spec -> spec.apply(httpResponse));
    } catch (IOException e) {
      return composeResults(
          spec -> HttpResponseAssertionFactory.exception("http call", spec.description(), e));
    }
  }

  @NotNull
  @UnmodifiableView
  List<HttpResponseAssertion<?>> composeResults(
      @NotNull Function<? super HttpElementSpec, ? extends HttpResponseAssertion<?>> assertion) {
    List<HttpResponseAssertion<?>> assertions = new ArrayList<>(size());
    for (HttpElementSpec spec : this.responseSpecs) {
      assertions.add(assertion.apply(spec));
    }
    return Collections.unmodifiableList(assertions);
  }

  boolean isConfigured() {
    return !responseSpecs.isEmpty();
  }

  public int size() {
    return responseSpecs.size();
  }

  void add(Iterable<HttpElementSpec> specs) {
    for (HttpElementSpec spec : specs) {
      responseSpecs.add(spec);
    }
  }

  @NotNull
  String unconfiguredMessage() {
    StringBuilder sub = new StringBuilder();
    if (!responseSpecs.isEmpty()) {
      sub.append(' ');
      String expecting =
          responseSpecs.stream().map(HttpElementSpec::description).collect(Collectors.joining("/"));
      sub.append(expecting);
    }
    return sub.toString();
  }
}
