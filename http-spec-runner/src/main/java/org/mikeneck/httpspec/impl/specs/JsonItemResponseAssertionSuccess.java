package org.mikeneck.httpspec.impl.specs;

import java.util.Objects;
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
  public @NotNull String subtitle() {
    return String.format("json item(%s)", path);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof JsonItemResponseAssertionSuccess)) return false;
    JsonItemResponseAssertionSuccess that = (JsonItemResponseAssertionSuccess) o;
    return path.equals(that.path) && expectedItem.equals(that.expectedItem);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, expectedItem);
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("JsonItemResponseAssertionSuccess{");
    sb.append("path='").append(path).append('\'');
    sb.append(", expectedItem=").append(expectedItem);
    sb.append('}');
    return sb.toString();
  }
}
