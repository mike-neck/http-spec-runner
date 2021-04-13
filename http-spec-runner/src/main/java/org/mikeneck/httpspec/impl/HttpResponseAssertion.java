package org.mikeneck.httpspec.impl;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HttpResponseAssertion<@NotNull T> {

  @NotNull
  T expected();

  @Nullable
  T actual();

  @NotNull
  String description();

  @NotNull
  static <@NotNull T> HttpResponseAssertion<T> success(@NotNull T expected) {
    return new Success<>(expected);
  }

  @NotNull
  static <@NotNull T> HttpResponseAssertion<T> failure(@NotNull T expected, @Nullable T actual) {
    return new Failure<>(expected, actual);
  }

  @NotNull
  static <@NotNull T> HttpResponseAssertion<T> exception(@NotNull T expected, Throwable throwable) {
    return new ExceptionOccurred<>(expected, throwable);
  }

  boolean isSuccess();

  class Success<@NotNull T> implements HttpResponseAssertion<T> {

    private final @NotNull T expected;

    Success(@NotNull T expected) {
      this.expected = expected;
    }

    @Override
    public boolean isSuccess() {
      return true;
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
    private final @Nullable T actual;

    Failure(@NotNull T expected, @Nullable T actual) {
      this.expected = expected;
      this.actual = actual;
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    @NotNull
    public T expected() {
      return expected;
    }

    @Override
    @Nullable
    public T actual() {
      return actual;
    }

    @Override
    @NotNull
    public String description() {
      return String.format("expected: %s\nactual : %s", expected(), actual());
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Failure)) return false;
      Failure<?> failure = (Failure<?>) o;
      return expected.equals(failure.expected) && Objects.equals(actual, failure.actual);
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

  class ExceptionOccurred<@NotNull T> implements HttpResponseAssertion<T> {

    private final @NotNull T expected;
    private @NotNull final Throwable throwable;

    public ExceptionOccurred(@NotNull T expected, @NotNull Throwable throwable) {
      this.expected = expected;
      this.throwable = throwable;
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public @NotNull T expected() {
      return expected;
    }

    @Override
    public @Nullable T actual() {
      return null;
    }

    @Override
    public @NotNull String description() {
      return String.format("expected: %s\nbut exception: %s", expected, throwable.toString());
    }
  }
}
