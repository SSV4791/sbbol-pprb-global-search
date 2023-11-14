plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
}

tasks {
    jar {
        enabled = false
    }
}

dependencies {
    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(liveLibs.spring.boot.starter.web)
}

description = "REST Controllers ППРБ.Digital.Умный поиск Task"
