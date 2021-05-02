package org.mikeneck.httpspec.junit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mikeneck.httpspec.HttpSpecRunner;
import org.mikeneck.httpspec.ResourceFile;

@ExtendWith(ResourceFile.Loader.class)
class JUnitJupiterAdapterTest {

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

  @ResourceFile("test.json")
  @TestFactory
  Stream<DynamicTest> apply(String responseJson) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withQueryParams(
                Map.of("q", equalTo("test"), "page", equalTo("4"), "limit", equalTo("10")))
            .withHeader("accept", equalTo("application/json"))
            .withHeader("authorization", containing("11aa22bb33cc"))
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
    return JUnitJupiterAdapter.apply(httpSpecRunner);
  }
}
