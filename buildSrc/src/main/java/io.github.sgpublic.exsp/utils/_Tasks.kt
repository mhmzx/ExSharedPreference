package io.github.sgpublic.exsp.utils

import io.github.sgpublic.exsp.base.ExspPublishingPlugin
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

fun TaskContainer.execute(name: String) {
    if (!names.contains(name)) {
        ExspPublishingPlugin.LOGGER.warn("task :$name not found!")
        return
    }
    execute(getByName(name))
}

fun TaskContainer.execute(task: Task) {
    for (item in task.dependsOn) {
        when (item) {
            is String -> {
                execute(item)
            }
            is Task -> {
                execute(item)
            }
        }
    }
    for (action in task.actions) {
        action.execute(task)
    }
}
