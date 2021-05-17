package org.mikeneck.httpspec

import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.process.JavaExecSpec

interface HttpSpecRunnerExtension {

  val reportDirectory: DirectoryProperty

  fun addSpec(specConfig: Action<HttpSpecCase>)

  fun runInBackground(backgroundAppSpec: Action<JavaExecSpec>)
}
