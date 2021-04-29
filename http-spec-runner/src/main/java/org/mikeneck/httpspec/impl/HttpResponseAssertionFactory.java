package org.mikeneck.httpspec.impl;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.impl.assertion.ExceptionOccurred;
import org.mikeneck.httpspec.impl.assertion.Failure;
import org.mikeneck.httpspec.impl.assertion.ItemFoundInCollection;
import org.mikeneck.httpspec.impl.assertion.Success;

public interface HttpResponseAssertionFactory {

  @NotNull
  static <@NotNull T> HttpResponseAssertion<T> success(@NotNull T expected) {
    return new Success<>(expected);
  }

  @NotNull
  @SafeVarargs
  static <@NotNull T> HttpResponseAssertion<Collection<T>> itemFoundInCollection(
      @NotNull T item, @NotNull T... collection) {
    return itemFoundInCollection(item, List.of(collection));
  }

  @NotNull
  static <@NotNull T> HttpResponseAssertion<Collection<T>> itemFoundInCollection(
      @NotNull T item, @NotNull Collection<@NotNull T> collection) {
    return new ItemFoundInCollection<>(item, collection);
  }

  @NotNull
  static <@NotNull T> HttpResponseAssertion<T> failure(@NotNull T expected, @Nullable T actual) {
    return new Failure<>(expected, actual);
  }

  @NotNull
  static <@NotNull T> HttpResponseAssertion<T> exception(@NotNull T expected, Throwable throwable) {
    return new ExceptionOccurred<>(expected, throwable);
  }
}
