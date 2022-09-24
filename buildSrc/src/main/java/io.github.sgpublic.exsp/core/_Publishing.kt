package io.github.sgpublic.exsp.core

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.internal.DefaultPublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPom
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication
import java.net.URI
import java.util.Properties

fun Project.applyPublising(prop: String) {
    val properties = Properties()
    rootProject.file(prop).inputStream().use {
        properties.load(it)
    }

    val projectName = this@applyPublising.rootProject.name + "-" + this@applyPublising.name
    val localRepo = properties.getProperty("exsp.publising.local", "")
        .takeIf { it != "" } ?: "file://${this@applyPublising.buildDir}/repo"
    val publishRemove = properties.getProperty("exsp.publising.remote", "false") == "true"
    val publisingUrl = properties.getProperty("exsp.publising.url", "")
        .takeIf { it != "" } ?: localRepo

    project.extensions.configure<PublishingExtension>("publishing") {
        repositories {
            maven {
                name = "local-repo"
                url = URI.create(localRepo)
            }
            if (publishRemove) {
                maven {
                    name = "ossrh"
                    url = URI.create("https://oss.sonatype.org/content/repositories/snapshots")
                }
            }
        }
        publications {
            register(projectName, MavenPublication::class.java) {
                groupId = this@applyPublising.group.toString()
                artifactId = projectName
                version = this@applyPublising.version.toString()

                pom {
                    name.set(projectName)
                    description.set(projectName)
                    url.set(publisingUrl)
                    licenses {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                    developers {
                        developer {
                            id.set(properties.getProperty("exsp.publising.developer.id", ""))
                            name.set(properties.getProperty("exsp.publising.developer.name", ""))
                            email.set(properties.getProperty("exsp.publising.developer.email", ""))
                        }
                    }
                }
            }
        }
    }
}