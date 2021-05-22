package org.mikeneck.httpspec

import java.io.File
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile

interface HttpSpecCase {

  @get:Input val name: Property<String>

  fun setName(name: String)

  @get:InputFile val useFile: RegularFileProperty

  fun setUseFile(file: File)
}
