package org.mikeneck.httpspec

import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory

interface HttpSpecRunnerTask : Task {

  @get:Internal val backgroundApplicationExecSpec: BackgroundApplicationExecSpec

  @get:Nested val specs: ListProperty<HttpSpecCase>

  @get:OutputDirectory val reportDirectory: DirectoryProperty

  fun addSpec(specConfig: Action<HttpSpecCase>)
}
