package org.mikeneck.httpspec

import java.io.OutputStream
import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.process.JavaExecSpec

interface BackgroundApplicationExecSpec : Action<JavaExecSpec> {

  @get:InputFiles val classpath: ConfigurableFileCollection

  @get:Input val args: ListProperty<String>

  @get:Input val mainClass: Property<String>

  fun setMainClass(mainClass: String)

  @get:Input val jvmArgs: ListProperty<String>

  @get:Input val environment: MapProperty<String, String>

  @get:Internal var stdout: OutputStream

  fun waitUntilGet(url: String, condition: Action<ResponseCondition>)
}
