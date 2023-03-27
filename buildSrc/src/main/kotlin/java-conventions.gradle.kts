import org.gradle.api.JavaVersion

plugins {
    `java-library`
    id("repositories-conventions")
}

subprojects {
    apply(plugin = "java-library")
}

group = "ru.sberbank.pprb.sbbol.global_search"

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    jar {
        enabled = false
    }
}
