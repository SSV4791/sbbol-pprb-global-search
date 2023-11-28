import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get

plugins {
    `maven-publish`
}

if (project.properties["publishApi"] == "true") {
    publishing {
        val tokenName = project.properties["tokenName"] as String?
        val tokenPassword = project.properties["tokenPassword"] as String?

        publications {
            create<MavenPublication>("api") {
                from(components["java"])
                groupId = "ru.sberbank.pprb.sbbol.global_search.producer"
                artifactId = project.name
            }
        }

        repositories {
            maven {
                name = "api"
                url = uri(project.properties["libRepositoryUrl"]!!)
                isAllowInsecureProtocol = true
                credentials {
                    username = tokenName
                    password = tokenPassword
                }
            }
        }
    }
}

