package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.Extension;
import org.mikeneck.httpspec.VerificationResult;

class HttpSpecRunnerImplTest {

  static NamedHttpSpecVerifier namedHttpSpecVerifier(
      String name, Consumer<? super Client> operation) {
    return new NamedHttpSpecVerifier() {
      @Override
      public @NotNull VerificationResult invokeOn(@NotNull Client client) {
        operation.accept(client);
        return new MockVerificationResult(name, HttpResponseAssertion.success(200));
      }

      @Override
      public @NotNull String specName() {
        return name;
      }
    };
  }

  @Test
  void shouldUseClientGetByClient() {
    HttpClient httpClient = new MockHttpClient((request, handler) -> new MockHttpResponse(200));

    Client client = () -> httpClient;

    List<Client> clients = new ArrayList<>();
    List<NamedHttpSpecVerifier> verifiers =
        List.of(
            namedHttpSpecVerifier("test-1", clients::add),
            namedHttpSpecVerifier("test-2", clients::add));

    HttpSpecRunnerImpl httpSpecRunner = new HttpSpecRunnerImpl(client, Extension.noOp(), verifiers);

    httpSpecRunner.run();

    assertThat(clients).hasSize(2);
  }

  @Test
  void extensionShouldBeUsedWhenRunForResultIsExecuted() {
    HttpClient httpClient = new MockHttpClient((request, handler) -> new MockHttpResponse(200));

    Client client = () -> httpClient;

    List<NamedHttpSpecVerifier> verifiers =
        List.of(namedHttpSpecVerifier("test-1", c -> {}), namedHttpSpecVerifier("test-2", c -> {}));

    List<String> list = new ArrayList<>();
    Extension extension =
        Extension.builder()
            .onCallBeforeAllSpecs(names -> names.forEach(specName -> list.add(specName.specName())))
            .onCallBeforeEachSpecs(specName -> list.add(specName.specName()))
            .onCallAfterEachSpecs(result -> list.add(result.specName()))
            .onCallAfterAllSpecs(results -> results.forEach(result -> list.add(result.specName())))
            .build();

    HttpSpecRunnerImpl httpSpecRunner = new HttpSpecRunnerImpl(client, extension, verifiers);

    List<@NotNull VerificationResult> results = httpSpecRunner.runForResult();

    assertAll(() -> assertThat(results).hasSize(2), () -> assertThat(list).hasSize(8));
  }

  @Test
  void extensionShouldBeUsedWhenRunIsExecuted() {
    HttpClient httpClient = new MockHttpClient((request, handler) -> new MockHttpResponse(200));

    Client client = () -> httpClient;

    List<NamedHttpSpecVerifier> verifiers =
        List.of(namedHttpSpecVerifier("test-1", c -> {}), namedHttpSpecVerifier("test-2", c -> {}));

    List<String> list = new ArrayList<>();
    Extension extension =
        Extension.builder()
            .onCallBeforeAllSpecs(names -> names.forEach(specName -> list.add(specName.specName())))
            .onCallBeforeEachSpecs(specName -> list.add(specName.specName()))
            .onCallAfterEachSpecs(result -> list.add(result.specName()))
            .onCallAfterAllSpecs(results -> results.forEach(result -> list.add(result.specName())))
            .build();

    HttpSpecRunnerImpl httpSpecRunner = new HttpSpecRunnerImpl(client, extension, verifiers);

    httpSpecRunner.run();

    assertAll(() -> assertThat(list).hasSize(8));
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
