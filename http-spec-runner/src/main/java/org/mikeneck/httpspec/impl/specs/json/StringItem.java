package org.mikeneck.httpspec.impl.specs.json;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.specs.JsonItem;

class StringItem implements JsonItem {

  @NotNull private final String value;

  StringItem(@NotNull String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof StringItem)) return false;
    StringItem that = (StringItem) o;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("json string[");
    sb.append('\'').append(value).append('\'');
    sb.append(']');
    return sb.toString();
  }
}
