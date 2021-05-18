package org.mikeneck.httpspec.impl

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.mikeneck.httpspec.HttpSpecCase

class DefaultHttpSpecCase(
    override val name: Property<String>,
    override val useFile: RegularFileProperty
) : HttpSpecCase
