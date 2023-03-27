plugins {
    id("dependency-locking-conventions")
    id("jacoco-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("test-conventions")
}
dependencies {
    api(project(":search-common:engine-core-api"))
    implementation(project(":search-common:facade-client-api"))
    implementation(project(":search-common:search-core-api"))

    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(liveLibs.commons.lang3)
    implementation(liveLibs.reflections)
    implementation(liveLibs.spring.boot.starter)

    testImplementation(testLibs.spring.boot.starter.test) {
        exclude("com.h2database", "h2")
        exclude("com.vaadin.external.google", "android-json")
    }
}

description = "Ядро полнотекстового поиска"
