package io.github.sgpublic.exsp.core

import org.gradle.api.Project

fun Project.applyInfo() {
    group = findProperty("exsp.group")
        ?: throw IllegalStateException("Unkonwn group!")
    version = findProperty("exsp.version")
        ?: throw IllegalStateException("Unkonwn version!")
}