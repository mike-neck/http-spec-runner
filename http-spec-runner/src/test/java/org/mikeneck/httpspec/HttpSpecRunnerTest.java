package org.mikeneck.httpspec;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.io.File;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpSpecRunnerTest {

  static WireMockServer wireMockServer;

  @BeforeAll
  static void setupMockServer() {
    wireMockServer = new WireMockServer(wireMockConfig().port(8000));
    wireMockServer.start();
    WireMock.configureFor(8000);
  }

  @AfterAll
  static void shutdownMockServer() {
    wireMockServer.stop();
  }

  @BeforeEach
  void resetMockServer() {
    WireMock.reset();
  }

  @Test
  void runRequestFromYamlFile() {
    var yamlFile = new File("http-spec-runner/src/test/resources/http-spec-runner.yaml");
    HttpSpecRunner.from(yamlFile).run();
  }

  @Test
  void runRequestByJavaCode() {
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
}
