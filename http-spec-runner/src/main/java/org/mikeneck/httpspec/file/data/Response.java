package org.mikeneck.httpspec.file.data;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.function.Consumer;
import org.mikeneck.httpspec.HttpResponseSpec;

public class Response implements Consumer<HttpResponseSpec> {

  public int status;

  public ObjectNode headers;

  public List<JsonBody> body;

  @Override
  public void accept(HttpResponseSpec httpResponseSpec) {}

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("Response{");
    sb.append("status=").append(status);
    sb.append(", headers=").append(headers);
    sb.append(", body=").append(body);
    sb.append('}');
    return sb.toString();
  }
}
