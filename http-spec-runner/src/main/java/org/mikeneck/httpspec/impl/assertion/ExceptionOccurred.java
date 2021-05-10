package org.mikeneck.httpspec.impl.assertion;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.HttpResponseAssertion;

public class ExceptionOccurred<@NotNull T> implements HttpResponseAssertion<T> {

  private final @NotNull String subtitle;
  private final @NotNull T expected;
  private @NotNull final Throwable throwable;

  public ExceptionOccurred(
      @NotNull String subtitle, @NotNull T expected, @NotNull Throwable throwable) {
    this.subtitle = subtitle;
    this.expected = expected;
    this.throwable = throwable;
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
  public @NotNull T expected() {
    return expected;
  }

  @Override
  public @Nullable T actual() {
    return null;
  }

  @Override
  public @NotNull String description() {
    return String.format("expected: %s\nbut exception: %s", expected, throwable);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ExceptionOccurred)) return false;
    ExceptionOccurred<?> that = (ExceptionOccurred<?>) o;
    return expected.equals(that.expected) && throwable.equals(that.throwable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expected, throwable);
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("ExceptionOccurred{");
    sb.append("expected=").append(expected);
    sb.append(", throwable=").append(throwable);
    sb.append('}');
    return sb.toString();
  }
}
