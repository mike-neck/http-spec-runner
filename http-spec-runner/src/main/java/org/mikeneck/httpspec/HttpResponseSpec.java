package org.mikeneck.httpspec;

public interface HttpResponseSpec {

  void status(int expectedHttpStatus);

  void header(String expectedHeaderName, String expectedHeaderValue);

  BodyAssertion body(String jsonPath);
}
