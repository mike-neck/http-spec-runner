package org.mikeneck.httpspec.impl.specs;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface JsonPathProduct {

  @NotNull
  Optional<@NotNull JsonItem> get();
}
