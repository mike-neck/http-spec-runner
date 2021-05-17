package org.mikeneck.httpspec

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile

interface HttpSpecCase {

  @get:Input val name: Property<String>

  @get:InputFile val useFile: RegularFileProperty
}
