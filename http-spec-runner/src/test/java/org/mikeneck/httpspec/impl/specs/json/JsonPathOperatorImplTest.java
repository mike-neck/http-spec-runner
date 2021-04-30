package org.mikeneck.httpspec.impl.specs.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.ResourceFile;
import org.mikeneck.httpspec.impl.specs.JsonItem;
import org.mikeneck.httpspec.impl.specs.JsonItemResponseAssertionError;
import org.mikeneck.httpspec.impl.specs.JsonPathOperator;
import org.mikeneck.httpspec.impl.specs.JsonPathProduct;

@ExtendWith(ResourceFile.Loader.class)
class JsonPathOperatorImplTest {

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void unknownPath(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.unknown");
    JsonPathProduct product = jsonPathOperator.apply(json);

    JsonItem jsonItem = JsonItemFactory.stringItem("expecting but not found");
    HttpResponseAssertion<JsonItem> assertion = product.assertBy(jsonItem);

    assertAll(
        () -> assertThat(assertion.description()).contains("$.unknown"),
        () -> assertThat(assertion.expected()).isEqualTo(jsonItem),
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () ->
            assertThat(assertion.description())
                .contains("given jsonpath not found", "path: $.unknown"),
        () -> assertThat(assertion).isInstanceOf(JsonItemResponseAssertionError.class));
  }

  @Test
  void invalidJsonPath() {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("invalid json path");
    // language=json
    JsonPathProduct product = jsonPathOperator.apply("{\"item\": 200}");

    JsonItem jsonItem = JsonItemFactory.intItem(200);
    HttpResponseAssertion<JsonItem> assertion = product.assertBy(jsonItem);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () -> assertThat(assertion.expected()).isEqualTo(jsonItem),
        () -> assertThat(assertion.actual()).isNull(),
        () -> assertThat(assertion.description()).contains("failed to compile jsonpath"),
        () -> assertThat(assertion).isInstanceOf(JsonItemResponseAssertionError.class));
  }

  @Test
  void nonJsonInput() {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.item");
    JsonPathProduct product = jsonPathOperator.apply("500 Server Error");

    JsonItem jsonItem = JsonItemFactory.stringItem("response is not json");
    HttpResponseAssertion<JsonItem> assertion = product.assertBy(jsonItem);

    assertAll(
        () -> assertThat(assertion.description()).contains("$.item"),
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () -> assertThat(assertion.expected()).isEqualTo(jsonItem),
        () ->
            assertThat(assertion.description())
                .contains("body contains unknown data type", "path: $.item"),
        () -> assertThat(assertion).isInstanceOf(JsonItemResponseAssertionError.class));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectedSingleValue(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.firstName");
    JsonPathProduct product = jsonPathOperator.apply(json);

    StringItem expectedValue = JsonItemFactory.stringItem("John");
    HttpResponseAssertion<JsonItem> assertion = product.assertBy(expectedValue);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () -> assertThat(assertion.expected()).isEqualTo(expectedValue),
        () -> assertThat(assertion.actual()).isEqualTo(expectedValue));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectSingleIntValue(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.age");
    JsonPathProduct product = jsonPathOperator.apply(json);

    IntItem expectedValue = JsonItemFactory.intItem(26L);
    HttpResponseAssertion<JsonItem> assertion = product.assertBy(expectedValue);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () -> assertThat(assertion.expected()).isEqualTo(expectedValue),
        () -> assertThat(assertion.actual()).isEqualTo(expectedValue));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectSingleDoubleValue(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.rate");
    JsonPathProduct product = jsonPathOperator.apply(json);

    DoubleItem expectedValue = JsonItemFactory.doubleItem(30.12);
    HttpResponseAssertion<JsonItem> assertion = product.assertBy(expectedValue);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () -> assertThat(assertion.expected()).isEqualTo(expectedValue),
        () -> assertThat(assertion.actual()).isEqualTo(expectedValue));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectArrayValue(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.phoneNumbers[*].type");
    JsonPathProduct product = jsonPathOperator.apply(json);

    ArrayItem expectedValue = JsonItemFactory.arrayItemOfString("iPhone", "home");
    HttpResponseAssertion<JsonItem> assertion = product.assertBy(expectedValue);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () -> assertThat(assertion.expected()).isEqualTo(expectedValue),
        () -> assertThat(assertion.actual()).isEqualTo(expectedValue));
  }

  @NotNull
  static JsonItem object(@NotNull Map<String, Object> map) {
    return JsonItemFactory.objectItem(map);
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void expectObjectValue(@NotNull String json) {
    JsonPathOperator jsonPathOperator = new JsonPathOperatorImpl("$.address");
    JsonPathProduct product = jsonPathOperator.apply(json);

    JsonItem expectedValue =
        object(
            Map.of(
                "streetAddress", "naist street", //
                "city", "Nara", //
                "postalCode", "630-0192"));
    HttpResponseAssertion<JsonItem> assertion = product.assertBy(expectedValue);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () -> assertThat(assertion.expected()).isEqualTo(expectedValue),
        () -> assertThat(assertion.actual()).isEqualTo(expectedValue));
  }
}
