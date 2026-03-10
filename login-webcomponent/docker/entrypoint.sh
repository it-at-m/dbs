#!/bin/sh
echo "Starting NGINX..."

# Fallback if no env is set
envsubst '${ALLOW_ORIGIN}' < /opt/app-root/etc/nginx.d/nginxhttp.conf.template > /opt/app-root/etc/nginx.d/nginxhttp.conf

nginx -g "daemon off;"