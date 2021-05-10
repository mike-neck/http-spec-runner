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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void asIterable(String responseJson) {
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
                response.jsonBody(jsonBody -> jsonBody.path("$.lastName").toBe("doe"));
              });
        });
    HttpSpecRunner httpSpecRunner = builder.build();
    @NotNull
    Iterable<@NotNull VerificationResult> verificationResults = httpSpecRunner.runningAsIterable();

    Stream<VerificationResult> stream =
        StreamSupport.stream(verificationResults.spliterator(), false);

    assertThat(stream)
        .hasSize(1)
        .satisfiesExactly(
            result -> assertThat(result.specName()).contains("getting path resource with paging"))
        .satisfiesExactly(result -> assertThat(result.allAssertions()).hasSize(4));
  }

  @ResourceFile("json-path-reader-impl-test/read.json")
  @Test
  void addExtension(String responseJson) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withQueryParams(Map.of("q", equalTo("test")))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(responseJson)));

    HttpSpecRunner.Builder builder = HttpSpecRunner.builder();
    builder.addSpec(
        spec -> {
          spec.name("1st");
          spec.request()
              .get(
                  "http://localhost:8080/path",
                  request -> {
                    request.query("q", "test");
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
    builder.addSpec(
        spec -> {
          spec.name("2nd");
          spec.request()
              .get(
                  "http://localhost:8080/path",
                  request -> {
                    request.query("q", "test");
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

    @NotNull List<@NotNull String> list = new ArrayList<>();
    Extension primalExtension =
        Extension.builder()
            .onCallBeforeAllSpecs(all -> list.add(String.format("[1]before-all(%d)", countOf(all))))
            .onCallBeforeEachSpecs(
                each -> list.add(String.format("[1]before(%s)", each.specName())))
            .onCallAfterEachSpecs(each -> list.add(String.format("[1]after(%s)", each.specName())))
            .onCallAfterAllSpecs(all -> list.add(String.format("[1]after-all(%d)", countOf(all))))
            .build();
    Extension secondaryExtension =
        Extension.builder()
            .onCallBeforeAllSpecs(all -> list.add(String.format("[2]before-all(%d)", countOf(all))))
            .onCallBeforeEachSpecs(
                each -> list.add(String.format("[2]before(%s)", each.specName())))
            .onCallAfterEachSpecs(each -> list.add(String.format("[2]after(%s)", each.specName())))
            .onCallAfterAllSpecs(all -> list.add(String.format("[2]after-all(%d)", countOf(all))))
            .build();

    HttpSpecRunner original = builder.build(primalExtension);
    HttpSpecRunner httpSpecRunner = original.addExtension(secondaryExtension);

    httpSpecRunner.run();

    assertThat(list)
        .hasSize(12)
        .containsExactly(
            "[1]before-all(2)", // pri-before-all
            "[2]before-all(2)", // snd-before-all
            "[1]before(1st)", // pri-before-1st
            "[2]before(1st)", // snd-before-1st
            "[2]after(1st)", // snd-after-1st
            "[1]after(1st)", // pri-after-1st
            "[1]before(2nd)", // pri-before-2nd
            "[2]before(2nd)", // snd-before-2nd
            "[2]after(2nd)", // snd-after-2nd
            "[1]after(2nd)", // pri-after-2nd
            "[2]after-all(2)", // snd-after-all
            "[1]after-all(2)" // pri-after-all
            );
  }

  static int countOf(Iterable<?> iterable) {
    int count = 0;
    for (Object ignored : iterable) {
      count++;
    }
    return count;
  }
}
