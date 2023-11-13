plugins {
    id("dependency-locking-conventions")
    id("jacoco-conventions")
    id("java-conventions")
    id("repositories-conventions")
    id("publish-lib-conventions")
    id("org.springframework.boot") apply false
}
dependencies {
    api(project(":search-producer:search-producer-api"))
    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation(project(":search-producer:search-producer-impl"))
    implementation(liveLibs.jackson.databind)
    implementation(liveLibs.spring.boot.starter)
    implementation(liveLibs.spring.kafka)
}

description = "Starter для модуля search-producer выгрузки документов в систему Глобального Поиска"
