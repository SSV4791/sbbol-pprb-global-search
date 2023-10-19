import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.openapi.generator")
}

dependencies {
    api(project(":search-common:search-core-api"))

    annotationProcessor(liveLibs.lombok)
    compileOnly(liveLibs.lombok)

    implementation(liveLibs.jackson.annotations)
    implementation(liveLibs.jackson.databind.nullable)
    implementation(liveLibs.jakarta.annotation.api)
    implementation(liveLibs.springdoc.openapi.ui)
}

description = "Серверная часть полнотекстового поиска. API"

val openApiGeneratorInputDir = "${project(":search-search").projectDir}/openapi/rest/"
val apiGeneratorOutputDir = "${project(":search-search:api").buildDir}/generated/sources"
tasks {
    register("apiSearchGenerate", GenerateTask::class) {
        inputSpec.set("$openApiGeneratorInputDir/search.yml")
        outputDir.set(apiGeneratorOutputDir)
        generatorName.set("spring")
        generateAliasAsModel.set(false)
        generateApiTests.set(false)
        generateApiDocumentation.set(false)
        generateModelTests.set(false)
        modelPackage.set("ru.sberbank.pprb.sbbol.global_search.search.model")
        apiPackage.set("ru.sberbank.pprb.sbbol.global_search.search")
        globalProperties.putAll(
            mapOf(
                "models" to ""
            )
        )
        configOptions.putAll(
            mapOf(
                "dateLibrary" to "java8",
                "interfaceOnly" to "true",
                "serializableModel" to "true",
                "skipDefaultInterface" to "true",
                "useTags" to "true",
                "useOneOfInterfaces" to "true"
            )
        )
    }
    compileJava {
        dependsOn("apiSearchGenerate")
    }
}

sourceSets {
    main {
        java {
            srcDir("$apiGeneratorOutputDir/src/main/java")
        }
    }
}
