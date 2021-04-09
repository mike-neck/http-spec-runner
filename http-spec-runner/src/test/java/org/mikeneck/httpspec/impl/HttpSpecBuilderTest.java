package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.HttpRequestMethodSpec;
import org.mikeneck.httpspec.HttpRequestSpec;
import org.mikeneck.httpspec.HttpSpec;

class HttpSpecBuilderTest {

  @Test
  void requestReturnsHttpRequestMethodSpec() {
    HttpSpecBuilder builder = new HttpSpecBuilder();
    HttpRequestMethodSpec httpRequestMethodSpec = ((HttpSpec) builder).request();
    assertThat(httpRequestMethodSpec).isNotNull();
  }

  @Test
  void httpRequestSpecInGetMethodWillConfigured() {
    List<HttpRequestSpec> specs = new ArrayList<>();
    HttpSpecBuilder builder = new HttpSpecBuilder();
    ((HttpSpec) builder).request().get("http://example.com", specs::add);
    assertThat(specs).hasSize(1);
  }
}
