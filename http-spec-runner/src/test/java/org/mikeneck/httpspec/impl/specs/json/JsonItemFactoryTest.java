package org.mikeneck.httpspec.impl.specs.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mikeneck.httpspec.impl.specs.JsonItem;

public class JsonItemFactoryTest {

  @Test
  void stringItem() {
    JsonNode node = new TextNode("value");
    JsonItem stringItem = JsonItemFactory.fromNode(node);
    assertThat(stringItem).isInstanceOf(StringItem.class);
  }

  @Test
  void intItem() {
    JsonNode jsonNode = new IntNode(20);
    JsonItem item = JsonItemFactory.fromNode(jsonNode);
    assertThat(item).isInstanceOf(IntItem.class);
  }

  @Test
  void longToIntItem() {
    JsonNode node = new LongNode(200L);
    JsonItem item = JsonItemFactory.fromNode(node);
    assertThat(item).isInstanceOf(IntItem.class);
  }

  @Test
  void doubleToDoubleItem() {
    JsonNode node = new DoubleNode(1.25);
    JsonItem item = JsonItemFactory.fromNode(node);
    assertThat(item).isInstanceOf(DoubleItem.class);
  }

  final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void arrayItemOfString() {
    JsonNode node =
        new ArrayNode(
            objectMapper.getNodeFactory(), List.of(new TextNode("foo"), new TextNode("bar")));
    JsonItem item = JsonItemFactory.fromNode(node);
    assertThat(item).isInstanceOf(ArrayItem.class);
  }

  @Test
  void objectItem() throws IOException {
    // language=json
    JsonNode node = objectMapper.readTree("{\"name\": \"foo\",\"value\": 200}");
    JsonItem item = JsonItemFactory.fromNode(node);
    assertThat(item).isInstanceOf(ObjectItem.class);
  }

  @TestFactory
  Iterable<DynamicTest> fromObject() {
    return List.of(
        test("fromObject(String) -> StringItem")
            .jsonItem(JsonItemFactory.fromObject("test"))
            .toBeInstanceOf(StringItem.class),
        test("fromObject(int) -> IntItem")
            .jsonItem(JsonItemFactory.fromObject(1))
            .toBeInstanceOf(IntItem.class),
        test("fromObject(long) -> IntItem")
            .jsonItem(JsonItemFactory.fromObject(10L))
            .toBeInstanceOf(IntItem.class),
        test("fromObject(float) -> DoubleItem")
            .jsonItem(JsonItemFactory.fromObject(2.1f))
            .toBeInstanceOf(DoubleItem.class),
        test("fromObject(double) -> DoubleItem")
            .jsonItem(JsonItemFactory.fromObject(10.2))
            .toBeInstanceOf(DoubleItem.class),
        test("fromObject(Collection) -> ArrayItem")
            .jsonItem(JsonItemFactory.fromObject(Set.of(1, 2, 4)))
            .toBeInstanceOf(ArrayItem.class),
        test("fromObject(Map) -> ObjectItem")
            .jsonItem(JsonItemFactory.fromObject(Map.of("name", "test", "value", 20)))
            .toBeInstanceOf(ObjectItem.class));
  }

  private static JsonItemProvider test(String name) {
    return item -> klass -> dynamicTest(name, () -> assertThat(item).isInstanceOf(klass));
  }

  private interface JsonItemProvider {
    JsonItemExpected jsonItem(JsonItem item);
  }

  private interface JsonItemExpected {
    DynamicTest toBeInstanceOf(Class<? extends JsonItem> klass);
  }
}
