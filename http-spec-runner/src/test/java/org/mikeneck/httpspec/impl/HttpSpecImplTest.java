package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.HttpRequestMethodSpec;
import org.mikeneck.httpspec.HttpRequestSpec;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.HttpSpec;
import org.mikeneck.httpspec.ResourceFile;
import org.mikeneck.httpspec.ResourceFileLoader;
import org.mikeneck.httpspec.VerificationResult;

@ExtendWith(ResourceFileLoader.class)
class HttpSpecImplTest {

  private static final Client HTTP_CLIENT_PROVIDER =
      () -> new MockHttpClient((request, handler) -> new MockHttpResponse(200));

  @Test
  void requestReturnsHttpRequestMethodSpec() {
    HttpSpecImpl builder = new HttpSpecImpl(1);
    HttpRequestMethodSpec httpRequestMethodSpec = ((HttpSpec) builder).request();
    assertThat(httpRequestMethodSpec).isNotNull();
  }

  @Test
  void httpRequestSpecInGetMethodWillConfigured() {
    List<HttpRequestSpec> specs = new ArrayList<>();
    HttpSpecImpl builder = new HttpSpecImpl(1);
    ((HttpSpec) builder).request().get("http://example.com", specs::add);
    assertThat(specs).hasSize(1);
  }

  @Test
  void callingRunMethodWithoutConfigurationWillFail() {
    HttpSpecImpl builder = new HttpSpecImpl(1);
    assertThatThrownBy(
            () -> {
              @SuppressWarnings("unused")
              VerificationResult assertions = builder.invokeOn(HTTP_CLIENT_PROVIDER);
            })
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("http-spec-1 not configured");
  }

  @Test
  void callingRunMethodWithOnlyNameConfigurationWillFail() {
    HttpSpecImpl builder = new HttpSpecImpl(1);

    ((HttpSpec) builder).name("http-spec-test-name");

    assertThatThrownBy(
            () -> {
              @SuppressWarnings("unused")
              VerificationResult verificationResult = builder.invokeOn(HTTP_CLIENT_PROVIDER);
            })
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("http-spec-test-name");
  }

  @Test
  void callingRunMethodWithOnlyRequestConfigurationWillFail() {
    HttpSpecImpl builder = new HttpSpecImpl(1);

    ((HttpSpec) builder).request().get("https://example.com");

    assertThatThrownBy(
            () -> {
              @SuppressWarnings("unused")
              VerificationResult verificationResult = builder.invokeOn(HTTP_CLIENT_PROVIDER);
            })
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("GET https://example.com");
  }

  @Test
  void callingRunMethodWithOnlyResponseConfigurationWillFail() {
    HttpSpecImpl builder = new HttpSpecImpl(1);

    ((HttpSpec) builder).response(response -> response.status(200));

    assertThatThrownBy(
            () -> {
              @SuppressWarnings("unused")
              VerificationResult verificationResult = builder.invokeOn(HTTP_CLIENT_PROVIDER);
            })
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("expecting status=200");
  }

  @Test
  void validCallOfRunMethod() {
    HttpSpecImpl builder = new HttpSpecImpl(1);

    ((HttpSpec) builder).name("valid-call-of-run-method");
    ((HttpSpec) builder).request().get("https://example.com");
    ((HttpSpec) builder).response(response -> response.status(200));

    VerificationResult verificationResult = builder.invokeOn(HTTP_CLIENT_PROVIDER);

    assertThat(verificationResult)
        .hasSize(1)
        .first()
        .satisfies(assertion -> assertThat(assertion.isSuccess()).isTrue());
  }

  @Test
  void validCallOfRunMethodWithoutName() {
    HttpSpecImpl builder = new HttpSpecImpl(1);

    ((HttpSpec) builder).request().get("https://example.com");
    ((HttpSpec) builder).response(response -> response.status(200));

    VerificationResult verificationResult = builder.invokeOn(HTTP_CLIENT_PROVIDER);

    assertThat(verificationResult)
        .hasSize(1)
        .first()
        .satisfies(assertion -> assertThat(assertion.isSuccess()).isTrue());
  }

  @Test
  void sizeOfAssertionsIsEqualToTheSizeOfSpecsInFailure() {
    HttpSpecImpl builder = new HttpSpecImpl(1);
    Client client = () -> new MockHttpClient((request, handler) -> new MockHttpResponse(401));

    ((HttpSpec) builder).name("valid-call-of-run-method");
    ((HttpSpec) builder).request().get("https://example.com");
    ((HttpSpec) builder)
        .response(
            response -> {
              response.status(200);
              response.header("content-type", "plain/text");
              response.header("x-attr", "foo");
            });

    VerificationResult verificationResult = builder.invokeOn(client);

    assertThat(verificationResult).hasSize(3).allMatch(assertion -> !assertion.isSuccess());
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void sizeOfAssertionsIsEqualToTheSizeOfSpecsInSuccess(String jsonBody) {
    HttpSpecImpl builder = new HttpSpecImpl(1);
    Multimap multimap = new Multimap();
    multimap.add("CONTENT-TYPE", "application/json");
    multimap.add("X-ATTR", "foo");
    Client client =
        () -> new MockHttpClient((request, handler) -> new MockHttpResponse(multimap, jsonBody));

    ((HttpSpec) builder).name("valid-call-of-run-method");
    ((HttpSpec) builder).request().get("https://example.com");
    ((HttpSpec) builder)
        .response(
            response -> {
              response.status(200);
              response.header("content-type", "application/json");
              response.header("x-attr", "foo");
              response.jsonBody(
                  json -> {
                    json.path("$.firstName").toBe("John");
                    json.path("$.lastName").toBe("doe");
                  });
            });

    VerificationResult verificationResult = builder.invokeOn(client);

    assertThat(verificationResult).hasSize(5).allMatch(HttpResponseAssertion::isSuccess);
  }

  @Test
  void ioExceptionWhileRequest() {
    HttpSpecImpl builder = new HttpSpecImpl(1);
    Client client =
        () ->
            new MockHttpClient(
                (request, handler) -> {
                  throw new IOException("test error");
                });

    ((HttpSpec) builder).name("valid-call-of-run-method");
    ((HttpSpec) builder).request().get("https://example.com");
    ((HttpSpec) builder)
        .response(
            response -> {
              response.status(200);
              response.header("content-type", "plain/text");
              response.header("x-attr", "foo");
            });

    VerificationResult verificationResult = builder.invokeOn(client);

    assertThat(verificationResult).hasSize(3).allMatch(assertion -> !assertion.isSuccess());
  }

  @Test
  void interruptedExceptionWhileRequest() {
    HttpSpecImpl builder = new HttpSpecImpl(1);
    Client client =
        () ->
            new MockHttpClient(
                (request, handler) -> {
                  throw new InterruptedException("test error");
                });

    ((HttpSpec) builder).name("valid-call-of-run-method");
    ((HttpSpec) builder).request().get("https://example.com");
    ((HttpSpec) builder)
        .response(
            response -> {
              response.status(200);
              response.header("content-type", "plain/text");
              response.header("x-attr", "foo");
            });

    assertThatThrownBy(
            () -> {
              @SuppressWarnings("unused")
              VerificationResult verificationResult = builder.invokeOn(client);
            })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("interrupted");
  }
}
