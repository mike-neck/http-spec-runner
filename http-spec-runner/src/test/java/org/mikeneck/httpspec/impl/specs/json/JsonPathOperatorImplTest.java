package org.mikeneck.httpspec.impl.specs.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mikeneck.httpspec.ResourceFile;
import org.mikeneck.httpspec.ResourceFileLoader;
import org.mikeneck.httpspec.impl.specs.JsonItem;
import org.mikeneck.httpspec.impl.specs.JsonPathOperator;
import org.mikeneck.httpspec.impl.specs.JsonPathProduct;

@ExtendWith(ResourceFileLoader.class)
class JsonPathOperatorImplTest {

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void unknownPath(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.unknown");
    JsonPathProduct product = jsonPathOperator.read(json);

    assertAll(
        () -> assertThat(product.path()).isEqualTo("$.unknown"),
        () -> assertThat(product.get()).isEmpty());
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectedSingleValue(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.firstName");
    JsonPathProduct product = jsonPathOperator.read(json);

    assertAll(
        () -> assertThat(product.path()).isEqualTo("$.firstName"),
        () -> assertThat(product.get()).isPresent().hasValue(JsonItemFactory.stringItem("John")));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectSingleIntValue(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.age");
    JsonPathProduct product = jsonPathOperator.read(json);

    assertAll(
        () -> assertThat(product.path()).isEqualTo("$.age"),
        () -> assertThat(product.get()).isPresent().hasValue(JsonItemFactory.intItem(26L)));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectSingleDoubleValue(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.rate");
    JsonPathProduct product = jsonPathOperator.read(json);

    assertAll(
        () -> assertThat(product.path()).isEqualTo("$.rate"),
        () -> assertThat(product.get()).isPresent().hasValue(JsonItemFactory.doubleItem(30.12)));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectArrayValue(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.phoneNumbers[*].type");
    JsonPathProduct product = jsonPathOperator.read(json);

    assertAll(
        () -> assertThat(product.path()).isEqualTo("$.phoneNumbers[*].type"),
        () ->
            assertThat(product.get())
                .isPresent()
                .hasValue(JsonItemFactory.arrayItemOfString("iPhone", "home")));
  }

  @NotNull
  static JsonItem object(@NotNull Map<String, Object> map) {
    return JsonItemFactory.objectItem(map);
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectObjectValue(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.address");
    JsonPathProduct product = jsonPathOperator.read(json);

    assertAll(
        () -> assertThat(product.path()).isEqualTo("$.address"),
        () ->
            assertThat(product.get())
                .isPresent()
                .hasValue(
                    object(
                        Map.of(
                            "streetAddress", "naist street", //
                            "city", "Nara", //
                            "postalCode", "630-0192"))));
  }
}
