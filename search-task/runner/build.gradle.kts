plugins {
    id("dependency-locking-conventions")
    id("jacoco-conventions")
    id("java-conventions")
    id("org.springframework.boot")
    id("test-conventions")
}

dependencies {
    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(liveLibs.http.healthcheck.starter)
    implementation(liveLibs.logstash.logback.encoder)
    implementation(liveLibs.spring.boot.starter)
}

description = "ППРБ.Digital.Умный поиск Task"
