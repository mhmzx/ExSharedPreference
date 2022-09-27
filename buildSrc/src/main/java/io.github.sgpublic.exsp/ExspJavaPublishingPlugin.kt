package io.github.sgpublic.exsp

import io.github.sgpublic.exsp.base.ExspPublishingPlugin
import io.github.sgpublic.exsp.core.applyJavaPublishing
import org.gradle.api.Project

class ExspJavaPublishingPlugin : ExspPublishingPlugin() {
    override fun configPublishing(target: Project) {
        target.applyJavaPublishing()
    }
}