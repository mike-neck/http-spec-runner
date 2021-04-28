package org.mikeneck.httpspec.file.data;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.BodyAssertion;

public class Expect implements Consumer<@NotNull BodyAssertion> {

  public String type;
  public JsonNode value;

  public Expect() {}

  Expect(String type, JsonNode value) {
    this.type = type;
    this.value = value;
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("Expect{");
    sb.append("type='").append(type).append('\'');
    sb.append(", value=").append(value);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public void accept(@NotNull BodyAssertion bodyAssertion) {
    if (value.isTextual()) {
      bodyAssertion.toBe(value.textValue());
    } else if (value.isIntegralNumber()) {
      bodyAssertion.toBe(value.asLong());
    } else if (value.isFloatingPointNumber()) {
      bodyAssertion.toBe(value.asDouble());
    } else if (value.isBoolean()) {
      bodyAssertion.toBe(value.asBoolean());
    } else {
      throw new IllegalArgumentException(
          String.format("unsupported value[type=%s,value=%s]", value.getNodeType(), value));
    }
  }
}
