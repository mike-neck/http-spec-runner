package org.mikeneck.httpspec;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface HttpRequestMethodSpec {

    void get(@NotNull String url, @NotNull Consumer<@NotNull ? super HttpRequestSpec> configuration);
}
