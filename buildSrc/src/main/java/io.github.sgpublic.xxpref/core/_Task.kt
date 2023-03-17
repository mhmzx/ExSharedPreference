package io.github.sgpublic.xxpref.core

import io.github.sgpublic.xxpref.base.XXPrefPublishingPlugin
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import java.util.regex.Pattern
import javax.annotation.RegEx

fun Project.applyPublishingTask() {
    tasks.createIfAbsent("publishXXPrefToMavenCentral") {
        dependsOn(
            ":common:publishXXPrefCommonPublicationToOssrhRepository",
            ":runtime:publishXXPrefRuntimePublicationToOssrhRepository",
            ":compiler-java:publishXXPrefCompilerJavaPublicationToOssrhRepository",
            ":compiler-kotlin:publishXXPrefCompilerKotlinPublicationToOssrhRepository",
        )
    }
    tasks.createIfAbsent("publishXXPrefToMavenLocal") {
        dependsOn(
            ":common:publishXXPrefCommonPublicationToMavenLocal",
            ":runtime:publishXXPrefRuntimePublicationToMavenLocal",
            ":compiler-java:publishXXPrefCompilerJavaPublicationToMavenLocal",
            ":compiler-kotlin:publishXXPrefCompilerKotlinPublicationToMavenLocal",
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