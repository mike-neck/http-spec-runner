package org.mikeneck.httpspec;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface HttpResponseSpec {

  void status(int expectedHttpStatus);

  void header(String expectedHeaderName, String expectedHeaderValue);

  void jsonBody(@NotNull Consumer<@NotNull JsonBody> jsonContentsTest);
}
