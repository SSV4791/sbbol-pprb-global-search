plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("jacoco-conventions")
    id("org.springframework.boot")
    id("test-conventions")
}

springBoot {
    mainClass.set("ru.sberbank.pprb.sbbol.global_search.search.SearchRunnerApplication")
}

dependencies {
    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(project(":search-common:engine:starter"))
    implementation(project(":search-common:facade:starter"))
    implementation(project(":search-common:search-core-api"))
    implementation(project(":search-search:api"))
    implementation(project(":search-search:rest"))
    implementation(project(":search-search:service"))

    implementation(liveLibs.http.healthcheck.starter)
    implementation(liveLibs.logstash.logback.encoder)
    implementation(liveLibs.spring.boot.starter)
    implementation(liveLibs.spring.boot.starter.web)

    testImplementation(liveLibs.mapstruct.core)
    testImplementation(testLibs.bundles.pact)
    testImplementation(testLibs.bundles.rest.assured)
    testImplementation(testLibs.spring.boot.starter.test) {
        exclude("com.h2database", "h2")
        exclude("com.vaadin.external.google", "android-json")
    }
}

description = "ППРБ.Digital.Умный поиск Search"
