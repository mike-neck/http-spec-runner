package org.mikeneck.httpspec

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory

interface HttpSpecRunnerTaskProperties {

  @get:Internal val backgroundApplicationExecSpec: Property<BackgroundApplicationExecSpec>

  @get:Input @get:Nested val specs: ListProperty<HttpSpecCase>

  @get:OutputDirectory val reportDirectory: DirectoryProperty
}
