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
            url = uri("https://nexus-ci.delta.sbrf.ru/repository/maven-proxy-lib-internal/")
            credentials {
                username = tokenName
                password = tokenPassword
            }
            isAllowInsecureProtocol = true
        }
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
