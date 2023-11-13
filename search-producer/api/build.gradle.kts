plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("publish-lib-conventions")
    id("org.springframework.boot") apply false
}

dependencies {
    api(project(":search-search:api"))
    annotationProcessor(liveLibs.lombok)
    compileOnly(liveLibs.lombok)
}

description = "API вызгрузки документов в систему Глобального Поиска"
