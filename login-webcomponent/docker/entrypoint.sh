#!/bin/sh
echo "Starting NGINX..."

ALLOW_ORIGIN="${ALLOW_ORIGIN:-'*'}" \
envsubst < ${DOCKER_ENV_NGINX_DEFAULT_CONF_PATH}/files.conf.template > ${DOCKER_ENV_NGINX_DEFAULT_CONF_PATH}/files.conf

nginx -g "daemon off;"