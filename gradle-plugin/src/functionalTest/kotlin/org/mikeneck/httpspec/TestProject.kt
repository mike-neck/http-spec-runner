package org.mikeneck.httpspec

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

class TestProject(
    private val projectRoot: File
) {
    fun gradle(vararg args: String): BuildResult =
        GradleRunner.create()
            .withProjectDir(projectRoot)
            .withArguments(*args)
            .withPluginClasspath()
            .build()

    fun file(path: String): File = projectRoot.resolve(path)
}
