plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("jacoco-conventions")
    id("org.springframework.boot") apply false
    id("test-conventions")
}

dependencies {
    implementation(project(":search-common:facade:client-api"))

    annotationProcessor(liveLibs.lombok)
    compileOnly(liveLibs.lombok)

    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(liveLibs.bundles.opensearch)
    implementation(liveLibs.commons.lang3)
    implementation(liveLibs.spring.boot.starter)
    implementation(liveLibs.spring.boot.starter.aop)
    implementation(liveLibs.spring.boot.starter.web)
}

description = "Фасад к клиенту взаимодействия с OpenSearch.API"
