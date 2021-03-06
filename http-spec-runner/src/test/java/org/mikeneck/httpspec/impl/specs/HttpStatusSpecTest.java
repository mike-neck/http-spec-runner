package org.mikeneck.httpspec.impl.specs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.impl.HttpElementSpec;
import org.mikeneck.httpspec.impl.HttpResponseAssertionFactory;
import org.mikeneck.httpspec.impl.MockHttpResponse;

class HttpStatusSpecTest {

  @Test
  void actualIsEqualToExpected200() {
    MockHttpResponse response = new MockHttpResponse(200);
    HttpElementSpec spec = new HttpStatusSpec(200);

    HttpResponseAssertion<?> result = spec.apply(response);

    assertThat(result).isEqualTo(HttpResponseAssertionFactory.success("http status", 200));
  }

  @Test
  void actualIsEqualToExpected404() {
    MockHttpResponse response = new MockHttpResponse(404);
    HttpElementSpec spec = new HttpStatusSpec(404);

    HttpResponseAssertion<?> result = spec.apply(response);

    assertThat(result).isEqualTo(HttpResponseAssertionFactory.success("http status", 404));
  }

  @Test
  void actual404IsDifferentFromExpected200() {
    MockHttpResponse response = new MockHttpResponse(404);
    HttpElementSpec spec = new HttpStatusSpec(200);

    HttpResponseAssertion<?> result = spec.apply(response);

    assertThat(result).isEqualTo(HttpResponseAssertionFactory.failure("http status", 200, 404));
  }

  @Test
  void description() {
    HttpStatusSpec spec = new HttpStatusSpec(200);
    assertThat(spec.description()).isEqualTo("expecting status=200");
  }
}
