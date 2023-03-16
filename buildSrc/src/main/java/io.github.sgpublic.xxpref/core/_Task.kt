package io.github.sgpublic.xxpref.core

import io.github.sgpublic.xxpref.base.XXPrefPublishingPlugin
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import java.util.regex.Pattern
import javax.annotation.RegEx

fun Project.applyPublishingTask() {
    tasks.createIfAbsent("publishExspToMaven") {
        dependsOn(
            ":runtime:publishExspRuntimePublicationToOssrhRepository",
            ":compiler:publishExspCompilerPublicationToOssrhRepository",
        )
    }
}

fun TaskContainer.createIfAbsent(name: String, configurationAction: Action<in Task>) {
    findByName(name)?.let {
        XXPrefPublishingPlugin.LOGGER.info("task '$name' already exist.")
        return
    }
    register(name, configurationAction)
}

fun TaskContainer.filter(@RegEx regex: String): List<String> {
    val pattern = Pattern.compile(regex)
    XXPrefPublishingPlugin.LOGGER.warn(names.toString())
    return names.filter { pattern.matcher(it).matches() }
        .also { XXPrefPublishingPlugin.LOGGER.warn(it.toString()) }
}