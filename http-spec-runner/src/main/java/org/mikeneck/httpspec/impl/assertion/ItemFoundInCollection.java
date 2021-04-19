package org.mikeneck.httpspec.impl.assertion;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;

public class ItemFoundInCollection<@NotNull T> implements HttpResponseAssertion<Collection<T>> {

  private final @NotNull T item;
  private final @NotNull Collection<T> collection;

  public ItemFoundInCollection(@NotNull T item, @NotNull Collection<T> collection) {
    this.item = item;
    this.collection = collection;
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
    return String.format("expected: to contain '%s'\nactual : %s", item, collection);
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
    if (!(o instanceof org.mikeneck.httpspec.impl.assertion.ItemFoundInCollection)) return false;
    org.mikeneck.httpspec.impl.assertion.ItemFoundInCollection<?> that =
        (org.mikeneck.httpspec.impl.assertion.ItemFoundInCollection<?>) o;
    return item.equals(that.item) && collection.equals(that.collection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(item, collection);
  }
}
