package org.mikeneck.httpspec.impl.specs.json;

import java.util.Objects;
import org.mikeneck.httpspec.impl.specs.JsonItem;

class IntItem implements JsonItem {

  private final long value;

  IntItem(long value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "json int[" + value + ']';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IntItem)) return false;
    IntItem intItem = (IntItem) o;
    return value == intItem.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
