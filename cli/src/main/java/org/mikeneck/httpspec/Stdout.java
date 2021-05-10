package org.mikeneck.httpspec;

import org.jetbrains.annotations.NotNull;

public interface Stdout {

  void success(@NotNull String line);

  void failure(@NotNull String line);

  void normal(@NotNull String line);

  static Color withMode(boolean quiet) {
    if (quiet) {
      return color -> Impl.QUIET;
    } else {
      return color -> {
        if (color) {
          return Impl.COLOR;
        } else {
          return Impl.NORMAL;
        }
      };
    }
  }

  interface Color {
    Stdout withColor(boolean color);
  }

  enum Impl implements Stdout {
    QUIET {
      @Override
      public void success(@NotNull String line) {}

      @Override
      public void failure(@NotNull String line) {}

      @Override
      public void normal(@NotNull String line) {}
    },
    COLOR {
      @Override
      public void success(@NotNull String line) {
        System.out.printf("\u001B[32m%s\u001B[0m\n", line);
      }

      @Override
      public void failure(@NotNull String line) {
        System.out.printf("\u001B[31m%s\u001B[0m\n", line);
      }

      @Override
      public void normal(@NotNull String line) {
        System.out.println(line);
      }
    },
    NORMAL {
      @Override
      public void success(@NotNull String line) {
        System.out.println(line);
      }

      @Override
      public void failure(@NotNull String line) {
        System.out.println(line);
      }

      @Override
      public void normal(@NotNull String line) {
        System.out.println(line);
      }
    },
  }
}
