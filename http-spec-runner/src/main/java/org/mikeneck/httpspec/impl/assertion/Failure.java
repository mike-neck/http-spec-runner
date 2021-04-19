package org.mikeneck.httpspec.impl.assertion;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;

public class Failure<@NotNull T> implements HttpResponseAssertion<T> {

  private final @NotNull T expected;
  private final @Nullable T actual;

  public Failure(@NotNull T expected, @Nullable T actual) {
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
    if (!(o instanceof org.mikeneck.httpspec.impl.assertion.Failure)) return false;
    org.mikeneck.httpspec.impl.assertion.Failure<?> failure =
        (org.mikeneck.httpspec.impl.assertion.Failure<?>) o;
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
