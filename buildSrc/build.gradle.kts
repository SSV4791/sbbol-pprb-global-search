
plugins {
    `java-library`
}
val tokenName = project.properties["tokenName"] as String?
val tokenPassword = project.properties["tokenPassword"] as String?

repositories {
    maven {
        url = uri("https://nexus-ci.delta.sbrf.ru/repository/maven-proxy-lib-internal/")
        credentials {
            username = tokenName
            password = tokenPassword
        }
        isAllowInsecureProtocol = true
    }
    maven {
        url = uri("https://nexus-ci.delta.sbrf.ru/repository/public/")
        credentials {
            username = tokenName
            password = tokenPassword
        }
        isAllowInsecureProtocol = true
    }
}

dependencies {
    implementation("ru.sbt.meta:meta-gradle-plugin:1.4.0")
}