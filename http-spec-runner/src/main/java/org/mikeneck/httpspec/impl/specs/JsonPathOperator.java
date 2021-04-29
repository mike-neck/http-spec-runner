package org.mikeneck.httpspec.impl.specs;

import org.jetbrains.annotations.NotNull;

public interface JsonPathOperator {

  @NotNull
  JsonPathProduct apply(@NotNull String json);
}
