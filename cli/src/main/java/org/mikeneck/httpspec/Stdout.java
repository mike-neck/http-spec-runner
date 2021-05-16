package org.mikeneck.httpspec;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Stdout {

  void success(@NotNull String line);

  void failure(@NotNull String line);

  void normal(@NotNull String line);

  @SuppressWarnings("Convert2MethodRef")
  static Color withMode(boolean quiet) {
    if (quiet) {
      return color -> destination -> Impl.QUIET.toStdout(destination);
    } else {
      return color ->
          destination -> {
            if (color) {
              return Impl.COLOR.toStdout(destination);
            } else {
              return Impl.NORMAL.toStdout(destination);
            }
          };
    }
  }

  interface Color {
    Out withColor(boolean color);
  }

  interface Out {
    Stdout outputTo(@NotNull Consumer<@NotNull String> destination);
  }

  interface Mixer {
    @Nullable
    String success(@NotNull String line);

    @Nullable
    String failure(@NotNull String line);

    @Nullable
    String normal(@NotNull String line);

    default Stdout toStdout(@NotNull Consumer<@NotNull String> destination) {
      final Mixer mixer = this;
      return new Stdout() {

        @Override
        public String toString() {
          return mixer.toString();
        }

        @Override
        public void success(@NotNull String line) {
          String text = mixer.success(line);
          if (text != null) {
            destination.accept(text);
          }
        }

        @Override
        public void failure(@NotNull String line) {
          String text = mixer.failure(line);
          if (text != null) {
            destination.accept(text);
          }
        }

        @Override
        public void normal(@NotNull String line) {
          String text = mixer.normal(line);
          if (text != null) {
            destination.accept(text);
          }
        }
      };
    }
  }

  enum Impl implements Mixer {
    QUIET {
      @Override
      public @Nullable String success(@NotNull String line) {
        return null;
      }

      @Override
      public @Nullable String failure(@NotNull String line) {
        return null;
      }

      @Override
      public @Nullable String normal(@NotNull String line) {
        return null;
      }
    },
    COLOR {
      @Override
      public @NotNull String success(@NotNull String line) {
        return String.format("\u001B[32m%s\u001B[0m", line);
      }

      @Override
      public @NotNull String failure(@NotNull String line) {
        return String.format("\u001B[31m%s\u001B[0m", line);
      }

      @Override
      public @NotNull String normal(@NotNull String line) {
        return line;
      }
    },
    NORMAL {
      @Override
      public @NotNull String success(@NotNull String line) {
        return line;
      }

      @Override
      public @NotNull String failure(@NotNull String line) {
        return line;
      }

      @Override
      public @NotNull String normal(@NotNull String line) {
        return line;
      }
    },
  }
}
