package org.mikeneck.httpspec.impl.specs.json;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.specs.JsonItem;

class ArrayItem implements JsonItem {

  @NotNull private final List<@NotNull JsonItem> items;

  ArrayItem(@NotNull List<@NotNull JsonItem> items) {
    this.items = items;
  }

  @Override
  public String describeValue() {
    return items.stream().map(JsonItem::describeValue).collect(Collectors.joining(",", "[", "]"));
  }

  @NotNull
  public static ArrayItem ofString(@NotNull String... items) {
    List<JsonItem> list =
        Arrays.stream(items)
            .<JsonItem>map(StringItem::new)
            .collect(Collectors.toUnmodifiableList());
    return new ArrayItem(list);
  }

  @Override
  public String toString() {
    return "json array [" + "items=" + items + ']';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ArrayItem)) return false;
    ArrayItem arrayItem = (ArrayItem) o;
    return items.equals(arrayItem.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items);
  }
}
