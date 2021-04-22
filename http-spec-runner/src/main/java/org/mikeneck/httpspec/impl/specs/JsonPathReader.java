package org.mikeneck.httpspec.impl.specs;

import org.jetbrains.annotations.NotNull;

public interface JsonPathReader {

  @NotNull
  JsonPathProduct read(@NotNull String json);
}
