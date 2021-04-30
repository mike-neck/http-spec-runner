package org.mikeneck.httpspec.impl.specs;

import org.jetbrains.annotations.NotNull;

public class JsonItemNotFoundException extends JsonPathOperationException {

  private final String body;

  public JsonItemNotFoundException(String message, String body) {
    super(message);
    this.body = body;
  }

  @Override
  public @NotNull String body() {
    return body;
  }
}
