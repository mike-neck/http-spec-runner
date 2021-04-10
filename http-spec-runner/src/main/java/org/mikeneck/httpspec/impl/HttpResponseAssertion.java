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
}
