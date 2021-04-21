package org.mikeneck.httpspec.impl.specs.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DynamicTest;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;
import org.mikeneck.httpspec.impl.specs.JsonItem;

public class JsonItemTestFactory<T extends JsonItem> {

  @NotNull private final Class<T> klass;
  @NotNull private final T target;
  @NotNull private final JsonItem differentType;
  @NotNull private final T differentValue;
  @NotNull private final T sameValue;

  public JsonItemTestFactory(
      @NotNull Class<T> klass,
      @NotNull T target,
      @NotNull JsonItem differentType,
      @NotNull T differentValue,
      @NotNull T sameValue) {
    this.klass = klass;
    this.target = target;
    this.differentType = differentType;
    this.differentValue = differentValue;
    this.sameValue = sameValue;
  }

  @NotNull
  Stream<DynamicTest> allTests() {
    return Stream.of(empty(), differentType(), differentValue(), sameValue())
        .flatMap(Supplier::get);
  }

  private Supplier<Stream<DynamicTest>> empty() {
    return () -> {
      @SuppressWarnings("NullableProblems")
      HttpResponseAssertion<?> assertion = target.testJson(Optional::empty);

      return Stream.of(
          dynamicTest(
              "test with null object -> failure",
              () -> assertThat(assertion.isSuccess()).isFalse()),
          dynamicTest(
              "test with null object -> actual is null",
              () -> assertThat(assertion.actual()).isNull()),
          dynamicTest(
              "test with null object -> expected is target object",
              () -> assertThat(assertion.expected()).isEqualTo(target)));
    };
  }

  private Supplier<Stream<DynamicTest>> differentType() {
    return () -> {
      HttpResponseAssertion<?> assertion = target.testJson(() -> Optional.of(differentType));

      return Stream.of(
          dynamicTest(
              "test with different type -> failure",
              () -> assertThat(assertion.isSuccess()).isFalse()),
          dynamicTest(
              "test with different type -> actual is different type",
              () -> assertThat(assertion.actual()).isEqualTo(differentType).isNotInstanceOf(klass)),
          dynamicTest(
              "test with different type -> expected is target object",
              () -> assertThat(assertion.expected()).isEqualTo(target)));
    };
  }

  private Supplier<Stream<DynamicTest>> differentValue() {
    return () -> {
      HttpResponseAssertion<?> assertion = target.testJson(() -> Optional.of(differentValue));

      return Stream.of(
          dynamicTest(
              "test with different value -> failure",
              () -> assertThat(assertion.isSuccess()).isFalse()),
          dynamicTest(
              "test with different value -> actual is the different value",
              () -> assertThat(assertion.actual()).isEqualTo(differentValue).isInstanceOf(klass)),
          dynamicTest(
              "test with different value -> expected is target object",
              () -> assertThat(assertion.expected()).isEqualTo(target)));
    };
  }

  private Supplier<Stream<DynamicTest>> sameValue() {
    return () -> {
      HttpResponseAssertion<?> assertion = target.testJson(() -> Optional.of(sameValue));

      return Stream.of(
          dynamicTest(
              "test with same value -> success", () -> assertThat(assertion.isSuccess()).isTrue()),
          dynamicTest(
              "test with same value -> actual is the expected value",
              () -> assertThat(assertion.actual()).isEqualTo(target).isInstanceOf(klass)),
          dynamicTest(
              "test with same value -> expected is target object",
              () -> assertThat(assertion.expected()).isEqualTo(sameValue)));
    };
  }
}
