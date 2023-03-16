package io.github.sgpublic.xxpref.core

import org.gradle.api.Project

fun Project.applyInfo() {
    group = "io.github.sgpublic"
    version = findProperty("xxpref.version")
        ?: throw IllegalStateException("Unkonwn XXPreference version!")
}