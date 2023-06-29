plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.asciidoctor.jvm.convert") version "2.4.0"
    id("repositories-conventions")
}

tasks {
    val buildAdminGuideDocs by registering(org.asciidoctor.gradle.jvm.AsciidoctorTask::class) {
        setSourceDir(file("asciidoc/admin-guide"))
        setOutputDir(file("${buildDir}/docs/admin-guide"))
        baseDirFollowsSourceDir()
        sources(delegateClosureOf<PatternSet> {
            include("index.adoc")
        })
    }

    val buildDevGuideDocs by registering(org.asciidoctor.gradle.jvm.AsciidoctorTask::class) {
        setSourceDir(file("asciidoc/dev-guide"))
        setOutputDir(file("${buildDir}/docs/dev-guide"))
        baseDirFollowsSourceDir()
        sources(delegateClosureOf<PatternSet> {
            include("index.adoc")
        })
        attributes(mapOf("source-highlighter" to "coderay"))
    }

    build {
        dependsOn(buildAdminGuideDocs)
        dependsOn(buildDevGuideDocs)
    }
}

description = "ППРБ. Фабика глобальный поиск модуль формирования документации."
