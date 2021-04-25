package org.mikeneck.httpspec;

import java.io.File;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface HttpSpecRunner {

  static Builder builder() {
    throw new UnsupportedOperationException("not implemented");
  }

  static HttpSpecRunner from(File yamlFile) {
    throw new UnsupportedOperationException("not implemented");
  }

  void run();

  interface Builder {

    @NotNull
    HttpSpecRunner build();

    @NotNull
    HttpSpecRunner build(@NotNull Consumer<@NotNull ClientConfiguration> clientConfiguration);

    void addSpec(@NotNull Consumer<@NotNull ? super HttpSpec> configuration);
  }
}
