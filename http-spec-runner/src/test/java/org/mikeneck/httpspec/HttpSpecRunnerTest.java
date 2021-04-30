package org.mikeneck.httpspec;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({ResourceFile.Loader.class, Env.Resolver.class})
class HttpSpecRunnerTest {

  static WireMockServer wireMockServer;

  @BeforeAll
  static void setupMockServer() {
    wireMockServer = new WireMockServer(options().port(8080));
    wireMockServer.start();
    WireMock.configureFor(8080);
  }

  @AfterAll
  static void shutdownMockServer() {
    wireMockServer.stop();
  }

  @BeforeEach
  void resetMockServer() {
    WireMock.reset();
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void runRequestFromYamlFile(
      String responseJson,
      @Env(
              name = "CONFIG_YAML",
              defaultValue = "http-spec-runner/src/test/resources/http-spec-runner.yaml")
          String fileName) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withQueryParams(Map.of("q", equalTo("test")))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(responseJson)));

    var yamlFile = new File(fileName);
    HttpSpecRunner.from(yamlFile).run();
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void runRequestByJavaCode(String responseJson) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withQueryParams(Map.of("q", equalTo("test")))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(responseJson)));

    HttpSpecRunner.Builder builder = HttpSpecRunner.builder();
    builder.addSpec(
        spec -> {
          spec.name("getting path resource with paging parameters");
          spec.request()
              .get(
                  "http://localhost:8080/path",
                  request -> {
                    request.query("q", "test");
                    request.query("page", 4);
                    request.query("limit", 10);
                    request.header("accept", "application/json");
                    request.header("authorization", "bearer 11aa22bb33cc");
                  });
          spec.response(
              response -> {
                response.status(200);
                response.header("content-type", "application/json");
                response.jsonBody(jsonBody -> jsonBody.path("$.firstName").toBe("John"));
              });
        });
    HttpSpecRunner httpSpecRunner = builder.build();
    httpSpecRunner.run();
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void runForResult(
      String responseJson,
      @Env(
              name = "CONFIG_YAML",
              defaultValue = "http-spec-runner/src/test/resources/http-spec-runner.yaml")
          String fileName) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withQueryParams(Map.of("q", equalTo("test")))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(responseJson)));

    var yamlFile = new File(fileName);
    List<@NotNull VerificationResult> results = HttpSpecRunner.from(yamlFile).runForResult();

    assertAll(
        () -> assertThat(results).hasSize(1),
        () ->
            assertThat(results.stream().flatMap(result -> result.allAssertions().stream()))
                .hasSize(3),
        () ->
            assertThat(results.stream().flatMap(result -> result.allAssertions().stream()))
                .allMatch(HttpResponseAssertion::isSuccess));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void runMethodThrowsExceptionWhenThereAreTestFailures(String responseJson) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withQueryParams(Map.of("q", equalTo("test")))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(responseJson)));

    HttpSpecRunner.Builder builder = HttpSpecRunner.builder();
    builder.addSpec(
        spec -> {
          spec.name("getting path resource with paging parameters");
          spec.request()
              .get(
                  "http://localhost:8080/path",
                  request -> {
                    request.query("q", "test");
                    request.query("page", 4);
                    request.query("limit", 10);
                    request.header("accept", "application/json");
                    request.header("authorization", "bearer 11aa22bb33cc");
                  });
          spec.response(
              response -> {
                response.status(200);
                response.header("content-type", "application/json");
                response.jsonBody(jsonBody -> jsonBody.path("$.firstName").toBe("Anthony"));
                response.jsonBody(jsonBody -> jsonBody.path("$.lastName").toBe("Davis"));
              });
        });
    HttpSpecRunner httpSpecRunner = builder.build();

    assertThatThrownBy(httpSpecRunner::run)
        .isInstanceOf(UnexpectedResponseException.class)
        .hasMessageContaining("getting path resource with paging parameters");
  }
}
