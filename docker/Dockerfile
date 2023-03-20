FROM docker-internal.registry-ci.delta.sbrf.ru/ci02281165/ci03069386/bellsoft/liberica-openjdk-rhel7.7-205:15.0.2-10
COPY build/libs/sbbol-pprb-global-search*.jar draft.jar
COPY docker/entrypoint.sh entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["sh", "/entrypoint.sh"]
