package org.mikeneck.httpspec.impl.assertion;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.HttpResponseAssertion;

public class Failure<@NotNull T> implements HttpResponseAssertion<T> {

  private final @NotNull String subtitle;
  private final @NotNull T expected;
  private final @Nullable T actual;

  public Failure(@NotNull String subtitle, @NotNull T expected, @Nullable T actual) {
    this.subtitle = subtitle;
    this.expected = expected;
    this.actual = actual;
  }

  @Override
  public boolean isSuccess() {
    return false;
  }

  @Override
  public @NotNull String subtitle() {
    return subtitle;
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
    return subtitle.equals(failure.subtitle)
        && expected.equals(failure.expected)
        && Objects.equals(actual, failure.actual);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subtitle, expected, actual);
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("Failure{");
    sb.append("subtitle='").append(subtitle).append('\'');
    sb.append(", expected=").append(expected);
    sb.append(", actual=").append(actual);
    sb.append('}');
    return sb.toString();
  }
}
