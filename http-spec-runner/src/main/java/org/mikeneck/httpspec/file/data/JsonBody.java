package org.mikeneck.httpspec.file.data;

import java.util.List;

public class JsonBody {

  public String path;
  public List<Expect> expect;

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
