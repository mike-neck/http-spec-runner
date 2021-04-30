package org.mikeneck.httpspec.impl.specs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.impl.specs.json.JsonItemFactory;

class JsonPathProductTest {

  private static JsonPathProduct jsonPathProduct(Supplier<@NotNull JsonItem> jsonItem) {
    return new JsonPathProduct() {
      @Override
      public @NotNull String path() {
        return "$.test";
      }

      @Override
      public @NotNull JsonItem get() {
        return jsonItem.get();
      }
    };
  }

  @Test
  void actualItemIsDifferentFromExpected() {
    JsonPathProduct jsonPathProduct = jsonPathProduct(() -> JsonItemFactory.intItem(200L));

    HttpResponseAssertion<@NotNull JsonItem> assertion =
        jsonPathProduct.assertBy(JsonItemFactory.stringItem("item"));

    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () -> assertThat(assertion.actual()).isEqualTo(JsonItemFactory.intItem(200L)),
        () -> assertThat(assertion.expected()).isEqualTo(JsonItemFactory.stringItem("item")));
  }

  @Test
  void actualItemIsEqualToExpectedItem() {
    JsonPathProduct jsonPathProduct = jsonPathProduct(() -> JsonItemFactory.stringItem("item"));

    HttpResponseAssertion<@NotNull JsonItem> assertion =
        jsonPathProduct.assertBy(JsonItemFactory.stringItem("item"));

    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () -> assertThat(assertion.actual()).isEqualTo(JsonItemFactory.stringItem("item")),
        () -> assertThat(assertion.expected()).isEqualTo(JsonItemFactory.stringItem("item")));
  }

  @Test
  void invalidBody() {
    JsonPath jsonPath = JsonPath.compile("$.test");
    String responseBody = "Server Error";
    assertThatThrownBy(
            () ->
                jsonPath.read(
                    responseBody,
                    Configuration.builder()
                        .jsonProvider(new JacksonJsonNodeJsonProvider())
                        .mappingProvider(new JacksonMappingProvider())
                        .build()))
        .isInstanceOf(InvalidJsonException.class);
  }

  @Test
  void invalidPath() {
    String invalidJsonPath = "invalid json path";
    assertThatThrownBy(() -> JsonPath.compile(invalidJsonPath))
        .isInstanceOf(InvalidPathException.class);
  }

  @Test
  void notFound() {
    JsonPath jsonPath = JsonPath.compile("$.name");
    assertThatThrownBy(
            () ->
                jsonPath.read(
                    "{\"test\": 200}",
                    Configuration.builder()
                        .jsonProvider(new JacksonJsonNodeJsonProvider())
                        .mappingProvider(new JacksonMappingProvider())
                        .build()))
        .isInstanceOf(PathNotFoundException.class);
  }

  @Test
  void unexpectedBodyTextExceptionIsThrown() {
    String jsonBody = "Server Error";
    JsonPathProduct jsonPathProduct =
        jsonPathProduct(
            () -> {
              throw new UnexpectedBodyTextException("unknown data type", jsonBody);
            });

    HttpResponseAssertion<@NotNull JsonItem> assertion =
        jsonPathProduct.assertBy(JsonItemFactory.stringItem("item"));

    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () -> assertThat(assertion.actual()).isNull(),
        () ->
            assertThat(assertion.description())
                .contains("path: $.test", jsonBody, "body contains unknown data type"),
        () -> assertThat(assertion.expected()).isEqualTo(JsonItemFactory.stringItem("item")));
  }

  @Test
  void invalidJsonPathExceptionIsThrown() {
    // language=json
    String jsonBody = "{\"name\": \"test\"}";
    JsonPathProduct jsonPathProduct =
        jsonPathProduct(
            () -> {
              throw new InvalidJsonPathException("invalid path", jsonBody);
            });

    HttpResponseAssertion<@NotNull JsonItem> assertion =
        jsonPathProduct.assertBy(JsonItemFactory.stringItem("item"));

    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () -> assertThat(assertion.actual()).isNull(),
        () ->
            assertThat(assertion.description())
                .contains("path: $.test", jsonBody, "failed to compile jsonpath"),
        () -> assertThat(assertion.expected()).isEqualTo(JsonItemFactory.stringItem("item")));
  }

  @Test
  void jsonPathNotFoundExceptionIsThrown() {
    // language=json
    String jsonBody = "{\"name\": \"test\"}";
    JsonPathProduct jsonPathProduct =
        jsonPathProduct(
            () -> {
              throw new JsonItemNotFoundException("item not found", jsonBody);
            });

    HttpResponseAssertion<@NotNull JsonItem> assertion =
        jsonPathProduct.assertBy(JsonItemFactory.stringItem("item"));

    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () -> assertThat(assertion.actual()).isNull(),
        () ->
            assertThat(assertion.description())
                .contains("path: $.test", jsonBody, "given jsonpath not found"),
        () -> assertThat(assertion.expected()).isEqualTo(JsonItemFactory.stringItem("item")));
  }
}
