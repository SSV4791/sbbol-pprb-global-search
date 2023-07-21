plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
}

dependencies {
    annotationProcessor(liveLibs.lombok)
    compileOnly(liveLibs.lombok)

    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation(liveLibs.spring.boot.starter)
}

description =
    "Service ППРБ.Digital.Умный поиск. Загрузка документов в полнотекстовый индекс. Сервис загрузки"
