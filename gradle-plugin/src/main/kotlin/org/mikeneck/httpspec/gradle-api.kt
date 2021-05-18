package org.mikeneck.httpspec

import kotlin.reflect.KClass
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

inline fun <reified T : Any> ObjectFactory.property(klass: KClass<T> = T::class): Property<T> =
    this.property(klass.java)

inline fun <reified T : Any> ObjectFactory.listProperty(
    klass: KClass<T> = T::class
): ListProperty<T> = this.listProperty(klass.java)
