package org.mikeneck.httpspec

import kotlin.reflect.KClass
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

inline fun <reified T : Any> ObjectFactory.property(klass: KClass<T> = T::class): Property<T> =
    this.property(klass.java)

inline fun <reified T : Any> ObjectFactory.listProperty(
    klass: KClass<T> = T::class
): ListProperty<T> = this.listProperty(klass.java)

inline fun <reified K : Any, reified V : Any> ObjectFactory.mapProperty(
    keyClass: KClass<K> = K::class,
    valueClass: KClass<V> = V::class
): MapProperty<K, V> = this.mapProperty(keyClass.java, valueClass.java)

inline fun <reified T : Any> logger(klass: KClass<T> = T::class): Logger =
    Logging.getLogger(klass.java)
