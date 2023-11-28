plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("publish-lib-conventions")
    id("org.springframework.boot") apply false
}

dependencies {
    implementation(project(":search-producer:search-producer-api"))
    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation(liveLibs.spring.kafka)
}

description = "Реализация API dызгрузки документов в систему Глобального Поиска через топик кафки. "
