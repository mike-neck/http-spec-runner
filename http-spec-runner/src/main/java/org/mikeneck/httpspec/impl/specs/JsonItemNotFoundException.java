package org.mikeneck.httpspec.impl.specs;

import com.jayway.jsonpath.PathNotFoundException;
import org.jetbrains.annotations.NotNull;

public class JsonItemNotFoundException extends JsonPathOperationException {

  @NotNull private final String body;

  public JsonItemNotFoundException(String message, @NotNull String body) {
    super(message);
    this.body = body;
  }

  public JsonItemNotFoundException(PathNotFoundException e, @NotNull String body) {
    super(e.getMessage(), e);
    this.body = body;
  }

  @Override
  public @NotNull String body() {
    return body;
  }
}
