package io.github.sgpublic.xxpref

import io.github.sgpublic.xxpref.base.XXPrefPublishingPlugin
import io.github.sgpublic.xxpref.core.applyAndroidPublishing
import org.gradle.api.Project

class XXPrefAndroidPublishingPlugin : XXPrefPublishingPlugin() {
    override fun configPublishing(target: Project) {
        target.applyAndroidPublishing()
    }
}