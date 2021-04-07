package org.mikeneck.httpspec;

import org.jetbrains.annotations.NotNull;

public interface HttpRequestSpec {

    @NotNull
    HttpRequestSpec query(@NotNull String name, @NotNull String value);

    @NotNull
    HttpRequestSpec query(@NotNull String name, int value);

    @NotNull
    HttpRequestSpec query(@NotNull String name, long value);

    @NotNull
    HttpRequestSpec header(@NotNull String name, @NotNull String... value);
}
