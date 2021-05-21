package org.mikeneck.httpspec

@Retention(AnnotationRetention.RUNTIME)
annotation class File(
    val path: String,
    val contents: String,
)
