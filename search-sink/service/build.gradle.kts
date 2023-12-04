plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
}

dependencies {
    annotationProcessor(liveLibs.lombok)
    annotationProcessor(liveLibs.mapstruct.processor)

    compileOnly(liveLibs.lombok)

    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation(project(":search-common:engine:starter"))
    implementation(project(":search-common:facade:facade-starter"))
    implementation(project(":search-search:api"))
    implementation(liveLibs.commons.lang3)
    implementation(liveLibs.mapstruct.core)
    implementation(liveLibs.spring.boot.starter)
}

description =
    "Service ППРБ.Digital.Умный поиск. Загрузка документов в полнотекстовый индекс. Сервис загрузки"
