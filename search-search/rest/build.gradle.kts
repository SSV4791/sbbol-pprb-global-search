import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.openapi.generator")
    id("org.springframework.boot") apply false
}

dependencies {
    implementation(project(":search-search:api"))

    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation(liveLibs.spring.boot.starter.web)
    implementation(liveLibs.jackson.databind.nullable)
    implementation(liveLibs.springdoc.openapi.ui)
}

description = "REST Controllers ППРБ.Digital.Умный поиск"

val openApiGeneratorInputDir = "${project(":search-search").projectDir}/openapi/rest/"
val restGeneratorOutputDir = "${project(":search-search:rest").buildDir}/generated/sources"
tasks {
    register("restSearchGenerate", GenerateTask::class) {
        inputSpec.set("$openApiGeneratorInputDir/search.yml")
        outputDir.set(restGeneratorOutputDir)
        generatorName.set("spring")
        generateAliasAsModel.set(false)
        generateApiTests.set(false)
        generateApiDocumentation.set(false)
        generateModelTests.set(false)
        modelPackage.set("ru.sberbank.pprb.sbbol.global_search.search.model")
        apiPackage.set("ru.sberbank.pprb.sbbol.global_search.search")
        globalProperties.putAll(
            mapOf(
                "apis" to ""
            )
        )
        configOptions.putAll(
            mapOf(
                "dateLibrary" to "java8",
                "interfaceOnly" to "true",
                "serializableModel" to "true",
                "skipDefaultInterface" to "true",
                "useTags" to "true",
            )
        )
    }
    compileJava {
        dependsOn("restSearchGenerate")
    }
}

sourceSets {
    main {
        java {
            srcDir("$restGeneratorOutputDir/src/main/java")
        }
    }
}
