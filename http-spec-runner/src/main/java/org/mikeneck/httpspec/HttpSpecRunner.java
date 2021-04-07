package org.mikeneck.httpspec;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface HttpSpecRunner {

    void run();

    interface Builder {

        @NotNull
        HttpSpecRunner build();

        void addSpec(@NotNull Consumer<@NotNull ? super HttpSpec> configuration);
    }
}
