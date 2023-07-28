plugins {
    id("dependency-locking-conventions")
    id("repositories-conventions")
}

dependencies {
    api(liveLibs.bundles.opensearch)

    annotationProcessor(liveLibs.lombok)
    compileOnly(liveLibs.lombok)
}

description = "Фасад к клиенту взаимодействия с OpenSearch.API"
