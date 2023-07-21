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
        id("io.spring.dependency-management") version "1.0.11.RELEASE"
        id("org.openapi.generator") version "5.2.0"
        id("org.springframework.boot") version "2.5.1"
    }
}

plugins {
    id("com.gradle.enterprise") version "3.0"
}

gradleEnterprise {
    buildScan {
        server = "http://dev-sbbol2.sigma.sbrf.ru:8801"
        allowUntrustedServer = true
        isCaptureTaskInputFiles = true
        gradle.taskGraph.whenReady {
            publishAlwaysIf(!gradle.taskGraph.hasTask(":addCredentials"))
        }
    }
}

rootProject.name = "sbbol-pprb-global-search"

include(":docs")
include(":search-admin:runner")
include(":search-common:engine:core-api")
include(":search-common:engine:core-impl")
include(":search-common:engine:starter")
include(":search-common:facade:client")
include(":search-common:facade:client-api")
include(":search-common:facade:starter")
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
