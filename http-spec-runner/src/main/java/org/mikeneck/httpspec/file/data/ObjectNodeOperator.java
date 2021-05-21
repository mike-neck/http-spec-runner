package org.mikeneck.httpspec.file.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjLongConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ObjectNodeOperator {

  private final @NotNull ObjectNode node;

  ObjectNodeOperator(@NotNull ObjectNode node) {
    this.node = node;
  }

  ObjectNodeOperator() {
    this.node = new ObjectNode(new JsonNodeFactory(false));
  }

  static ObjectNodeOperator fromNullable(@Nullable ObjectNode node) {
    if (node == null) {
      return new ObjectNodeOperator();
    } else {
      return new ObjectNodeOperator(node);
    }
  }

  @NotNull
  IntOperator doOnString(BiConsumer<? super String, ? super String> doOnString) {
    return doOnInt ->
        douOnDouble ->
            doOnBoolean ->
                () -> {
                  List<String> unsupported = new ArrayList<>();
                  Iterator<String> iterator = node.fieldNames();
                  while (iterator.hasNext()) {
                    String name = iterator.next();
                    JsonNode value = this.node.get(name);
                    if (value.isTextual()) {
                      doOnString.accept(name, value.asText());
                    } else if (value.isIntegralNumber()) {
                      doOnInt.accept(name, value.asLong());
                    } else if (value.isFloatingPointNumber()) {
                      douOnDouble.accept(name, value.asDouble());
                    } else if (value.isBoolean()) {
                      doOnBoolean.accept(name, value.asBoolean());
                    } else {
                      unsupported.add(name);
                    }
                  }
                  if (!unsupported.isEmpty()) {
                    throw new IllegalStateException(
                        String.format("unsupported values are found: %s", unsupported));
                  }
                };
  }

  @FunctionalInterface
  public interface BooleanOperation {
    void accept(@NotNull String name, boolean value);
  }

  public interface Configured {
    void execute();
  }

  @FunctionalInterface
  public interface IntOperator extends DoubleOperator {
    @Override
    default BooleanOperator doOnDouble(ObjDoubleConsumer<? super String> doOnDouble) {
      return doOnInt((k, v) -> {}).doOnDouble(doOnDouble);
    }

    DoubleOperator doOnInt(ObjLongConsumer<? super String> doOnInt);
  }

  @FunctionalInterface
  public interface DoubleOperator extends BooleanOperator {
    @Override
    default Configured doOnBoolean(BooleanOperation doOnBoolean) {
      return doOnDouble((string, value) -> {}).doOnBoolean(doOnBoolean);
    }

    BooleanOperator doOnDouble(ObjDoubleConsumer<? super String> doOnDouble);
  }

  @FunctionalInterface
  public interface BooleanOperator extends Configured {
    @Override
    default void execute() {
      doOnBoolean((string, value) -> {}).execute();
    }

    Configured doOnBoolean(BooleanOperation doOnBoolean);
  }
}
