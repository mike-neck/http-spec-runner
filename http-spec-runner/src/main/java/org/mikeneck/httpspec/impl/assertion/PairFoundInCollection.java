package org.mikeneck.httpspec.impl.assertion;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.NameValuePair;

public class PairFoundInCollection<@NotNull S, @NotNull T extends NameValuePair<S>>
    implements HttpResponseAssertion<Collection<T>> {

  private final @NotNull String subtitle;
  private final @NotNull T item;
  private final @NotNull Collection<T> collection;

  public PairFoundInCollection(
      @NotNull String subtitle, @NotNull T item, @NotNull Collection<T> collection) {
    this.subtitle = subtitle;
    this.item = item;
    this.collection = collection;
  }

  @Override
  public @NotNull String subtitle() {
    return subtitle;
  }

  @Override
  public @NotNull Collection<T> expected() {
    return List.of(item);
  }

  @Override
  public @Nullable Collection<T> actual() {
    return collection;
  }

  @Override
  public @NotNull String description() {
    String expected = String.format("header(%s: %s)", item.name(), item.value());
    String actual =
        collection.stream()
            .map(pair -> String.format("(%s: %s)", pair.name(), pair.value()))
            .collect(Collectors.joining(", ", "headers ", ""));
    return String.format("expected: to contain '%s'\nactual : %s", expected, actual);
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("ItemFoundInCollection{");
    sb.append("item=").append(item);
    sb.append(", collection=").append(collection);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PairFoundInCollection)) return false;
    PairFoundInCollection<?, ?> that = (PairFoundInCollection<?, ?>) o;
    return item.equals(that.item) && collection.equals(that.collection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(item, collection);
  }
}
