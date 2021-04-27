package org.mikeneck.httpspec.file.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjLongConsumer;
import org.junit.jupiter.api.Test;

class ObjectNodeOperatorTest {

  private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

  @Test
  void doOnString() {
    ObjectNode node =
        new ObjectNode(objectMapper.getNodeFactory(), Map.of("name", new TextNode("value")));
    ObjectNodeOperator operator = new ObjectNodeOperator(node);
    Map<String, String> map = new HashMap<>();
    BiConsumer<String, String> doOnString = map::put;
    ObjectNodeOperator.Configured operation = operator.doOnString(doOnString);

    operation.execute();

    assertThat(map).hasSize(1).containsEntry("name", "value");
  }

  @Test
  void doOnStringWith2Items() {
    ObjectNode node =
        new ObjectNode(
            objectMapper.getNodeFactory(),
            Map.of("name", new TextNode("value"), "another", new TextNode("item")));
    ObjectNodeOperator operator = new ObjectNodeOperator(node);
    Map<String, String> map = new HashMap<>();
    BiConsumer<String, String> doOnString = map::put;
    ObjectNodeOperator.Configured operation = operator.doOnString(doOnString);
    operation.execute();

    assertThat(map).hasSize(2).containsEntry("name", "value").containsEntry("another", "item");
  }

  @Test
  void doOnStringShouldNotAcceptIntValue() {
    ObjectNode node =
        new ObjectNode(
            objectMapper.getNodeFactory(),
            Map.of(
                "name", new TextNode("value"),
                "another", new TextNode("item"),
                "int-value", new IntNode(20)));
    ObjectNodeOperator operator = new ObjectNodeOperator(node);
    Map<String, String> map = new HashMap<>();
    BiConsumer<String, String> doOnString = map::put;
    ObjectNodeOperator.Configured operation = operator.doOnString(doOnString);

    operation.execute();

    assertThat(map).hasSize(2).containsEntry("name", "value").containsEntry("another", "item");
  }

  @Test
  void doOnInt() {
    ObjectNode node =
        new ObjectNode(objectMapper.getNodeFactory(), Map.of("name", new LongNode(20L)));
    ObjectNodeOperator operator = new ObjectNodeOperator(node);
    Map<String, Long> map = new HashMap<>();
    ObjLongConsumer<String> doOnInt = map::put;
    ObjectNodeOperator.IntOperator intOperator =
        operator.doOnString(
            (k, v) -> {
              throw new IllegalStateException();
            });
    ObjectNodeOperator.Configured operation = intOperator.doOnInt(doOnInt);

    operation.execute();

    assertThat(map).hasSize(1).containsEntry("name", 20L);
  }

  @Test
  void doOnIntAcceptsIntValue() {
    ObjectNode node =
        new ObjectNode(objectMapper.getNodeFactory(), Map.of("name", new IntNode(20)));
    ObjectNodeOperator operator = new ObjectNodeOperator(node);
    Map<String, Long> map = new HashMap<>();
    ObjLongConsumer<String> doOnInt = map::put;
    ObjectNodeOperator.IntOperator intOperator =
        operator.doOnString(
            (k, v) -> {
              throw new IllegalStateException();
            });
    ObjectNodeOperator.Configured operation = intOperator.doOnInt(doOnInt);

    operation.execute();

    assertThat(map).hasSize(1).containsEntry("name", 20L);
  }

  @Test
  void doOnDouble() {
    ObjectNode node =
        new ObjectNode(objectMapper.getNodeFactory(), Map.of("name", new DoubleNode(10.250)));
    ObjectNodeOperator operator = new ObjectNodeOperator(node);
    Map<String, Double> map = new HashMap<>();
    ObjDoubleConsumer<String> doOnDouble = map::put;
    ObjectNodeOperator.IntOperator intOperator =
        operator.doOnString(
            (k, v) -> {
              throw new IllegalStateException();
            });
    ObjectNodeOperator.DoubleOperator doubleOperator =
        intOperator.doOnInt(
            (k, v) -> {
              throw new IllegalStateException();
            });
    ObjectNodeOperator.Configured operation = doubleOperator.doOnDouble(doOnDouble);

    operation.execute();

    assertThat(map).hasSize(1).containsEntry("name", 10.250);
  }

  @Test
  void doOnBoolean() {
    ObjectNode node =
        new ObjectNode(objectMapper.getNodeFactory(), Map.of("name", BooleanNode.getTrue()));
    ObjectNodeOperator operator = new ObjectNodeOperator(node);
    Map<String, String> map = new HashMap<>();
    ObjectNodeOperator.BooleanOperation doOnBoolean =
        (name, value) -> {
          if (value) map.put(name, name);
          else map.put(name, "");
        };
    ObjectNodeOperator.IntOperator intOperator =
        operator.doOnString(
            (k, v) -> {
              throw new IllegalStateException();
            });
    ObjectNodeOperator.DoubleOperator doubleOperator =
        intOperator.doOnInt(
            (k, v) -> {
              throw new IllegalStateException();
            });
    ObjectNodeOperator.BooleanOperator booleanOperator =
        doubleOperator.doOnDouble(
            (k, v) -> {
              throw new IllegalStateException();
            });
    ObjectNodeOperator.Configured operation = booleanOperator.doOnBoolean(doOnBoolean);

    operation.execute();

    assertThat(map).hasSize(1).containsEntry("name", "name");
  }
}
