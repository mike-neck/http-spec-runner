package org.mikeneck.httpspec.file.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.HttpRequestMethodSpec;
import org.mikeneck.httpspec.HttpSpec;

public class Get implements Request {

  public String get;

  @Nullable
  public ObjectNode queries;

  @Nullable
  public ObjectNode headers;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("GET " + get);
    if (queries != null && !queries.isEmpty()) {
      sb.append('?');
    }
    if (queries != null) {
      Iterator<String> names = queries.fieldNames();
      while (names.hasNext()) {
        String name = names.next();
        JsonNode value = queries.get(name);
        sb.append(name).append('=').append(value);
        if (names.hasNext()) {
          sb.append('&');
        }
      }
    }
    if (headers != null && !headers.isEmpty()) {
      sb.append(' ');
    }
    if (headers != null) {
      Iterator<String> headerNames = headers.fieldNames();
      while (headerNames.hasNext()) {
        String name = headerNames.next();
        JsonNode value = headers.get(name);
        sb.append("header[").append(name).append("]=").append(value);
        if (headerNames.hasNext()) {
          sb.append(' ');
        }
      }
    }
    return sb.toString();
  }

  @Override
  public void accept(HttpSpec http) {
    ObjectNodeOperator queriesOperator = ObjectNodeOperator.fromNullable(queries);
    ObjectNodeOperator headerOperator = ObjectNodeOperator.fromNullable(headers);
    HttpRequestMethodSpec request = http.request();
    request.get(get);
    request.get(
        get,
        httpRequestSpec -> {
          queriesOperator
              .doOnString(httpRequestSpec::query)
              .doOnInt(httpRequestSpec::query)
              .doOnDouble((name, value) -> httpRequestSpec.query(name, Double.toString(value)))
              .doOnBoolean((name, value) -> httpRequestSpec.query(name, Boolean.toString(value)))
              .execute();
          headerOperator
              .doOnString(httpRequestSpec::header)
              .doOnInt((name, value) -> httpRequestSpec.header(name, Long.toString(value)))
              .doOnDouble((name, value) -> httpRequestSpec.header(name, Double.toString(value)))
              .doOnBoolean((name, value) -> httpRequestSpec.header(name, Boolean.toString(value)))
              .execute();
        });
  }
}
