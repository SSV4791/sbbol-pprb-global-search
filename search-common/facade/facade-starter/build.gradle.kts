plugins {
    id("dependency-locking-conventions")
    id("jacoco-conventions")
    id("repositories-conventions")
    id("org.springframework.boot") apply false
}
dependencies {

    api(project(":search-common:facade:client-api"))
    api(project(":search-common:facade:client"))

    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(liveLibs.spring.boot.starter)
    implementation(liveLibs.jackson.databind)

}

description = "Starter для модуля facade"
