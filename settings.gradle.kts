pluginManagement {
    repositories {
        val tokenName: String by settings
        val tokenPassword: String by settings
        maven {
            url = uri("https://nexus-ci.delta.sbrf.ru/repository/public/")
            credentials {
                username = tokenName
                password = tokenPassword
            }
            isAllowInsecureProtocol = true
        }

        maven {
            url = uri("https://nexus-ci.delta.sbrf.ru/repository/maven-lib-int/")
            credentials {
                username = tokenName
                password = tokenPassword
            }
            isAllowInsecureProtocol = true
        }
    }
    @Suppress("UnstableApiUsage")
    dependencyResolutionManagement {
        @Suppress("UnstableApiUsage")
        versionCatalogs {
            create("liveLibs") {
                from(files("gradle/libs.versions.toml"))
            }
            create("testLibs") {
                from(files("gradle/test-libs.versions.toml"))
            }
        }
    }
    plugins {
        id("com.intershop.gradle.javacc") version "3.0.3"
        id("io.spring.dependency-management") version "1.0.11.RELEASE"
        id("org.openapi.generator") version "6.4.0"
        id("org.springframework.boot") version "2.5.1"
    }
}

rootProject.name = "sbbol-pprb-global-search"

include(":docs")
include(":search-admin:runner")
include(":search-admin:rest")
include(":search-common:engine:core-api")
include(":search-common:engine:core-impl")
include(":search-common:engine:starter")
include(":search-common:facade:client")
include(":search-common:facade:client-api")
include(":search-common:facade:facade-starter")
include(":search-common:search-core-api")
include(":search-search:api")
include(":search-search:rest")
include(":search-search:runner")
include(":search-search:service")
include(":search-sink:api")
include(":search-sink:integration")
include(":search-sink:runner")
include(":search-sink:service")
include(":search-task:rest")
include(":search-task:runner")
include(":search-task:service")
include(":search-updater")
include(":search-producer:api")
include(":search-producer:impl")
include(":search-producer:starter")

project(":search-producer:api").name = "search-producer-api"
project(":search-producer:impl").name = "search-producer-impl"
project(":search-producer:starter").name = "search-producer-starter"
