package org.mikeneck.httpspec.impl.specs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.http.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mikeneck.httpspec.ResourceFile;
import org.mikeneck.httpspec.ResourceFileLoader;
import org.mikeneck.httpspec.impl.HttpElementSpec;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;
import org.mikeneck.httpspec.impl.MockHttpResponse;
import org.mikeneck.httpspec.impl.specs.json.JsonItemFactory;

@ExtendWith(ResourceFileLoader.class)
class HttpBodyJsonSpecTest {

  @ResourceFile("http-body-json-spec-test/single-entry-json.json")
  @Test
  void singleEntryJson(@NotNull String jsonBody) {
    HttpElementSpec spec = new HttpBodyJsonSpec("$.name", JsonItemFactory.stringItem("test"));
    HttpResponse<byte[]> httpResponse = new MockHttpResponse(jsonBody);

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () -> assertThat(assertion.expected()).isEqualTo(JsonItemFactory.stringItem("test")),
        () -> assertThat(assertion.actual()).isEqualTo(JsonItemFactory.stringItem("test")));
  }

  @ResourceFile("http-body-json-spec-test/single-entry-json.json")
  @Test
  void singleEntryJsonNotMatching(@NotNull String jsonBody) {
    HttpElementSpec spec =
        new HttpBodyJsonSpec("$.name", JsonItemFactory.stringItem("different value"));
    HttpResponse<byte[]> httpResponse = new MockHttpResponse(jsonBody);

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () ->
            assertThat(assertion.expected())
                .isEqualTo(JsonItemFactory.stringItem("different value")),
        () -> assertThat(assertion.actual()).isEqualTo(JsonItemFactory.stringItem("test")));
  }

  @ResourceFile("http-body-json-spec-test/single-entry-json.json")
  @Test
  void singleEntryJsonNotExisting(@NotNull String jsonBody) {
    HttpElementSpec spec =
        new HttpBodyJsonSpec("$.value", JsonItemFactory.stringItem("not-existing"));
    HttpResponse<byte[]> httpResponse = new MockHttpResponse(jsonBody);

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () ->
            assertThat(assertion.expected()).isEqualTo(JsonItemFactory.stringItem("not-existing")),
        () -> assertThat(assertion.actual()).isNull());
  }

  @Test
  void singleEntryJsonDifferentTypeAsInt() {
    HttpElementSpec spec = new HttpBodyJsonSpec("$.name", JsonItemFactory.stringItem("test"));
    // language=json
    HttpResponse<byte[]> httpResponse = new MockHttpResponse("{\"name\": 400}");

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);
    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () -> assertThat(assertion.expected()).isEqualTo(JsonItemFactory.stringItem("test")),
        () -> assertThat(assertion.actual()).isEqualTo(JsonItemFactory.intItem(400)));
  }

  @Test
  void singleEntryJsonDifferentTypeAsDouble() {
    HttpElementSpec spec = new HttpBodyJsonSpec("$.name", JsonItemFactory.stringItem("test"));
    // language=json
    HttpResponse<byte[]> httpResponse = new MockHttpResponse("{\"name\": 4.25}");

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);
    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () -> assertThat(assertion.expected()).isEqualTo(JsonItemFactory.stringItem("test")),
        () -> assertThat(assertion.actual()).isEqualTo(JsonItemFactory.doubleItem(4.25)));
  }

  @Test
  void singleEntryJsonDifferentTypeAsArrayOfString() {
    HttpElementSpec spec =
        new HttpBodyJsonSpec(
            // language=json-path
            "$.name", JsonItemFactory.stringItem("test"));
    // language=json
    HttpResponse<byte[]> httpResponse = new MockHttpResponse("{\"name\": [\"James\",\"Johnson\"]}");

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);
    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () -> assertThat(assertion.expected()).isEqualTo(JsonItemFactory.stringItem("test")),
        () ->
            assertThat(assertion.actual())
                .isEqualTo(JsonItemFactory.arrayItemOfString("James", "Johnson")));
  }

  @Test
  void description() {
    HttpBodyJsonSpec httpBodyJsonSpec =
        new HttpBodyJsonSpec("$.name", JsonItemFactory.stringItem("John"));
    assertThat(httpBodyJsonSpec.description()).isEqualTo("expecting path=[$.name] value=John");
  }
}
