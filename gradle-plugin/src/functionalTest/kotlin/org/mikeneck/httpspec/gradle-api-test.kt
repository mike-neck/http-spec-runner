package org.mikeneck.httpspec

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

fun BuildResult.successTaskPaths(): List<String> =
    this.tasks(TaskOutcome.SUCCESS).map { it.path }
