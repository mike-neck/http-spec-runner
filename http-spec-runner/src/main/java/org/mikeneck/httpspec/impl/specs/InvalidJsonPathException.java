package org.mikeneck.httpspec.impl.specs;

import com.jayway.jsonpath.InvalidPathException;
import org.jetbrains.annotations.NotNull;

public class InvalidJsonPathException extends JsonPathOperationException {

  @NotNull private final String body;

  public InvalidJsonPathException(String message, @NotNull String body) {
    super(message);
    this.body = body;
  }

  public InvalidJsonPathException(InvalidPathException e, @NotNull String body) {
    super(e.getMessage(), e);
    this.body = body;
  }

  @Override
  public @NotNull String body() {
    return body;
  }
}
