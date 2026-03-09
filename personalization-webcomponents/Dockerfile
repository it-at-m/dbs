# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi10/nginx-126:10.1-1772526262@sha256:53bd27444054cf2fda020c1886d2e5322a47f26412ffd6537a1d8894d05fb817

# Copy built web components and SBOM result
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/compression.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/files.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/health.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/sbom.conf "${NGINX_DEFAULT_CONF_PATH}"

# Start the web server
CMD ["nginx", "-g", "daemon off;"]