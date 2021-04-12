package org.mikeneck.httpspec.impl.specs;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import javax.net.ssl.SSLSession;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.impl.HttpElementSpec;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;

class HttpStatusSpecTest {

  @Test
  void actualIsEqualToExpected200() {
    MockHttpResponse response = new MockHttpResponse(200);
    HttpElementSpec spec = new HttpStatusSpec(200);

    HttpResponseAssertion<Integer> result = spec.apply(response);

    assertThat(result).isEqualTo(HttpResponseAssertion.success(200));
  }

  @Test
  void actualIsEqualToExpected404() {
    MockHttpResponse response = new MockHttpResponse(404);
    HttpElementSpec spec = new HttpStatusSpec(404);

    HttpResponseAssertion<Integer> result = spec.apply(response);

    assertThat(result).isEqualTo(HttpResponseAssertion.success(404));
  }

  @Test
  void actual404IsDifferentFromExpected200() {
    MockHttpResponse response = new MockHttpResponse(404);
    HttpElementSpec spec = new HttpStatusSpec(200);

    HttpResponseAssertion<Integer> result = spec.apply(response);

    assertThat(result).isEqualTo(HttpResponseAssertion.failure(200, 404));
  }

  private static class MockHttpResponse implements HttpResponse<byte[]> {

    private final int status;

    public MockHttpResponse(int status) {
      this.status = status;
    }

    @Override
    public int statusCode() {
      return status;
    }

    @Override
    public HttpRequest request() {
      return null;
    }

    @Override
    public Optional<HttpResponse<byte[]>> previousResponse() {
      return Optional.empty();
    }

    @Override
    public HttpHeaders headers() {
      return null;
    }

    @Override
    public byte[] body() {
      return new byte[0];
    }

    @Override
    public Optional<SSLSession> sslSession() {
      return Optional.empty();
    }

    @Override
    public URI uri() {
      return null;
    }

    @Override
    public HttpClient.Version version() {
      return null;
    }
  }
}
