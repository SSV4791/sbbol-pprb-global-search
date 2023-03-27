import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    jacoco
    id("create-liquibase-path-conventions")
    id("create-release-conventions")
    id("dependency-locking-conventions")
    id("idea-conventions")
    id("java-conventions")
    id("publish-release-conventions")
    id("repositories-conventions")
    id("org.sonarqube") version "3.2.0"
    id("ru.sbt.meta.meta-gradle-plugin")
    id("ru.sbrf.build.gradle.qa.reporter") version "3.3.4"
}

//нужны для meta
val nexusLogin = project.properties["nexusLogin"] as String?
val nexusPassword = project.properties["nexusPassword"] as String?

val tokenName = project.properties["tokenName"] as String?
val tokenPassword = project.properties["tokenPassword"] as String?

jacoco {
    toolVersion = "0.8.7"
}

tasks.register<Test>("generateVectorTest") {
    useJUnitPlatform()
    filter { includeTestsMatching("**changevector.generate.*") }
}
tasks.register<Test>("applyVectorTest") {
    useJUnitPlatform()
    filter { includeTestsMatching("**changevector.apply.*") }
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
    exclude("**/changevector/**")
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
    systemProperty("file.encoding", "UTF-8")
}

//TODO: Настроить пути для исключений, которые не попадут в анализа покрытия тестами кода проекта.
val coverageExclusions = listOf(
    // Классы с конфигурациями
    "**/*Application*",
    //POJO
    "**/model/**",
    "**/transport/**",
    "**/deserializer/**",
    "**/monitoring/**",
    "**/enums/**",
    "**/entity/**",
    "**/dto/**",
    //Классы с контроллерами и вызовами сервисов без логики, в которых происходит только вызов соответствующего сервиса
    "**/*Invoker*",
    "**/*Controller*",
    "**/*Adapter*",
    //Классы с заглушками для локальной разработки
    "**/*Stub*",
    //Сериализаторы/десериализаторы
    "**/identifier/*Deserializer*",
    "**/identifier/*Serializer*",
    "**/handler/*Handler*",
    //Классы с exception
    "**/exception/**",
    //Инфраструктура
    "**/security/CryptoProvider*",
    "**/security/CryptoService*",
    "**/processor/*Processor*",
    "**/swagger/**",
    "**/infrastructure/**",
    "**/*Aspect*",
    "**/*Config*"
)

tasks {
    register("sonarCoverage", DefaultTask::class) {
        group = "verification"
        dependsOn(jacocoTestReport)
        finalizedBy(sonarqube)
    }

    qaReporterUpload {
        //Добавляем фильтры для классов или целых пакетов, которые не должны учитываться в покрытии Jacoco
        jacocoExcludes.addAll(coverageExclusions)
    }
}


// отключаем генерацию *-plain.jar
val jar: Task by tasks.getting {
    enabled = false
}

jacoco {
    toolVersion = "0.8.6"
}

//meta {
//    nexusUrl = null
//    nexusUser = nexusLogin
//    nexusPassword = this@Build_gradle.nexusPassword
//    componentId = "pprb-draft" // TODO replace with component id from META
//    ext {
//        set("url", "https://meta.sigma.sbrf.ru")
//        set("openApiSpecs", listOf("openapi/openapi.yml"))
//        set("analyzeJava", false)
//        set("failBuildOnError", true)
//    }
//}

sonarqube {
    properties {
        property("sonar.coverage.jacoco.xmlReportPaths", "${rootProject.buildDir}/coverage/jacoco/jacocoTestReport.xml")
        property("sonar.coverage.exclusions", coverageExclusions.joinToString())
        property(
                "sonar.cpd.exclusions", """
                    src/main/java/ru/sberbank/pprb/sbbol/draft/entity/**,
                    src/main/java/ru/sberbank/pprb/sbbol/draft/dto/**
                """.trimIndent()
        )

    }
}

java.sourceSets["test"].java {
    srcDir("$buildDir/generated/openapiclient/src/main/java")
}

qaReporter {
    // FIXME COVERAGE: Прописать идентификатор вашего приложения для корректной выгрузки покрытий в портал http://10.21.25.54:8812/coverage
    // Для получения идентификатора обратиться к команде Сценаристы (AUTOTEST5 https://confluence.sberbank.ru/pages/viewpage.action?pageId=768508170).
    projectKey.set("sbbol-pprb-global-search")
}
