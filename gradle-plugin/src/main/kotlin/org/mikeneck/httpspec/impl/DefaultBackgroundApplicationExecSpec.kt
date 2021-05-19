package org.mikeneck.httpspec.impl

import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.process.JavaExecSpec
import org.mikeneck.httpspec.BackgroundApplicationExecSpec
import org.mikeneck.httpspec.listProperty
import org.mikeneck.httpspec.mapProperty
import org.mikeneck.httpspec.property

class DefaultBackgroundApplicationExecSpec(
    override val classpath: FileCollection,
    override val args: ListProperty<String>,
    override val mainClass: Property<String>,
    override val jvmArgs: ListProperty<String>,
    override val environment: MapProperty<String, String>
) : BackgroundApplicationExecSpec {

  override fun execute(javaExec: JavaExecSpec) {
    javaExec.mainClass.set(mainClass)
    javaExec.classpath(classpath)
    javaExec.jvmArgs(jvmArgs)
    javaExec.args(args)
    javaExec.environment(environment.get())
  }

  companion object {
    operator fun invoke(objectFactory: ObjectFactory): BackgroundApplicationExecSpec =
        DefaultBackgroundApplicationExecSpec(
            objectFactory.fileCollection(),
            objectFactory.listProperty(),
            objectFactory.property(),
            objectFactory.listProperty(),
            objectFactory.mapProperty())
  }
}
