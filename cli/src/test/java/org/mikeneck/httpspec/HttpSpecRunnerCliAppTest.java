package org.mikeneck.httpspec;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import picocli.CommandLine;

@ExtendWith({ResourceFile.Loader.class, MockServer.class, Env.Resolver.class})
@MockServer.Port(8080)
class HttpSpecRunnerCliAppTest {

  @Test
  @ResourceFile("response.json")
  void successCaseWithQuiet(
      @Env(name = "TEST_SPEC", defaultValue = "src/test/resources/get-spec.yml") String yamlFile,
      @NotNull String responseJson) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withQueryParam("q", equalTo("test"))
            .withHeader("accept", equalTo("application/json"))
            .willReturn(
                aResponse().withHeader("content-type", "application/json").withBody(responseJson)));

    HttpSpecRunnerCliApp app = new HttpSpecRunnerCliApp();
    StringWriter sw = new StringWriter();
    CommandLine commandLine = new CommandLine(app).setOut(new PrintWriter(sw));

    System.out.println(new File(yamlFile).getAbsolutePath());
    int exit = commandLine.execute("-f", yamlFile, "-q");

    assertAll(() -> assertThat(exit).isEqualTo(0), () -> assertThat(sw.toString()).isEmpty());
  }

  @Test
  @ResourceFile("response-failure.json")
  void failureCaseWithQuiet(
      @Env(name = "TEST_SPEC", defaultValue = "src/test/resources/get-spec.yml") String yamlFile,
      @NotNull String responseJson) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withQueryParam("q", equalTo("test"))
            .withHeader("accept", equalTo("application/json"))
            .willReturn(
                aResponse().withHeader("content-type", "application/json").withBody(responseJson)));

    HttpSpecRunnerCliApp app = new HttpSpecRunnerCliApp();
    StringWriter sw = new StringWriter();
    CommandLine commandLine = new CommandLine(app).setOut(new PrintWriter(sw));

    System.out.println(new File(yamlFile).getAbsolutePath());
    int exit = commandLine.execute("-f", yamlFile, "-q");

    assertAll(() -> assertThat(exit).isNotEqualTo(0), () -> assertThat(sw.toString()).isEmpty());
  }

  @Test
  @ResourceFile("response.json")
  void successCaseWithPrinting(
      @Env(name = "TEST_SPEC", defaultValue = "src/test/resources/get-spec.yml") String yamlFile,
      @NotNull String responseJson) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withQueryParam("q", equalTo("test"))
            .withHeader("accept", equalTo("application/json"))
            .willReturn(
                aResponse().withHeader("content-type", "application/json").withBody(responseJson)));

    HttpSpecRunnerCliApp app = new HttpSpecRunnerCliApp();
    StringWriter sw = new StringWriter();
    CommandLine commandLine = new CommandLine(app).setOut(new PrintWriter(sw));

    System.out.println(new File(yamlFile).getAbsolutePath());
    int exit = commandLine.execute("-f", yamlFile);

    assertAll(
        () -> assertThat(exit).isEqualTo(0),
        () ->
            assertThat(sw.toString())
                .hasLineCount(4 + 5)
                .contains(
                    "all-spec         :      1",
                    "failed-spec      :      0",
                    "all-assertions   :      4",
                    "failed-assertions:      0"));
  }

  @Test
  @ResourceFile("response-failure.json")
  void failureCaseWithPrinting(
      @Env(name = "TEST_SPEC", defaultValue = "src/test/resources/get-spec.yml") String yamlFile,
      @NotNull String responseJson) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withQueryParam("q", equalTo("test"))
            .withHeader("accept", equalTo("application/json"))
            .willReturn(
                aResponse().withHeader("content-type", "application/json").withBody(responseJson)));

    HttpSpecRunnerCliApp app = new HttpSpecRunnerCliApp();
    StringWriter sw = new StringWriter();
    CommandLine commandLine = new CommandLine(app).setOut(new PrintWriter(sw));

    System.out.println(new File(yamlFile).getAbsolutePath());
    int exit = commandLine.execute("-f", yamlFile);

    assertAll(
        () -> assertThat(exit).isEqualTo(1),
        () ->
            assertThat(sw.toString())
                .hasLineCount(4 + 5 + 3)
                .contains(
                    "all-spec         :      1",
                    "failed-spec      :      1",
                    "all-assertions   :      4",
                    "failed-assertions:      1"));
  }
}
