package org.mikeneck.httpspec.impl;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public interface HttpResponseAssertion<@NotNull T> {

  @NotNull
  T expected();

  @NotNull
  T actual();

  @NotNull
  String description();

  static <@NotNull T> HttpResponseAssertion<T> success(@NotNull T expected) {
    return new Success<>(expected);
  }

  static <@NotNull T> HttpResponseAssertion<T> failure(@NotNull T expected, @NotNull T actual) {
    return new Failure<>(expected, actual);
  }

  class Success<@NotNull T> implements HttpResponseAssertion<T> {

    private final @NotNull T expected;

    Success(@NotNull T expected) {
      this.expected = expected;
    }

    @Override
    @NotNull
    public T expected() {
      return expected;
    }

    @Override
    @NotNull
    public T actual() {
      return expected;
    }

    @Override
    @NotNull
    public String description() {
      return String.format("expected: %s\nactual : %s", expected(), actual());
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof HttpResponseAssertion.Success)) return false;
      Success<?> that = (Success<?>) o;
      return expected.equals(that.expected);
    }

    @Override
    public int hashCode() {
      return Objects.hash(expected);
    }

    @Override
    public String toString() {
      @SuppressWarnings("StringBufferReplaceableByString")
      final StringBuilder sb = new StringBuilder("Success{");
      sb.append("expected=").append(expected);
      sb.append('}');
      return sb.toString();
    }
  }

  class Failure<@NotNull T> implements HttpResponseAssertion<T> {

    private final @NotNull T expected;
    private final @NotNull T actual;

    Failure(@NotNull T expected, @NotNull T actual) {
      this.expected = expected;
      this.actual = actual;
    }

    @Override
    @NotNull
    public T expected() {
      return expected;
    }

    @Override
    @NotNull
    public T actual() {
      return Objects.requireNonNullElse(actual, expected);
    }

    @Override
    @NotNull
    public String description() {
      return String.format("expected: %s\nactual : %s", expected(), actual());
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof HttpResponseAssertion.Failure)) return false;
      Failure<?> that = (Failure<?>) o;
      return expected.equals(that.expected) && Objects.equals(actual, that.actual);
    }

    @Override
    public int hashCode() {
      return Objects.hash(expected, actual);
    }

    @Override
    public String toString() {
      @SuppressWarnings("StringBufferReplaceableByString")
      final StringBuilder sb = new StringBuilder("Failure{");
      sb.append("expected=").append(expected);
      sb.append(", actual=").append(actual);
      sb.append('}');
      return sb.toString();
    }
  }
}
