package org.mikeneck.httpspec.impl.specs.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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
    } else if (node.isBoolean()) {
      boolean value = node.asBoolean();
      return booleanItem(value);
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
  static JsonItem booleanItem(boolean value) {
    if (value) {
      return BooleanItem.TRUE;
    } else {
      return BooleanItem.FALSE;
    }
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
    return objectItem(node.size(), items, JsonItemFactory::fromNode);
  }

  @NotNull
  private static <T> ObjectItem objectItem(
      int size,
      Iterable<Map.Entry<String, T>> items,
      Function<? super T, ? extends JsonItem> converter) {
    Map<String, JsonItem> object = new LinkedHashMap<>(size);
    for (Map.Entry<String, T> item : items) {
      JsonItem value = converter.apply(item.getValue());
      object.put(item.getKey(), value);
    }
    return new ObjectItem(object);
  }

  @NotNull
  static JsonItem fromObject(@NotNull Object object) {
    if (object instanceof String) {
      return stringItem(((String) object));
    } else if (object instanceof Long || object instanceof Integer) {
      return intItem(((Number) object).longValue());
    } else if (object instanceof Double || object instanceof Float) {
      return doubleItem(((Number) object).doubleValue());
    } else if (object instanceof Boolean) {
      return booleanItem((Boolean) object);
    } else if (object instanceof Collection) {
      Collection<?> list = (Collection<?>) object;
      List<JsonItem> items = new ArrayList<>(list.size());
      for (Object o : list) {
        items.add(fromObject(o));
      }
      return new ArrayItem(List.copyOf(items));
    } else if (object instanceof Map) {
      Map<?, ?> map = (Map<?, ?>) object;
      int size = map.size();
      return objectItem(map, size);
    }
    throw new IllegalArgumentException("unsupported type " + object);
  }

  @NotNull
  private static JsonItem objectItem(Map<?, ?> map, int size) {
    Map<String, JsonItem> items = new LinkedHashMap<>(size);

    for (Map.Entry<?, ?> item : map.entrySet()) {
      items.put(item.getKey().toString(), fromObject(item.getValue()));
    }
    return new ObjectItem(Collections.unmodifiableMap(items));
  }

  static JsonItem objectItem(@NotNull Map<@NotNull String, @NotNull Object> object) {
    return objectItem(object, object.size());
  }
}
