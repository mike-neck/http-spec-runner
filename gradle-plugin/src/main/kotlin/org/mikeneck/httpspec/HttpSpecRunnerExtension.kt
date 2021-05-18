package org.mikeneck.httpspec

import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory

interface HttpSpecRunnerExtension {

  @get:OutputDirectory val reportDirectory: DirectoryProperty

  fun addSpec(specConfig: Action<HttpSpecCase>)

  fun runInBackground(backgroundAppSpec: Action<BackgroundApplicationExecSpec>)

  companion object {
    @JvmStatic val DEFAULT_REPORT_DIRECTORY: String = "http-spec-runner"
  }
}
