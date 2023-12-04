plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
}

dependencies {
    annotationProcessor(liveLibs.lombok)
    compileOnly(liveLibs.lombok)

    implementation(project(":search-search:api"))
    implementation(project(":search-sink:service"))
    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation(liveLibs.spring.boot.starter)
    implementation(liveLibs.spring.kafka)
}

description =
    "Service ППРБ.Digital.Умный поиск. Серверная часть загрузки документов в полнотекстовый индекс. Интеграция с кафкой"
