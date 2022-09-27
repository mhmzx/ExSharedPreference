package io.github.sgpublic.exsp

import io.github.sgpublic.exsp.base.ExspPublishingPlugin
import io.github.sgpublic.exsp.core.applyAndroidPublishing
import org.gradle.api.Project

class ExspAndroidPublishingPlugin : ExspPublishingPlugin() {
    override fun configPublishing(target: Project) {
        target.applyAndroidPublishing()
    }
}