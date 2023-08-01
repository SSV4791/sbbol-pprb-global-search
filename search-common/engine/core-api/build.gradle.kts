description = "Ядро сервиса полнотекстового поиска. API"

plugins {
    id("dependency-locking-conventions")
    id("repositories-conventions")
}

dependencies {
    annotationProcessor(liveLibs.lombok)
    compileOnly(liveLibs.lombok)
}
