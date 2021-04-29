package org.mikeneck.httpspec.file.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mikeneck.httpspec.impl.Multimap;

class ResponseTest {

  private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

  @TestFactory
  Stream<DynamicTest> httpStatus() {
    return IntStream.of(200, 201, 400, 401, 402, 404, 500)
        .mapToObj(
            status -> {
              ResponseImpl response = new ResponseImpl();
              response.status = status;

              MockHttpResponseSpec httpResponseSpec = new MockHttpResponseSpec();

              response.accept(httpResponseSpec);

              return dynamicTest(
                  String.format("response status %d", status),
                  () -> assertThat(httpResponseSpec.status).isEqualTo(status));
            });
  }

  @Test
  void defaultStatus() {
    Response response = new ResponseImpl();

    MockHttpResponseSpec httpResponseSpec = new MockHttpResponseSpec();

    response.accept(httpResponseSpec);

    assertThat(httpResponseSpec.status).isEqualTo(-1);
  }

  @Test
  void headers() {
    ResponseImpl response = new ResponseImpl();
    response.headers =
        new ObjectNode(
            objectMapper.getNodeFactory(),
            Map.of("content-type", new TextNode("application/json")));

    MockHttpResponseSpec httpResponseSpec = new MockHttpResponseSpec();

    response.accept(httpResponseSpec);

    Multimap expected = new Multimap();
    expected.add("content-type", "application/json");

    assertAll(
        () -> assertThat(httpResponseSpec.headers.isEmpty()).isFalse(),
        () -> assertThat(httpResponseSpec.headers).isEqualTo(expected));
  }

  @Test
  void jsonBody() {
    ResponseImpl response = new ResponseImpl();
    response.body =
        jsonBodies(
            bodies -> {
              bodies.add("$.string", jb -> jb.stringOf("string-value"));
              bodies.add("$.int", jb -> jb.integralOf(20));
              bodies.add("$.double", jb -> jb.floatingOf(10.25));
            });

    MockHttpResponseSpec httpResponseSpec = new MockHttpResponseSpec();

    response.accept(httpResponseSpec);

    List<@NotNull Predicate<@NotNull String>> assertions = httpResponseSpec.jsonBodies;

    // language=json
    String json = "{\"int\": 20,\"string\": \"string-value\",\"double\": 10.25}";
    assertThat(assertions).hasSize(3).allMatch(predicate -> predicate.test(json));
  }

  @TestFactory
  Iterable<DynamicTest> all() {
    ResponseImpl response = new ResponseImpl();
    response.status = 200;
    response.headers =
        new ObjectNode(
            objectMapper.getNodeFactory(),
            Map.of("content-type", new TextNode("application/json")));
    response.body =
        jsonBodies(
            bodies -> {
              bodies.add("$.name", jb -> jb.stringOf("John"));
              bodies.add("$.age", jb -> jb.integralOf(30));
              bodies.add("$.average", jb -> jb.floatingOf(1.5));
            });

    MockHttpResponseSpec httpResponseSpec = new MockHttpResponseSpec();

    response.accept(httpResponseSpec);

    Multimap expectedHeaders = new Multimap();
    expectedHeaders.add("content-type", "application/json");
    // language=json
    String json = "{\"name\": \"John\",\"age\": 30,\"average\": 1.5}";

    return List.of(
        dynamicTest("http status == 200", () -> assertThat(httpResponseSpec.status).isEqualTo(200)),
        dynamicTest(
            "http header[content-type=application/json]",
            () -> assertThat(httpResponseSpec.headers).isEqualTo(expectedHeaders)),
        dynamicTest(
            "body assertions",
            () ->
                assertThat(httpResponseSpec.jsonBodies).allMatch(jsonTest -> jsonTest.test(json))));
  }

  static List<JsonBody> jsonBodies(Consumer<? super JsonBodies> config) {
    JsonBodies jsonBodies = new JsonBodies();
    config.accept(jsonBodies);
    return Collections.unmodifiableList(jsonBodies);
  }

  static class JsonBodies extends ArrayList<JsonBody> {
    void add(String path, Function<? super Builder, ? extends JsonBody> body) {
      Builder builder = new Builder(path);
      JsonBody jsonBody = body.apply(builder);
      add(jsonBody);
    }
  }

  static class Builder {

    final String path;

    Builder(String path) {
      this.path = path;
    }

    JsonBody stringOf(String value) {
      Expect expect = new Expect("string", new TextNode(value));
      return new JsonBody(path, expect);
    }

    JsonBody integralOf(long value) {
      Expect expect = new Expect("number", new LongNode(value));
      return new JsonBody(path, expect);
    }

    JsonBody floatingOf(double value) {
      Expect expect = new Expect("number", new DoubleNode(value));
      return new JsonBody(path, expect);
    }
  }
}
