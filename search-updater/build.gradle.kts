import java.util.*

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
            packageName = "ru/sberbank/pprb/sbbol/global_search/updater/parser"
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

tasks {
    compileJava {
        dependsOn("javacc")
    }
    jar {
        dependsOn(configurations.runtimeClasspath)
        manifest {
            attributes["Main-Class"] = "ru.sberbank.pprb.sbbol.global_search.updater.OpenSearchUpdaterApplication"
        }
        from(configurations.runtimeClasspath.get().map(::zipTree))
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    val basePath = "$projectDir/src/main/resources/queries"
    register<JavaExec>("updateSearch") {
        systemProperties["config"] = "${projectDir}/src/main/resources/opensearch-updater.yml"
        mainClass.set("ru.sberbank.pprb.sbbol.global_search.updater.OpenSearchUpdaterApplication")
        classpath = sourceSets["main"].runtimeClasspath
        args = listOf("--path", basePath)
    }
    register("newSearchPatch") {
        description = "Generate template for Opensearch update script"

        val patchExtension = ".est"
        doFirst {
            val patchname: String by project
            val stage: String by project
            val allowedStages = listOf("before", "pipeline", "template", "after")

            require(project.hasProperty("patchname")) { "Property patchname not provided" }
            require(project.hasProperty("stage")) { "Property stage not provided" }
            require(stage in allowedStages) { "Illegal stage type! Must be one of: $allowedStages" }

            val folder = File(basePath, stage)
            if (!folder.exists()) {
                folder.mkdirs()
                println("Patch folder '$folder' created")
            }

            val patchTs = System.currentTimeMillis()
            val fullPatchName = "${folder.absolutePath}/${patchTs}_${patchname}${patchExtension}"
            val patchText = "## GUID=" + UUID.randomUUID().toString()

            val file = File(fullPatchName)
            require(file.createNewFile()) { "Unable to create patch file with name '$fullPatchName'" }
            file.writeText(patchText)
            println("File '$file' was created")
        }
    }

}
description = "Модуль проливки скриптов обновлений в OpenSearch"
