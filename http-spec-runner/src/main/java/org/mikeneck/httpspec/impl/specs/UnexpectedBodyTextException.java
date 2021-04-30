package org.mikeneck.httpspec.impl.specs;

import org.jetbrains.annotations.NotNull;

public class UnexpectedBodyTextException extends JsonPathOperationException {

  private @NotNull final String body;

  public UnexpectedBodyTextException(String message, @NotNull String body) {
    super(message);
    this.body = body;
  }

  @Override
  public @NotNull String body() {
    return body;
  }
}
