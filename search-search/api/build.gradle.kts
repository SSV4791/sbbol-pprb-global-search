plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
}

dependencies {
    api(project(":search-common:search-core-api"))

    implementation(liveLibs.jackson.annotations)
}

description = "Серверная часть полнотекстового поиска. API"
