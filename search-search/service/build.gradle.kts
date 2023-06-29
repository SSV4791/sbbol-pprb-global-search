plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
}

dependencies {
    annotationProcessor(liveLibs.mapstruct.processor)

    implementation(project(":search-search:api"))
    implementation(project(":search-common:engine-core-api"))

    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(liveLibs.spring.boot.starter)
    implementation(liveLibs.mapstruct.core)
}

description = "Service ППРБ.Digital.Умный поиск"
