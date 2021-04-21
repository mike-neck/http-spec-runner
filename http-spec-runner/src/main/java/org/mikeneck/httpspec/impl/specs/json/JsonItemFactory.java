package org.mikeneck.httpspec.impl.specs.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.specs.JsonItem;

public interface JsonItemFactory {

  @NotNull
  static JsonItem fromNode(@NotNull JsonNode node) {
    if (node.isTextual()) {
      String value = node.asText();
      return stringItem(value);
    } else if (node.isIntegralNumber()) {
      long value = node.asLong(0L);
      return intItem(value);
    } else if (node.isDouble()) {
      double value = node.asDouble(0.0d);
      return doubleItem(value);
    } else if (node.isArray()) {
      return arrayItem((ArrayNode) node);
    } else if (node.isObject()) {
      return objectItem(node);
    }
    throw new IllegalArgumentException(String.format("unsupported type [%s]", node));
  }

  @NotNull
  static StringItem stringItem(@NotNull String value) {
    return new StringItem(value);
  }

  @NotNull
  static IntItem intItem(long value) {
    return new IntItem(value);
  }

  @NotNull
  static DoubleItem doubleItem(double value) {
    return new DoubleItem(value);
  }

  @NotNull
  private static ArrayItem arrayItem(@NotNull ArrayNode node) {
    List<JsonItem> items = new ArrayList<>(node.size());
    for (JsonNode child : node) {
      JsonItem item = fromNode(child);
      items.add(item);
    }
    return new ArrayItem(items);
  }

  @NotNull
  static ArrayItem arrayItemOfString(@NotNull String... items) {
    return ArrayItem.ofString(items);
  }

  @NotNull
  static ObjectItem objectItem(@NotNull JsonNode node) {
    Iterable<Map.Entry<String, JsonNode>> items =
        () -> {
          Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
          return Objects.requireNonNullElse(iterator, Collections.emptyIterator());
        };
    Map<String, JsonItem> object = new LinkedHashMap<>(node.size());
    for (Map.Entry<String, JsonNode> item : items) {
      JsonItem value = fromNode(item.getValue());
      object.put(item.getKey(), value);
    }
    return new ObjectItem(object);
  }
}
