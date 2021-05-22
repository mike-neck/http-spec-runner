package org.mikeneck.httpspec

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Files(
    val value: Array<File> = []
)
