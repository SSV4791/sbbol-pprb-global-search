plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.openapi.generator")
    id("org.springframework.boot") apply false
}

dependencies {
    implementation(project(":search-search:api"))

    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation(liveLibs.spring.boot.starter.web)
    implementation(liveLibs.jackson.databind.nullable)
    implementation(liveLibs.springdoc.openapi.ui)
}

description = "REST Controllers ППРБ.Digital.Умный поиск"


