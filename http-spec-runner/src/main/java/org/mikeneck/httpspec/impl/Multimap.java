package org.mikeneck.httpspec.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class Multimap {

  @NotNull final Map<@NotNull String, @NotNull List<String>> map;

  public Multimap() {
    this(new HashMap<>());
  }

  Multimap(@NotNull Map<@NotNull String, @NotNull List<String>> map) {
    this.map = map;
  }

  public void add(@NotNull String name, @NotNull String value) {
    this.map.compute(
        name,
        (key, mayList) -> {
          if (mayList == null) {
            List<String> list = new ArrayList<>();
            list.add(value);
            return list;
          } else {
            mayList.add(value);
            return mayList;
          }
        });
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  @NotNull
  <@NotNull T> Stream<T> mapNameAndValue(
      BiFunction<? super String, ? super String, ? extends T> mappingFunction) {
    return map.entrySet().stream()
        .flatMap(
            entry ->
                entry.getValue().stream()
                    .map(value -> mappingFunction.apply(entry.getKey(), value)));
  }

  @NotNull
  Stream<NameAndValue> nameAndValues() {
    return map.entrySet().stream()
        .flatMap(
            entry ->
                entry.getValue().stream().map(value -> new NameAndValue(entry.getKey(), value)));
  }

  @Override
  public String toString() {
    return map.toString();
  }

  static class NameAndValue {
    final String name;
    final String value;

    NameAndValue(String name, String value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public String toString() {
      @SuppressWarnings("StringBufferReplaceableByString")
      final StringBuilder sb = new StringBuilder("NameAndValue{");
      sb.append("name='").append(name).append('\'');
      sb.append(", value='").append(value).append('\'');
      sb.append('}');
      return sb.toString();
    }
  }
}
