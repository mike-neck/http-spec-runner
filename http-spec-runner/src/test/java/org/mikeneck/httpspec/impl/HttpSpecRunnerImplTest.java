package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.Extension;
import org.mikeneck.httpspec.HttpSpecVerifier;
import org.mikeneck.httpspec.VerificationResult;

class HttpSpecRunnerImplTest {

  @Test
  void shouldUseClientGetByClient() {
    HttpClient httpClient = new MockHttpClient((request, handler) -> new MockHttpResponse(200));

    Client client = () -> httpClient;

    List<Client> clients = new ArrayList<>();
    List<HttpSpecVerifier> verifiers =
        List.of(
            c -> {
              clients.add(client);
              return new MockVerificationResult("test-1", HttpResponseAssertion.success(200));
            },
            c -> {
              clients.add(c);
              return new MockVerificationResult("test-2", HttpResponseAssertion.success(200));
            });

    HttpSpecRunnerImpl httpSpecRunner = new HttpSpecRunnerImpl(client, Extension.noOp(), verifiers);

    httpSpecRunner.run();

    assertThat(clients).hasSize(2);
  }

  static class MockVerificationResult implements VerificationResult {

    final String specName;
    final List<HttpResponseAssertion<?>> assertions;

    MockVerificationResult(String specName, HttpResponseAssertion<?>... assertions) {
      this.specName = specName;
      this.assertions = List.of(assertions);
    }

    @Override
    public @NotNull String specName() {
      return specName;
    }

    @Override
    public @UnmodifiableView @NotNull List<HttpResponseAssertion<?>> allAssertions() {
      return assertions;
    }
  }
}
