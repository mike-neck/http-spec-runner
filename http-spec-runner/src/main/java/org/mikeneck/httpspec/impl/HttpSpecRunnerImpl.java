package org.mikeneck.httpspec.impl;

import java.util.List;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.HttpSpecRunner;
import org.mikeneck.httpspec.HttpSpecVerifier;

class HttpSpecRunnerImpl implements HttpSpecRunner {

  private final Client client;
  private final List<HttpSpecVerifier> httpSpecVerifiers;

  public HttpSpecRunnerImpl(Client client, List<HttpSpecVerifier> httpSpecVerifiers) {
    this.client = client;
    this.httpSpecVerifiers = httpSpecVerifiers;
  }

  @Override
  public void run() {
    httpSpecVerifiers.forEach(httpSpecVerifier -> httpSpecVerifier.invokeOn(client));
  }
}
