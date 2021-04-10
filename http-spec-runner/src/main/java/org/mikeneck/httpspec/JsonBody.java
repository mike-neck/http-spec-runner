package org.mikeneck.httpspec;

import org.jetbrains.annotations.NotNull;

public interface JsonBody {

  @NotNull
  BodyAssertion path(@NotNull String jsonPath);
}
