package org.mikeneck.httpspec.file.data;

import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Specs implements Iterable<Spec> {

  public List<Spec> spec;

  @NotNull
  @Override
  public Iterator<Spec> iterator() {
    return spec.iterator();
  }
}
