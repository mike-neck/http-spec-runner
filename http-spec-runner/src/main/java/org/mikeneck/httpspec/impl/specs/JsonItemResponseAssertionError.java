package org.mikeneck.httpspec.impl.specs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.HttpResponseAssertion;

public class JsonItemResponseAssertionError implements HttpResponseAssertion<JsonItem> {

  private final @NotNull String message;
  private final @NotNull String path;
  private final @NotNull JsonItem expectedItem;
  private final @NotNull JsonPathOperationException exception;

  public JsonItemResponseAssertionError(
      @NotNull String message,
      @NotNull String path,
      @NotNull JsonItem expectedItem,
      @NotNull JsonPathOperationException exception) {
    this.message = message;
    this.path = path;
    this.expectedItem = expectedItem;
    this.exception = exception;
  }

  @Override
  public @NotNull JsonItem expected() {
    return expectedItem;
  }

  @Override
  public @Nullable JsonItem actual() {
    return null;
  }

  @Override
  public @NotNull String description() {
    return String.format(
        "%s\npath: %s\nexpected: %s\nactual :\n%s",
        message, path, expectedItem.describeValue(), exception.body());
  }

  @Override
  public boolean isSuccess() {
    return false;
  }
}
