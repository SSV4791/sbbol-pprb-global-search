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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}
