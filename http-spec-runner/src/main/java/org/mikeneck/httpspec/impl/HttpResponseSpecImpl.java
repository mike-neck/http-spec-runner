package org.mikeneck.httpspec.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseSpec;
import org.mikeneck.httpspec.JsonBody;
import org.mikeneck.httpspec.impl.specs.HttpHeadersSpec;
import org.mikeneck.httpspec.impl.specs.HttpStatusSpec;

public class HttpResponseSpecImpl implements HttpResponseSpec, Iterable<HttpElementSpec> {

  private final List<HttpElementSpec> specs;

  public HttpResponseSpecImpl() {
    this(new ArrayList<>());
  }

  HttpResponseSpecImpl(List<HttpElementSpec> specs) {
    this.specs = specs;
  }

  @NotNull
  List<HttpElementSpec> getSpecs() {
    return Collections.unmodifiableList(specs);
  }

  @Override
  public void status(int expectedHttpStatus) {
    specs.add(new HttpStatusSpec(expectedHttpStatus));
  }

  @Override
  public void header(String expectedHeaderName, String expectedHeaderValue) {
    specs.add(new HttpHeadersSpec(expectedHeaderName, expectedHeaderValue));
  }

  @Override
  public void jsonBody(@NotNull Consumer<@NotNull JsonBody> jsonContentsTest) {
    JsonBodyImpl jsonBody = new JsonBodyImpl();
    jsonContentsTest.accept(jsonBody);
    List<HttpElementSpec> specs = jsonBody.getSpecs();
    this.specs.addAll(specs);
  }

  @NotNull
  @Override
  public Iterator<HttpElementSpec> iterator() {
    return specs.iterator();
  }
}
