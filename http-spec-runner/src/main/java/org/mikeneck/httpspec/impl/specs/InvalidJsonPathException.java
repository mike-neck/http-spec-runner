package org.mikeneck.httpspec.impl.specs;

import org.jetbrains.annotations.NotNull;

public class InvalidJsonPathException extends JsonPathOperationException {

  private final String body;

  public InvalidJsonPathException(String message, String body) {
    super(message);
    this.body = body;
  }

  @Override
  public @NotNull String body() {
    return body;
  }
}
