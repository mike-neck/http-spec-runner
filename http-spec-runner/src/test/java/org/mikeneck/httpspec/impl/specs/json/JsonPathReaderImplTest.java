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
import org.mikeneck.httpspec.impl.specs.JsonPathProduct;
import org.mikeneck.httpspec.impl.specs.JsonPathReader;

@ExtendWith(ResourceFileLoader.class)
class JsonPathReaderImplTest {

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void unknownPath(@NotNull String json) {
    JsonPathReader jsonPathReader = new JsonPathReaderImpl("$.unknown");
    JsonPathProduct product = jsonPathReader.read(json);

    assertAll(
        () -> assertThat(product.path()).isEqualTo("$.unknown"),
        () -> assertThat(product.get()).isEmpty());
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectedSingleValue(@NotNull String json) {
    JsonPathReader jsonPathReader = new JsonPathReaderImpl("$.firstName");
    JsonPathProduct product = jsonPathReader.read(json);

    assertAll(
        () -> assertThat(product.path()).isEqualTo("$.firstName"),
        () -> assertThat(product.get()).isPresent().hasValue(JsonItemFactory.stringItem("John")));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectSingleIntValue(@NotNull String json) {
    JsonPathReader jsonPathReader = new JsonPathReaderImpl("$.age");
    JsonPathProduct product = jsonPathReader.read(json);

    assertAll(
        () -> assertThat(product.path()).isEqualTo("$.age"),
        () -> assertThat(product.get()).isPresent().hasValue(JsonItemFactory.intItem(26L)));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectSingleDoubleValue(@NotNull String json) {
    JsonPathReader jsonPathReader = new JsonPathReaderImpl("$.rate");
    JsonPathProduct product = jsonPathReader.read(json);

    assertAll(
        () -> assertThat(product.path()).isEqualTo("$.rate"),
        () -> assertThat(product.get()).isPresent().hasValue(JsonItemFactory.doubleItem(30.12)));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectArrayValue(@NotNull String json) {
    JsonPathReader jsonPathReader = new JsonPathReaderImpl("$.phoneNumbers[*].type");
    JsonPathProduct product = jsonPathReader.read(json);

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
    JsonPathReader jsonPathReader = new JsonPathReaderImpl("$.address");
    JsonPathProduct product = jsonPathReader.read(json);

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
