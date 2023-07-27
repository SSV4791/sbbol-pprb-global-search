plugins {
    id("dependency-locking-conventions")
    id("jacoco-conventions")
    id("java-conventions")
    id("repositories-conventions")
    id("org.springframework.boot") apply false
}
dependencies {

    api(project(":search-common:engine:core-impl"))
    implementation(project(":search-common:facade:starter"))

    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(liveLibs.spring.boot.starter)

}

description = "Starter для модуля engine"
