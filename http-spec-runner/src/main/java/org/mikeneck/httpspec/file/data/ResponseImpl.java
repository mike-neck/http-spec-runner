package org.mikeneck.httpspec.file.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mikeneck.httpspec.HttpResponseSpec;

public class ResponseImpl implements Response {

  private static final int DEFAULT_STATUS = -1;

  public int status = DEFAULT_STATUS;

  public ObjectNode headers;

  public List<JsonBody> body;

  @Override
  public void accept(HttpResponseSpec httpResponseSpec) {
    if (status != DEFAULT_STATUS) {
      httpResponseSpec.status(status);
    }

    if (headers != null) {
      Iterator<Map.Entry<String, JsonNode>> headerIterator = headers.fields();
      while (headerIterator.hasNext()) {
        Map.Entry<String, JsonNode> header = headerIterator.next();
        JsonNode node = header.getValue();
        if (!node.isNull()) {
          String value = node.asText();
          httpResponseSpec.header(header.getKey(), value);
        }
      }
    }

    if (body != null && !body.isEmpty()) {
      for (JsonBody jsonBody : body) {
        httpResponseSpec.jsonBody(jsonBody);
      }
    }
  }

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
