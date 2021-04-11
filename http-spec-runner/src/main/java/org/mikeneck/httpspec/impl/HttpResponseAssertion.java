package org.mikeneck.httpspec.impl;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HttpResponseAssertion<@NotNull T> {

  private final @NotNull T expected;
  private @Nullable T actual;

  HttpResponseAssertion(@NotNull T expected) {
    this.expected = expected;
  }

  public HttpResponseAssertion(@NotNull T expected, @NotNull T actual) {
    this.expected = expected;
    this.actual = actual;
  }

  @NotNull
  public T expected() {
    return expected;
  }

  @NotNull
  public T actual() {
    return Objects.requireNonNullElse(actual, expected);
  }

  @NotNull
  public String description() {
    return String.format("expected: %s\nactual : %s", expected(), actual());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HttpResponseAssertion)) return false;
    HttpResponseAssertion<?> that = (HttpResponseAssertion<?>) o;
    return expected.equals(that.expected) && Objects.equals(actual, that.actual);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expected, actual);
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("HttpResponseAssertion{");
    sb.append("expected=").append(expected);
    sb.append(", actual=").append(actual);
    sb.append('}');
    return sb.toString();
  }
}
