package org.mikeneck.httpspec

import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.nio.file.Path

class TestProjectBuilder: ParameterResolver, BeforeEachCallback {

    operator fun <T: Any> java.util.Optional<T>.invoke(): T? = this.orElse(null)

    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        if (extensionContext == null) {
            return false
        }
        val testMethod = extensionContext.testMethod() ?: return false
        val files = testMethod.getAnnotation(Files::class.java) ?: return false
        if (files.value.isEmpty()) {
            return false
        }
        extensionContext.testClass()?.simpleName ?: return false
        if (parameterContext == null) {
            return false
        }
        if (System.getenv("PROJECT_PARENT") == null) {
            return false
        }
        val parameter = parameterContext.parameter
        return parameter.type.isAssignableFrom(TestProject::class.java)
    }

    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Any {
        val extContext = shouldBeNotNull(extensionContext, "extensionContext", "extension-context is null")
        val testMethod = shouldBeNotNull(extContext.testMethod(), "testMethod", "test method is null")
        val files = shouldBeNotNull(testMethod.getAnnotation(Files::class.java), "File", "@Files annotation is null")
        if (files.value.isEmpty()) {
            throw IllegalStateException("unexpected state[@File in @Files annotation is empty]")
        }
        val className = shouldBeNotNull(extContext.testClass()?.simpleName, "test-class", "test class is null")
        val parameter = shouldBeNotNull(parameterContext?.parameter, "parameterContext.parameter", "parameter is null")
        if (!parameter.type.isAssignableFrom(TestProject::class.java)) {
            throw IllegalStateException("unexpected state[parameter is not GradleRunner]")
        }
        val parentRoot = shouldBeNotNull(System.getenv("PROJECT_PARENT"), "projectRoot", "environment variable PROJECT_PARENT is null")
        val parentDir = Path.of(parentRoot).toFile()
        val projectDir = parentDir.resolve(className)
        return TestProject(projectDir)
    }

    private fun <T: Any> shouldBeNotNull(any: T?, name: String, messageIfNull: String): T {
return any ?:           throw IllegalStateException("unexpected state @$name[$messageIfNull]")
    }

    override fun beforeEach(extensionContext: ExtensionContext?) {
        val context = shouldBeNotNull(extensionContext, "beforeEach", "extension-context is null")
        val testMethod = context.testMethod() ?: throw IllegalStateException("unexpected state[test method is null]")
        val files = testMethod.getAnnotation(Files::class.java) ?: throw IllegalStateException("unexpected state[@Files annotation is null]")
        if (files.value.isEmpty()) {
            throw IllegalStateException("unexpected state[@File in @Files annotation is empty]")
        }
        val className = shouldBeNotNull(context.testClass()?.simpleName, "test-class", "test class is null")

        val parentRoot = shouldBeNotNull(System.getenv("PROJECT_PARENT"), "projectRoot", "environment variable PROJECT_PARENT is null")
        val parentDir = Path.of(parentRoot).toFile()
        val projectDir = parentDir.resolve(className)

        for (file in files.value) {
            val filePath = projectDir.resolve(file.path)
            val dir = filePath.parentFile
            if (!dir.exists()) {
                dir.mkdirs()
            }
            filePath.writeText(file.contents, Charsets.UTF_8)
        }
        val settingsGradleFile = projectDir.resolve("settings.gradle")
        settingsGradleFile.writeText("rootProject.name = '$className'")
    }
}
