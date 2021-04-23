package org.mikeneck.httpspec.impl.specs.json;

import java.util.Objects;
import org.mikeneck.httpspec.impl.specs.JsonItem;

class BooleanItem implements JsonItem {

  private final boolean value;

  static final JsonItem TRUE = new BooleanItem(true);
  static final JsonItem FALSE = new BooleanItem(false);

  BooleanItem(boolean value) {
    this.value = value;
  }

  @Override
  public String describeValue() {
    return value ? "true" : "false";
  }

  @Override
  public String toString() {
    return "json item boolean [" + "value=" + value + ']';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BooleanItem)) return false;
    BooleanItem that = (BooleanItem) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
