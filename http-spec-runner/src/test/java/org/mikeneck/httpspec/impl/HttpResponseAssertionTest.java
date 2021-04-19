package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class HttpResponseAssertionTest {

  @Test
  void success() {
    HttpResponseAssertion<Integer> httpResponseAssertion = HttpResponseAssertion.success(200);
    assertAll(
        () -> assertThat(httpResponseAssertion.expected()).isEqualTo(200),
        () -> assertThat(httpResponseAssertion.actual()).isEqualTo(200),
        () ->
            assertThat(httpResponseAssertion.description())
                .isEqualTo("expected: 200\nactual : 200"),
        () -> assertThat(httpResponseAssertion).isEqualTo(HttpResponseAssertion.success(200)));
  }

  @Test
  void failure() {
    HttpResponseAssertion<Integer> httpResponseAssertion = HttpResponseAssertion.failure(200, 404);
    assertAll(
        () -> assertThat(httpResponseAssertion.expected()).isEqualTo(200),
        () -> assertThat(httpResponseAssertion.actual()).isEqualTo(404),
        () ->
            assertThat(httpResponseAssertion.description())
                .isEqualTo("expected: 200\nactual : 404"),
        () -> assertThat(httpResponseAssertion).isNotEqualTo(HttpResponseAssertion.success(200)));
  }

  @Test
  void error() {
    Exception exception = new IOException("http error");
    HttpResponseAssertion<Integer> httpResponseAssertion =
        HttpResponseAssertion.exception(200, exception);
    assertAll(
        () -> assertThat(httpResponseAssertion.expected()).isEqualTo(200),
        () -> assertThat(httpResponseAssertion.actual()).isNull(),
        () ->
            assertThat(httpResponseAssertion.description())
                .isEqualTo("expected: 200\nbut exception: " + exception.toString()),
        () -> assertThat(httpResponseAssertion).isNotEqualTo(HttpResponseAssertion.success(200)));
  }

  @Test
  void itemFoundInCollection() {
    HttpResponseAssertion<Collection<String>> assertion =
        HttpResponseAssertion.itemFoundInCollection("foo", "bar", "foo", "baz");
    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () -> assertThat(assertion.expected()).isEqualTo(List.of("foo")),
        () -> assertThat(assertion.actual()).containsAll(Set.of("foo", "bar", "baz")),
        () -> assertThat(assertion.description()).contains("expected: to contain 'foo'"),
        () -> assertThat(assertion.description()).contains("actual : ", "bar, foo, baz"));
  }
}
