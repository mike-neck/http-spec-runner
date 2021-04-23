package org.mikeneck.httpspec.impl.specs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.impl.HttpElementSpec;
import org.mikeneck.httpspec.impl.HttpHeaderItemImpl;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;
import org.mikeneck.httpspec.impl.MockHttpResponse;
import org.mikeneck.httpspec.impl.Multimap;

class HttpHeadersSpecTest {

  @Test
  void expectedHeaderAndActuallyDoesNotExist() {
    HttpElementSpec spec = new HttpHeadersSpec("content-type", "application/json");

    Multimap multimap = new Multimap();
    HttpResponse<byte[]> httpResponse = new MockHttpResponse(multimap);

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () ->
            assertThat(assertion.expected())
                .isEqualTo(new HttpHeaderItemImpl("content-type", "application/json")),
        () -> assertThat(assertion.actual()).isNull());
  }

  @Test
  void expectedHeaderAndActuallyDoesNotExist2() {
    HttpElementSpec spec = new HttpHeadersSpec("access-control-allow-methods", "GET");

    Multimap multimap = new Multimap();
    HttpResponse<byte[]> httpResponse = new MockHttpResponse(multimap);

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isFalse(),
        () ->
            assertThat(assertion.expected())
                .isEqualTo(new HttpHeaderItemImpl("access-control-allow-methods", "GET")),
        () -> assertThat(assertion.actual()).isNull());
  }

  @Test
  void expectedHeaderAndActuallyExists() {
    HttpElementSpec spec = new HttpHeadersSpec("content-type", "application/json");

    Multimap multimap = new Multimap();
    multimap.add("content-type", "application/json");
    HttpResponse<byte[]> httpResponse = new MockHttpResponse(multimap);

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () ->
            assertThat(assertion.expected())
                .isEqualTo(List.of(new HttpHeaderItemImpl("content-type", "application/json"))));
  }

  @Test
  void headerNameIgnoresCase() {
    HttpElementSpec spec = new HttpHeadersSpec("content-type", "application/json");

    Multimap multimap = new Multimap();
    multimap.add("CONTENT-TYPE", "application/json");
    HttpResponse<byte[]> httpResponse = new MockHttpResponse(multimap);

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);

    assertAll(
        () -> assertThat(assertion.isSuccess()).isTrue(),
        () ->
            assertThat(assertion.actual())
                .isEqualTo(List.of(new HttpHeaderItemImpl("CONTENT-TYPE", "application/json"))),
        () ->
            assertThat(assertion.expected())
                .isEqualTo(List.of(new HttpHeaderItemImpl("content-type", "application/json"))));
  }

  @Test
  void headerEqualsToOneOfHeaderValues() {
    HttpElementSpec spec = new HttpHeadersSpec("access-control-allow-methods", "GET");

    Multimap multimap = new Multimap();
    multimap.add("access-control-allow-methods", "GET");
    multimap.add("access-control-allow-methods", "POST");
    multimap.add("access-control-allow-methods", "OPTION");
    HttpResponse<byte[]> httpResponse = new MockHttpResponse(multimap);

    HttpResponseAssertion<?> assertion = spec.apply(httpResponse);

    assertAll(() -> assertThat(assertion.isSuccess()).isTrue());
  }

  @Test
  void description() {
    HttpElementSpec spec = new HttpHeadersSpec("content-type", "application/json");
    assertThat(spec.description())
        .isEqualTo("expecting header=content-type value=application/json");
  }
}
