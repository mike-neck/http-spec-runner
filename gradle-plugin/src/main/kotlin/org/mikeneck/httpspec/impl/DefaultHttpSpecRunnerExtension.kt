package org.mikeneck.httpspec.impl

import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.mikeneck.httpspec.BackgroundApplicationExecSpec
import org.mikeneck.httpspec.HttpSpecCase
import org.mikeneck.httpspec.HttpSpecRunnerExtension
import org.mikeneck.httpspec.property

class DefaultHttpSpecRunnerExtension(
    @OutputDirectory override val reportDirectory: DirectoryProperty,
    @Input @Nested override val specs: ListProperty<HttpSpecCase>,
    @Internal val bgAppExecSpec: Property<DefaultBackgroundApplicationExecSpec>,
    @Internal private val objectFactory: ObjectFactory
) : HttpSpecRunnerExtension {

  @get:Internal
  override val backgroundApplicationExecSpec: Provider<BackgroundApplicationExecSpec>
    get() = bgAppExecSpec.map { it }

  override fun addSpec(specConfig: Action<HttpSpecCase>) {
    val name = objectFactory.property(String::class.java)
    val useFile = objectFactory.fileProperty()
    val httpSpecCase = DefaultHttpSpecCase(name, useFile)

    specConfig.execute(httpSpecCase)

    specs.add(httpSpecCase)
  }

  override fun runInBackground(backgroundAppSpec: Action<BackgroundApplicationExecSpec>) {
    val backgroundApplicationExecSpec = DefaultBackgroundApplicationExecSpec(objectFactory)
    backgroundAppSpec.execute(backgroundApplicationExecSpec)
    this.bgAppExecSpec.set(backgroundApplicationExecSpec)
  }

  fun waitApplicationReady() {
    val appExecSpec = bgAppExecSpec.get()
    appExecSpec.waitApplicationReady()
  }
}
