package org.mikeneck.httpspec.file.data;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpSpec;

public class Spec implements Consumer<@NotNull HttpSpec> {

  public String name;
  public Request request;
  public Response response;

  @Override
  public void accept(@NotNull HttpSpec httpSpec) {}

  @Override
  public String toString() {
    return String.format("Spec{request=%s, response=%s}", request, response);
  }
}
