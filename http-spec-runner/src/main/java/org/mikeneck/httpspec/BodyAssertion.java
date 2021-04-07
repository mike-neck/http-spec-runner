package org.mikeneck.httpspec;

public interface BodyAssertion {

    void toBe(String expectedValue);

    void toBe(long expectedValue);
}
