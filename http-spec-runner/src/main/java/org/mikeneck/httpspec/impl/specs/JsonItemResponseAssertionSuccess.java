package org.mikeneck.httpspec.impl.specs;

import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseAssertion;

public class JsonItemResponseAssertionSuccess implements HttpResponseAssertion<@NotNull JsonItem> {

  private @NotNull final String path;
  private final @NotNull JsonItem expectedItem;

  public JsonItemResponseAssertionSuccess(@NotNull String path, @NotNull JsonItem expectedItem) {
    this.path = path;
    this.expectedItem = expectedItem;
  }

  @Override
  public @NotNull JsonItem expected() {
    return expectedItem;
  }

  @Override
  public @NotNull JsonItem actual() {
    return expectedItem;
  }

  @Override
  public @NotNull String description() {
    String expected = expectedItem.describeValue();
    return String.format("path: %s\nexpected: %s\nactual : %s", path, expected, expected);
  }

  @Override
  public boolean isSuccess() {
    return true;
  }
}
