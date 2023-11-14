plugins {
    id("dependency-locking-conventions")
    id("jacoco-conventions")
    id("java-conventions")
    id("org.springframework.boot")
    id("test-conventions")
}

tasks {
    jar {
        enabled = false
    }
}

dependencies {
    implementation(project(":search-task:rest"))

    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(liveLibs.http.healthcheck.starter)
    implementation(liveLibs.logstash.logback.encoder)
    implementation(liveLibs.spring.boot.starter)
}

description = "ППРБ.Digital.Умный поиск Task"
