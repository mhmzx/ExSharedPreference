package io.github.sgpublic.exsp.core

import org.gradle.api.Project

fun Project.applyInfo() {
    group = "io.github.sgpublic"
    version = findProperty("exsp.version")
        ?: throw IllegalStateException("Unkonwn version!")
}