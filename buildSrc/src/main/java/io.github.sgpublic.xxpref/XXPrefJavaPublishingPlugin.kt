package io.github.sgpublic.xxpref

import io.github.sgpublic.xxpref.base.XXPrefPublishingPlugin
import io.github.sgpublic.xxpref.core.applyJavaPublishing
import org.gradle.api.Project

class XXPrefJavaPublishingPlugin : XXPrefPublishingPlugin() {
    override fun configPublishing(target: Project) {
        target.applyJavaPublishing()
    }
}