package org.mikeneck.httpspec.file.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetTest {

  private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

  @Test
  void httpSpecWithoutQueryWithoutHeader() {
    Get get = new Get();
    get.get = "https://example.com/test-resource";
    get.queries = new ObjectNode(objectMapper.getNodeFactory());
    get.headers = new ObjectNode(objectMapper.getNodeFactory());

    HttpSpecForTest.HttpSpecInterceptor httpSpec = HttpSpecForTest.newHttpSpec();

    get.accept(httpSpec);

    assertAll(
        () -> assertThat(httpSpec.url).isEqualTo(get.get),
        () -> assertThat(httpSpec.request.headers).isEmpty(),
        () -> assertThat(httpSpec.request.queries).isEmpty());
  }

  @Test
  void httpSpecWithQueries() {
    Get get = new Get();
    get.get = "https://example.com/test-resource";
    ObjectNode queries = new ObjectNode(objectMapper.getNodeFactory());
    queries.put("search", "java");
    get.queries = queries;
    get.headers = new ObjectNode(objectMapper.getNodeFactory());

    HttpSpecForTest.HttpSpecInterceptor httpSpec = HttpSpecForTest.newHttpSpec();

    get.accept(httpSpec);

    assertAll(
        () -> assertThat(httpSpec.url).isEqualTo(get.get),
        () -> assertThat(httpSpec.request.headers).isEmpty(),
        () -> assertThat(httpSpec.request.queries).hasSize(1).containsKey("search"));
  }

  @Test
  void httpSpecWithHeaders() {
    Get get = new Get();
    get.get = "https://example.com/test-resource";
    ObjectNode headers = new ObjectNode(objectMapper.getNodeFactory());
    headers.put("accept", "application/json");
    get.queries = new ObjectNode(objectMapper.getNodeFactory());
    get.headers = headers;

    HttpSpecForTest.HttpSpecInterceptor httpSpec = HttpSpecForTest.newHttpSpec();

    get.accept(httpSpec);

    assertAll(
        () -> assertThat(httpSpec.url).isEqualTo(get.get),
        () ->
            assertThat(httpSpec.request.headers)
                .hasSize(1)
                .containsEntry("accept", List.of("application/json")),
        () -> assertThat(httpSpec.request.queries).isEmpty());
  }
}
