package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.HttpRequestSpec;

class GetRequestBuilderTest {

  @Test
  void getWithoutQuery() {
    String url = "http://example.com/foo";
    GetRequestBuilder getRequestBuilder = new GetRequestBuilder(url);
    HttpRequest request = getRequestBuilder.build();
    assertAll(
        () -> assertThat(request.method()).isEqualTo("GET"),
        () -> assertThat(request.uri()).isEqualTo(URI.create(url)));
  }

  @Test
  void getWithSingleQuery() {
    String url = "http://example.com/foo";
    GetRequestBuilder getRequestBuilder = new GetRequestBuilder(url);
    ((HttpRequestSpec) getRequestBuilder).query("foo", "bar");
    HttpRequest request = getRequestBuilder.build();
    assertAll(
        () -> assertThat(request.method()).isEqualTo("GET"),
        () -> assertThat(request.uri()).isEqualTo(URI.create(url + "?foo=bar")));
  }

  @Test
  void getWithSingleQueryWithEncoding() {
    String url = "http://example.com/foo";
    GetRequestBuilder getRequestBuilder = new GetRequestBuilder(url);
    ((HttpRequestSpec) getRequestBuilder).query("foo", "テスト");
    HttpRequest request = getRequestBuilder.build();
    assertAll(
        () -> assertThat(request.method()).isEqualTo("GET"),
        () ->
            assertThat(request.uri())
                .isEqualTo(URI.create(url + "?foo=%E3%83%86%E3%82%B9%E3%83%88")));
  }

  @Test
  void getWithDoubleQuery() {
    String url = "http://example.com/foo";
    GetRequestBuilder getRequestBuilder = new GetRequestBuilder(url);
    ((HttpRequestSpec) getRequestBuilder).query("foo", "bar").query("baz", "qux");
    HttpRequest request = getRequestBuilder.build();
    assertAll(
        () -> assertThat(request.method()).isEqualTo("GET"),
        () -> assertThat(request.uri()).isEqualTo(URI.create(url + "?foo=bar&baz=qux")));
  }

  @Test
  void getWithDoubleEncodingQuery() {
    String url = "http://example.com/foo";
    GetRequestBuilder getRequestBuilder = new GetRequestBuilder(url);
    ((HttpRequestSpec) getRequestBuilder).query("foo", "b=r").query("baz", "q=x");
    HttpRequest request = getRequestBuilder.build();
    assertAll(
        () -> assertThat(request.method()).isEqualTo("GET"),
        () -> assertThat(request.uri()).isEqualTo(URI.create(url + "?foo=b%3Dr&baz=q%3Dx")));
  }

  @Test
  void getWithSingleIntQuery() {
    String url = "http://example.com/foo";
    GetRequestBuilder getRequestBuilder = new GetRequestBuilder(url);
    ((HttpRequestSpec) getRequestBuilder).query("foo", 1234);
    HttpRequest request = getRequestBuilder.build();
    assertAll(
        () -> assertThat(request.method()).isEqualTo("GET"),
        () -> assertThat(request.uri()).isEqualTo(URI.create(url + "?foo=1234")));
  }

  @Test
  void getWithSingleLongQuery() {
    String url = "http://example.com/foo";
    GetRequestBuilder getRequestBuilder = new GetRequestBuilder(url);
    ((HttpRequestSpec) getRequestBuilder).query("foo", 1234L);
    HttpRequest request = getRequestBuilder.build();
    assertAll(
        () -> assertThat(request.method()).isEqualTo("GET"),
        () -> assertThat(request.uri()).isEqualTo(URI.create(url + "?foo=1234")));
  }

  @Test
  void getWithMultipleValuedInSingleKeyQuery() {
    String url = "http://example.com/foo";
    GetRequestBuilder getRequestBuilder = new GetRequestBuilder(url);
    ((HttpRequestSpec) getRequestBuilder).query("foo", "bar").query("foo", "baz");
    HttpRequest request = getRequestBuilder.build();
    assertAll(
        () -> assertThat(request.method()).isEqualTo("GET"),
        () -> assertThat(request.uri()).isEqualTo(URI.create(url + "?foo=bar&foo=baz")));
  }

  @Test
  void getWithSingleHeaderValue() {
    String url = "http://example.com/foo";
    GetRequestBuilder getRequestBuilder = new GetRequestBuilder(url);
    ((HttpRequestSpec) getRequestBuilder).header("foo", "bar");
    HttpRequest request = getRequestBuilder.build();
    assertAll(
        () -> assertThat(request.method()).isEqualTo("GET"),
        () -> assertThat(request.uri()).isEqualTo(URI.create(url)),
        () -> {
          HttpHeaders headers = request.headers();
          assertThat(headers.allValues("foo"))
              .describedAs("header assertion %s", headers)
              .containsOnly("bar");
        });
  }

  @Test
  void methodChain() {
    String url = "http://example.com/foo";
    GetRequestBuilder getRequestBuilder = new GetRequestBuilder(url);
    ((HttpRequestSpec) getRequestBuilder)
        .header("foo", "bar")
        .query("baz", 1)
        .query("baz", 2L)
        .query("qux", "quux")
        .header("test", "test");
    HttpRequest request = getRequestBuilder.build();
  }
}
