package org.mikeneck.httpspec.impl;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.Extension;
import org.mikeneck.httpspec.HttpSpecRunner;
import org.mikeneck.httpspec.HttpSpecVerifier;

class HttpSpecRunnerImpl implements HttpSpecRunner {

  private final @NotNull Client client;
  private final @NotNull List<@NotNull HttpSpecVerifier> httpSpecVerifiers;
  private final @NotNull Extension extension;

  public HttpSpecRunnerImpl(
      @NotNull Client client,
      @NotNull Extension extension,
      @NotNull List<@NotNull HttpSpecVerifier> httpSpecVerifiers) {
    this.client = client;
    this.httpSpecVerifiers = httpSpecVerifiers;
    this.extension = extension;
  }

  @Override
  public void run() {
    httpSpecVerifiers.forEach(httpSpecVerifier -> httpSpecVerifier.invokeOn(client));
  }
}
