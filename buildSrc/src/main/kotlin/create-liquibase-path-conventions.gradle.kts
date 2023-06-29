tasks.register("newpatch") {
    description = "Создание sql патча"
    var currentVersion = ""

    doFirst {
        if (!project.hasProperty("patchname")) {
            throw IllegalArgumentException("Property patchname not provided")
        }
        val patchname = project.property("patchname")
        currentVersion = if (project.hasProperty("releaseversion")) {
            project.property("releaseversion").toString()
        } else {
            try {
                //От текущей версии вида major.manor.hotfix_build откидываем последнюю группу с build
                //например от 01.000.00_123-SNAPSHOT получаем версию вида 01.000.00
                //для минора например от 01.000.00_123-SNAPSHOT получаем версию вида 01.000.00 и т.д.
                project.property("version").toString().split("\\_")[0]
            } catch (e: Exception) {
                throw GradleException("Unable to parse version parameter value $version from gradle.properties", e)
            }
        }
        val path = "${projectDir}/src/main/resources/db/changelog"
        val patchDirectory = "sql/${currentVersion}"
        val patchPath = "${path}/${patchDirectory}"

        val folder = File(patchPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val patchTs = System.currentTimeMillis()

        val updateFileName = "${patchTs}_${patchname}.sql"
        val updateFilePath = "${patchPath}/${updateFileName}"

        val patchText = "-- liquibase formatted sql"
        File(updateFilePath).writeText(patchText, Charsets.UTF_8)
        println("File $updateFileName was created")

        val changelog = "changelog.yaml"
        val changelogFilePath = "${patchPath}/${changelog}"
        val mainChangeLogFilePath = "${path}/${changelog}"
        val file = File(changelogFilePath)
        var changelogText =
            """  - include:
      file: $updateFileName
      relativeToChangelogFile: true
"""
        if (file.length().toInt() == 0) {
            var mainChangelogText =
                """  - include:
      file: ${patchDirectory}/${changelog}
      relativeToChangelogFile: true
"""
            val mainChangeLogFile = File(mainChangeLogFilePath)
            if (mainChangeLogFile.length().toInt() == 0) {
                mainChangeLogFile.appendText("databaseChangeLog:\n${mainChangelogText}", Charsets.UTF_8)
            } else {
                if (!mainChangeLogFile.readText(Charsets.UTF_8).endsWith("\n")) {
                    mainChangelogText = "\n" + mainChangelogText
                }
                mainChangeLogFile.appendText(mainChangelogText, Charsets.UTF_8)
            }
            file.appendText("databaseChangeLog:\n${changelogText}", Charsets.UTF_8)
        } else {
            if (!file.readText(Charsets.UTF_8).endsWith("\n")) {
                changelogText = "\n" + changelogText
            }
            file.appendText(changelogText, Charsets.UTF_8)
        }
    }
}
