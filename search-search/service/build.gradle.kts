plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
}

tasks {
    compileJava {
        options.compilerArgs.addAll(
            listOf(
                "-Amapstruct.defaultComponentModel=spring",
                "-Amapstruct.defaultInjectionStrategy=constructor"
            )
        )
    }
}

dependencies {
    annotationProcessor(liveLibs.mapstruct.processor)
    annotationProcessor(liveLibs.lombok)

    compileOnly(liveLibs.lombok)

    implementation(project(":search-search:api"))
    implementation(project(":search-common:engine:starter"))

    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(liveLibs.spring.boot.starter)
    implementation(liveLibs.mapstruct.core)
}

description = "Service ППРБ.Digital.Умный поиск"
