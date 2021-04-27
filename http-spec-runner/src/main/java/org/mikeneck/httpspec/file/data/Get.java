package org.mikeneck.httpspec.file.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;
import org.mikeneck.httpspec.HttpRequestMethodSpec;
import org.mikeneck.httpspec.HttpSpec;

public class Get implements Request {

  public String get;

  public ObjectNode queries;

  public ObjectNode headers;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("GET " + get);
    if (!queries.isEmpty()) {
      sb.append('?');
    }
    Iterator<String> names = queries.fieldNames();
    while (names.hasNext()) {
      String name = names.next();
      JsonNode value = queries.get(name);
      sb.append(name).append('=').append(value);
      if (names.hasNext()) {
        sb.append('&');
      }
    }
    if (!headers.isEmpty()) {
      sb.append(' ');
    }
    Iterator<String> headerNames = headers.fieldNames();
    while (headerNames.hasNext()) {
      String name = headerNames.next();
      JsonNode value = headers.get(name);
      sb.append("header[").append(name).append("]=").append(value);
      if (headerNames.hasNext()) {
        sb.append(' ');
      }
    }
    return sb.toString();
  }

  @Override
  public void accept(HttpSpec http) {
    HttpRequestMethodSpec request = http.request();
    request.get(get);
    request.get(get, httpRequestSpec -> {});
  }
}
