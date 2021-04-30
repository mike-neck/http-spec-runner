package org.mikeneck.httpspec.impl.specs;

import org.jetbrains.annotations.NotNull;

public abstract class JsonPathOperationException extends RuntimeException {

  public JsonPathOperationException(String message) {
    super(message);
  }

  public JsonPathOperationException(Throwable cause) {
    super(cause);
  }

  public JsonPathOperationException(String message, Throwable cause) {
    super(message, cause);
  }

  public abstract @NotNull String body();
}
