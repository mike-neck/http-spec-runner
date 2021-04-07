package org.mikeneck.httpspec;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface HttpSpec {

    void name(@NotNull String specName);

    @NotNull
    HttpRequestMethodSpec request();

    void response(@NotNull Consumer<@NotNull ? super HttpResponseSpec> configuration);
}
