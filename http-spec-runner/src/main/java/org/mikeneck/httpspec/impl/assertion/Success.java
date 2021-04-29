package org.mikeneck.httpspec.impl.assertion;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseAssertion;

public class Success<@NotNull T> implements HttpResponseAssertion<T> {

  private final @NotNull T expected;

  public Success(@NotNull T expected) {
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
    if (!(o instanceof org.mikeneck.httpspec.impl.assertion.Success)) return false;
    org.mikeneck.httpspec.impl.assertion.Success<?> that =
        (org.mikeneck.httpspec.impl.assertion.Success<?>) o;
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
