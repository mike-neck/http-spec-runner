package org.mikeneck.httpspec;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface HttpSpecRunner {

  static Builder builder() {
    throw new UnsupportedOperationException("not implemented");
  }

  static HttpSpecRunner from(File yamlFile) {
    throw new UnsupportedOperationException("not implemented");
  }

  @NotNull
  List<@NotNull VerificationResult> runForResult();

  void run();

  interface Builder {

    @NotNull
    HttpSpecRunner build();

    @NotNull
    HttpSpecRunner build(@NotNull Client client);

    @NotNull
    HttpSpecRunner build(@NotNull Extension extension);

    @NotNull
    HttpSpecRunner build(@NotNull Client client, @NotNull Extension extension);

    void addSpec(@NotNull Consumer<@NotNull ? super HttpSpec> configuration);
  }
}
