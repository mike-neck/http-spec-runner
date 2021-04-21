package org.mikeneck.httpspec.impl.specs.json;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
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
}
