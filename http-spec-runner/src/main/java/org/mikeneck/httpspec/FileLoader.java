package org.mikeneck.httpspec;

import java.io.File;
import org.jetbrains.annotations.NotNull;

public interface FileLoader {
  @NotNull
  HttpSpecRunner load(@NotNull File file);

  HttpSpecRunner load(@NotNull File yamlFile, @NotNull Client client, @NotNull Extension extension);
}
