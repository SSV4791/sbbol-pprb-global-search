plugins {
    id("repositories-conventions")
}

dependencies {
    api(liveLibs.bundles.opensearch)
}

description = "Фасад к клиенту взаимодействия с OpenSearch.API"
