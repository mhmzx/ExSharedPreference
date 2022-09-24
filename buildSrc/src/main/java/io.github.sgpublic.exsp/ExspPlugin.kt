package io.github.sgpublic.exsp

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import io.github.sgpublic.exsp.core.applyInfo
import io.github.sgpublic.exsp.core.applyPublising
import org.gradle.internal.logging.slf4j.OutputEventListenerBackedLogger

class ExspPlugin(): Plugin<Project> {
    override fun apply(project: Project) {
        ExspPlugin.rootProject = project.rootProject
        ExspPlugin.logger = project.logger
        project.applyInfo()
        project.applyPublising("publising.properties")
    }

    companion object {
        lateinit var rootProject: Project private set
        lateinit var logger: Logger private set
    }
}