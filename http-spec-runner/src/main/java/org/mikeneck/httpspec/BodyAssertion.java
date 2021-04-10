package org.mikeneck.httpspec;

public interface BodyAssertion {

  void toBe(String expectedValue);

  void toBe(long expectedValue);

  void toBe(boolean expectedValue);

  void toBe(double expectedValue);
}
