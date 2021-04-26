package org.mikeneck.httpspec.impl;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.Extension;
import org.mikeneck.httpspec.HttpSpec;
import org.mikeneck.httpspec.HttpSpecRunner;
import org.mikeneck.httpspec.HttpSpecVerifier;

public class HttpSpecRunnerBuilder implements HttpSpecRunner.Builder, Iterable<HttpSpecVerifier> {

  private final int[] count = new int[] {0};
  private final List<NamedHttpSpecVerifier> httpSpecVerifiers = new ArrayList<>();

  @SuppressWarnings("NullableProblems")
  @Override
  public @NotNull HttpSpecRunner build() {
    return build(HttpClient::newHttpClient);
  }

  @Override
  @NotNull
  public HttpSpecRunner build(@NotNull Client client) {
    return build(client, Extension.noOp());
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public @NotNull HttpSpecRunner build(@NotNull Extension extension) {
    return build(HttpClient::newHttpClient, extension);
  }

  @Override
  public @NotNull HttpSpecRunner build(@NotNull Client client, @NotNull Extension extension) {
    if (count[0] == 0) {
      throw new IllegalArgumentException("specs is empty");
    }
    return new HttpSpecRunnerImpl(client, extension, httpSpecVerifiers);
  }

  @Override
  public void addSpec(@NotNull Consumer<@NotNull ? super HttpSpec> configuration) {
    int id = ++count[0];
    HttpSpecImpl httpSpec = new HttpSpecImpl(id);
    configuration.accept(httpSpec);
    httpSpecVerifiers.add(httpSpec);
  }

  public int size() {
    return count[0];
  }

  @NotNull
  @Override
  public Iterator<HttpSpecVerifier> iterator() {
    Iterator<NamedHttpSpecVerifier> iterator = httpSpecVerifiers.iterator();
    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public HttpSpecVerifier next() {
        return iterator.next();
      }
    };
  }
}
