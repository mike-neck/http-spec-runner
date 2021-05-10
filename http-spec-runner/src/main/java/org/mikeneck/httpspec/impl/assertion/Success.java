package org.mikeneck.httpspec.impl.assertion;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseAssertion;

public class Success<@NotNull T> implements HttpResponseAssertion<T> {

  private final @NotNull String subtitle;
  private final @NotNull T expected;

  public Success(@NotNull String subtitle, @NotNull T expected) {
    this.subtitle = subtitle;
    this.expected = expected;
  }

  @Override
  public boolean isSuccess() {
    return true;
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
    if (!(o instanceof Success)) return false;
    Success<?> success = (Success<?>) o;
    return subtitle.equals(success.subtitle) && expected.equals(success.expected);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subtitle, expected);
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("Success{");
    sb.append("subtitle='").append(subtitle).append('\'').append(", ");
    sb.append("expected=").append(expected);
    sb.append('}');
    return sb.toString();
  }
}
