package org.mikeneck.httpspec.impl;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.NameValuePair;
import org.mikeneck.httpspec.impl.assertion.ExceptionOccurred;
import org.mikeneck.httpspec.impl.assertion.Failure;
import org.mikeneck.httpspec.impl.assertion.PairFoundInCollection;
import org.mikeneck.httpspec.impl.assertion.Success;

public interface HttpResponseAssertionFactory {

  @NotNull
  static <@NotNull T> HttpResponseAssertion<T> success(
      @NotNull String subtitle, @NotNull T expected) {
    return new Success<>(subtitle, expected);
  }

  @NotNull
  @SafeVarargs
  static <@NotNull S, @NotNull T extends NameValuePair<S>>
      HttpResponseAssertion<Collection<T>> pairFoundInCollection(
          @NotNull String subtitle, @NotNull T item, @NotNull T... collection) {
    return pairFoundInCollection(subtitle, item, List.of(collection));
  }

  @NotNull
  static <@NotNull S, @NotNull T extends NameValuePair<S>>
      HttpResponseAssertion<Collection<T>> pairFoundInCollection(
          @NotNull String subtitle, @NotNull T item, @NotNull Collection<@NotNull T> collection) {
    return new PairFoundInCollection<>(subtitle, item, collection);
  }

  @NotNull
  static <@NotNull T> HttpResponseAssertion<T> failure(
      @NotNull String subtitle, @NotNull T expected, @Nullable T actual) {
    return new Failure<>(subtitle, expected, actual);
  }

  @NotNull
  static <@NotNull T> HttpResponseAssertion<T> exception(
      @NotNull String subtitle, @NotNull T expected, Throwable throwable) {
    return new ExceptionOccurred<>(subtitle, expected, throwable);
  }
}
