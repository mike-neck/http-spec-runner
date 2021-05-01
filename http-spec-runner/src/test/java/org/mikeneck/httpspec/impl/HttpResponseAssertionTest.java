package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.NameValuePair;

public class HttpResponseAssertionTest {

  @Test
  void success() {
    HttpResponseAssertion<Integer> httpResponseAssertion =
        HttpResponseAssertionFactory.success(200);
    assertAll(
        () -> assertThat(httpResponseAssertion.expected()).isEqualTo(200),
        () -> assertThat(httpResponseAssertion.actual()).isEqualTo(200),
        () ->
            assertThat(httpResponseAssertion.description())
                .isEqualTo("expected: 200\nactual : 200"),
        () ->
            assertThat(httpResponseAssertion).isEqualTo(HttpResponseAssertionFactory.success(200)));
  }

  @Test
  void failure() {
    HttpResponseAssertion<Integer> httpResponseAssertion =
        HttpResponseAssertionFactory.failure(200, 404);
    assertAll(
        () -> assertThat(httpResponseAssertion.expected()).isEqualTo(200),
        () -> assertThat(httpResponseAssertion.actual()).isEqualTo(404),
        () ->
            assertThat(httpResponseAssertion.description())
                .isEqualTo("expected: 200\nactual : 404"),
        () ->
            assertThat(httpResponseAssertion)
                .isNotEqualTo(HttpResponseAssertionFactory.success(200)));
  }

  @Test
  void error() {
    Exception exception = new IOException("http error");
    HttpResponseAssertion<Integer> httpResponseAssertion =
        HttpResponseAssertionFactory.exception(200, exception);
    assertAll(
        () -> assertThat(httpResponseAssertion.expected()).isEqualTo(200),
        () -> assertThat(httpResponseAssertion.actual()).isNull(),
        () ->
            assertThat(httpResponseAssertion.description())
                .isEqualTo("expected: 200\nbut exception: " + exception),
        () ->
            assertThat(httpResponseAssertion)
                .isNotEqualTo(HttpResponseAssertionFactory.success(200)));
  }

  @Test
  void pairFoundInCollection() {
    HttpResponseAssertion<Collection<NameValuePair<String>>> assertion =
        HttpResponseAssertionFactory.pairFoundInCollection(
            pair("foo", "FOO"), pair("bar", "BAR"), pair("foo", "FOO"), pair("baz", "BAZ"));
    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () -> assertThat(assertion.expected()).containsOnly(pair("foo", "FOO")),
        () ->
            assertThat(assertion.actual())
                .containsAll(Set.of(pair("foo", "FOO"), pair("bar", "BAR"), pair("baz", "BAZ"))),
        () -> assertThat(assertion.description()).contains("expected: to contain '[foo,FOO]'"),
        () ->
            assertThat(assertion.description())
                .contains("actual : ", "[bar,BAR], [foo,FOO], [baz,BAZ]"));
  }

  private static NameValuePair<String> pair(@NotNull String name, @NotNull String value) {
    return new NameValuePair<>() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public @NotNull String value() {
        return value;
      }

      @Override
      public String toString() {
        return '[' + name + ',' + value + ']';
      }

      @Override
      public int hashCode() {
        return Objects.hash(name, value);
      }

      @Override
      public boolean equals(Object obj) {
        if (obj == null) {
          return false;
        }
        if (!(obj instanceof NameValuePair)) {
          return false;
        }
        NameValuePair<?> pair = (NameValuePair<?>) obj;
        return Objects.equals(name, pair.name()) && Objects.equals(value, pair.value());
      }
    };
  }
}
