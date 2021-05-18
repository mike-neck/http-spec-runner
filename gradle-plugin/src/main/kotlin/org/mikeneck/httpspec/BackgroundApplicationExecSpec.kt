package org.mikeneck.httpspec

import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.process.JavaExecSpec

interface BackgroundApplicationExecSpec : Action<JavaExecSpec> {

  @get:InputFiles val classpath: FileCollection

  @get:Input val args: ListProperty<String>

  @get:Input val mainClass: Property<String>

  @get:Input val jvmArgs: ListProperty<String>
}
