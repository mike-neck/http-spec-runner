package org.mikeneck.httpspec.file.data;

import java.util.function.Consumer;
import org.mikeneck.httpspec.BodyAssertion;

public class JsonBody implements Consumer<org.mikeneck.httpspec.JsonBody> {

  public String path;
  public Expect expect;

  public JsonBody() {}

  public JsonBody(String path, Expect expect) {
    this.path = path;
    this.expect = expect;
  }

  @Override
  public void accept(org.mikeneck.httpspec.JsonBody jsonBody) {
    BodyAssertion bodyAssertion = jsonBody.path(this.path);
    expect.accept(bodyAssertion);
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("JsonBody{");
    sb.append("path='").append(path).append('\'');
    sb.append(", expect=").append(expect);
    sb.append('}');
    return sb.toString();
  }
}
