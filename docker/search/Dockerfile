FROM docker-internal.registry-ci.delta.sbrf.ru/ci02281165/ci03069386/bellsoft/liberica-openjdk-rhel7.7-205:15.0.2-10
COPY search-search/runner/build/libs/runner*.jar search-search.jar
COPY docker/entrypoint-common.sh entrypoint-common.sh
COPY docker/search/entrypoint.sh entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["sh", "/entrypoint.sh"]
