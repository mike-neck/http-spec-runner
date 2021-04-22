package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.HttpResponseSpec;
import org.mikeneck.httpspec.impl.specs.HttpBodyJsonSpec;
import org.mikeneck.httpspec.impl.specs.HttpHeadersSpec;
import org.mikeneck.httpspec.impl.specs.HttpStatusSpec;

class HttpResponseSpecImplTest {
  @Test
  void initialIsEmpty() {
    HttpResponseSpecImpl httpResponseSpec = new HttpResponseSpecImpl();

    List<HttpElementSpec> specs = httpResponseSpec.getSpecs();

    assertThat(specs).isEmpty();
  }

  @Test
  void callStatus() {
    HttpResponseSpecImpl httpResponseSpec = new HttpResponseSpecImpl();

    ((HttpResponseSpec) httpResponseSpec).status(200);

    List<HttpElementSpec> specs = httpResponseSpec.getSpecs();

    assertThat(specs).hasSize(1).hasOnlyElementsOfTypes(HttpStatusSpec.class);
  }

  @Test
  void callHeader() {
    HttpResponseSpecImpl httpResponseSpec = new HttpResponseSpecImpl();

    ((HttpResponseSpec) httpResponseSpec).header("content-type", "application/json");

    List<HttpElementSpec> specs = httpResponseSpec.getSpecs();

    assertThat(specs).hasSize(1).hasOnlyElementsOfTypes(HttpHeadersSpec.class);
  }

  @Test
  void callJsonBodyInSingleAssertion() {
    HttpResponseSpecImpl httpResponseSpec = new HttpResponseSpecImpl();

    ((HttpResponseSpec) httpResponseSpec)
        .jsonBody(jsonBody -> jsonBody.path("$.name").toBe("John"));

    List<HttpElementSpec> specs = httpResponseSpec.getSpecs();

    assertThat(specs).hasSize(1).hasOnlyElementsOfTypes(HttpBodyJsonSpec.class);
  }
}
