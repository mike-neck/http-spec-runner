package org.mikeneck.httpspec.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpSpec;
import org.mikeneck.httpspec.HttpSpecRunner;
import org.mikeneck.httpspec.HttpSpecVerifier;

public class HttpSpecRunnerBuilder implements HttpSpecRunner.Builder, Iterable<HttpSpecVerifier> {

  private final int[] count = new int[] {0};
  private final List<HttpSpecVerifier> httpSpecVerifiers = new ArrayList<>();

  @Override
  public @NotNull HttpSpecRunner build() {
    if (count[0] == 0) {
      throw new IllegalArgumentException("specs is empty");
    }
    return new HttpSpecRunnerImpl();
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
    return httpSpecVerifiers.iterator();
  }
}
