package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

public class HttpResponseAssertionTest {

  @Test
  void success() {
    HttpResponseAssertion<Integer> httpResponseAssertion = new HttpResponseAssertion<>(200);
    assertAll(
        () -> assertThat(httpResponseAssertion.expected()).isEqualTo(200),
        () -> assertThat(httpResponseAssertion.actual()).isEqualTo(200),
        () ->
            assertThat(httpResponseAssertion.description())
                .isEqualTo("expected: 200\nactual : 200"));
  }

  @Test
  void failure() {
    HttpResponseAssertion<Integer> httpResponseAssertion = new HttpResponseAssertion<>(200, 404);
    assertAll(
        () -> assertThat(httpResponseAssertion.expected()).isEqualTo(200),
        () -> assertThat(httpResponseAssertion.actual()).isEqualTo(404),
        () ->
            assertThat(httpResponseAssertion.description())
                .isEqualTo("expected: 200\nactual : 404"));
  }
}
