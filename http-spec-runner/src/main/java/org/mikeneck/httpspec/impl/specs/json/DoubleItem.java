package org.mikeneck.httpspec.impl.specs.json;

import java.util.Objects;
import org.mikeneck.httpspec.impl.specs.JsonItem;

public class DoubleItem implements JsonItem {

  private final double value;

  public DoubleItem(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "json double[" + value + ']';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DoubleItem)) return false;
    DoubleItem that = (DoubleItem) o;
    return Double.compare(that.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
