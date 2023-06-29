#!/bin/sh

../entrypoint-common.sh

# Определение кастомных енвов
if [ -e /vault/secrets/environments ]; then
    custom_envs=`cat /vault/secrets/environments/*`
else
    custom_envs=''
fi

if [ "$RUN_DEBUG" == "true" ];
then
  export DEBUG_CONFIG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000"
fi
# Запуск приложения
env $custom_envs sh -c '
exec java $DEBUG_CONFIG -jar search-search.jar
'
