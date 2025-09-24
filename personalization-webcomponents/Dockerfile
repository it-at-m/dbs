# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi10/nginx-126:10.0-1757952698@sha256:8e282961aa38ee1070b69209af21e4905c2ca27719502e7eaa55925c016ecb76

# Copy built web components and SBOM result
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/compression.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/files.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/health.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/sbom.conf "${NGINX_DEFAULT_CONF_PATH}"

# Start the web server
CMD ["nginx", "-g", "daemon off;"]