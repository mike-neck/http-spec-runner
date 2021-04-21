package org.mikeneck.httpspec.impl.specs.json;

import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.specs.JsonItem;

public class ObjectItem implements JsonItem {

  @NotNull private final Map<@NotNull String, @NotNull JsonItem> object;

  public ObjectItem(@NotNull Map<@NotNull String, @NotNull JsonItem> object) {
    this.object = object;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ObjectItem)) return false;
    ObjectItem that = (ObjectItem) o;
    return object.equals(that.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object);
  }

  @Override
  public String toString() {
    return "json object [" + "object=" + object + ']';
  }
}
