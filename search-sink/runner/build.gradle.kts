plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("jacoco-conventions")
    id("org.springframework.boot")
    id("test-conventions")
}

dependencies {
    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(project(":search-sink:integration"))

    implementation(liveLibs.http.healthcheck.starter)
    implementation(liveLibs.logstash.logback.encoder)
    implementation(liveLibs.spring.boot.starter)
    implementation(liveLibs.spring.boot.starter.validation)
    implementation(liveLibs.spring.boot.starter.web)

    testImplementation(liveLibs.mapstruct.core)
    testImplementation(testLibs.bundles.pact)
    testImplementation(testLibs.bundles.rest.assured)
    testImplementation(testLibs.spring.boot.starter.test) {
        exclude("com.h2database", "h2")
        exclude("com.vaadin.external.google", "android-json")
    }
}

description = "Service ППРБ.Digital.Умный поиск. Загрузка документов в полнотекстовый индекс. Запуск службы"
