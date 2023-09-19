
plugins {
    id("com.intershop.gradle.javacc")
    id("dependency-locking-conventions")
    id("jacoco-conventions")
    id("org.springframework.boot") apply false
    id("test-conventions")
}

dependencies {
    implementation(project(":search-common:facade:client-api"))
    implementation(project(":search-common:facade:client"))
    implementation(project(":search-common:facade:facade-starter"))

    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(liveLibs.commons.cli)
    implementation(liveLibs.commons.lang3)
    implementation(liveLibs.jackson.databind)
    implementation(liveLibs.spring.web)
    implementation(liveLibs.spring.boot.starter)

    testImplementation(testLibs.spring.boot.starter.test) {
        exclude("com.h2database", "h2")
        exclude("com.vaadin.external.google", "android-json")
    }
}

val javaccOutputDir = file("$buildDir/generated/sources/javacc")

javacc {
    javaCCVersion = "6.1.2"
    configs {
        create("config") {
            javaUnicodeEscape = "true"
            jdkVersion = JavaVersion.VERSION_1_8.toString()
            inputFile = file("src/main/javacc/parser/IndexQueryGrammar.jj")
            outputDir = javaccOutputDir
            packageName = "ru/sbrf/sbbol/search/updater/parser"
            staticParam = "false"
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs(javaccOutputDir)
        }
    }
}

tasks.compileJava{
    dependsOn("javacc")
}

tasks.jar {
    dependsOn(configurations.runtimeClasspath)
    manifest {
        attributes["Main-Class"] = "ru.sbrf.sbbol.search.updater.OpenSearchUpdaterApplication"
    }
    from(configurations.runtimeClasspath.get().map(::zipTree))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

description = "Модуль проливки скриптов обновлений в OpenSearch"
