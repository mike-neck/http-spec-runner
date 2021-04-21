package org.mikeneck.httpspec.impl.specs;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface JsonPathReader {
  @NotNull
  Optional<JsonItem> readJson(@NotNull String json);
}
