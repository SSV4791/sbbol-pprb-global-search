tasks.register<Zip>("fullDistrib") {
    destinationDirectory.set(file("${rootProject.buildDir}"))
    archiveFileName.set("${rootProject.name}.zip")
    from("${rootDir}/openshift/") {
        into("openshift")
        exclude("Deployment*")
    }
    from("${rootDir}/openshift/") {
        into("openshift")
        include("Deployment*")
        filter { line: String ->
            line.replace(
                "{app_admin_docker_image}",
                project.properties["adminDockerImage"] as String
            )
        }
        filter { line: String ->
            line.replace(
                "{app_search_docker_image}",
                project.properties["searchDockerImage"] as String
            )
        }
        filter { line: String ->
            line.replace(
                "{app_sink_docker_image}",
                project.properties["sinkDockerImage"] as String
            )
        }
        filter { line: String ->
            line.replace(
                "{app_task_docker_image}",
                project.properties["taskDockerImage"] as String
            )
        }
    }
    from("$rootDir/docs") {
        into("docs")
    }
    from("${rootDir}/src/main/resources/db/changelog/") {
        into("liquibase")
    }
    from("${rootDir}/gradle") {
        include("dependency-locks/*")
        into("locks")
    }
    from("${rootDir}/gradle/wrapper") {
        include("gradle-wrapper.properties")
        into("locks")
    }
    from("${rootDir}/src/main/resources/audit/") {
        into("package/conf/data/audit_metamodel/")
    }
    from("${rootDir}/vectors/") {
        into("vectors")
    }
    from("${rootDir}/search-updater/build/libs") {
        into("search-updater")
    }
}
