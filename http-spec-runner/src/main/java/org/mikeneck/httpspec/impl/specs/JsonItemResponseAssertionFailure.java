package org.mikeneck.httpspec.impl.specs;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.HttpResponseAssertion;

public class JsonItemResponseAssertionFailure implements HttpResponseAssertion<JsonItem> {

  @NotNull final String path;
  @NotNull final JsonItem expected;
  @Nullable final JsonItem actual;

  JsonItemResponseAssertionFailure(@NotNull String path, @NotNull JsonItem expected) {
    this(path, expected, null);
  }

  JsonItemResponseAssertionFailure(
      @NotNull String path, @NotNull JsonItem expected, @Nullable JsonItem actual) {
    this.path = path;
    this.expected = expected;
    this.actual = actual;
  }

  @Override
  public @NotNull String subtitle() {
    return String.format("json item(%s)", path);
  }

  @Override
  public @NotNull JsonItem expected() {
    return expected;
  }

  @Override
  public @Nullable JsonItem actual() {
    return actual;
  }

  @Override
  public @NotNull String description() {
    if (actual == null) {
      return String.format(
          "path: %s\nexpected: %s\nactual : [not found]", path, expected.describeValue());
    } else {
      return String.format(
          "path: %s\nexpected: %s\nactual : %s",
          path, expected.describeValue(), actual.describeValue());
    }
  }

  @Override
  public boolean isSuccess() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof JsonItemResponseAssertionFailure)) return false;
    JsonItemResponseAssertionFailure that = (JsonItemResponseAssertionFailure) o;
    return path.equals(that.path)
        && expected.equals(that.expected)
        && Objects.equals(actual, that.actual);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, expected, actual);
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("JsonItemResponseAssertionFailure{");
    sb.append("path='").append(path).append('\'');
    sb.append(", expected=").append(expected);
    sb.append(", actual=").append(actual);
    sb.append('}');
    return sb.toString();
  }
}
