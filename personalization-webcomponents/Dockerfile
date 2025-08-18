# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi10/nginx-126:10.0-1755015258@sha256:f536dd2c6fd92a3b6c5ad356d3493132416398b5fd1582a0dfbcef6fa4626889

# Copy built web components and SBOM result
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/compression.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/files.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/health.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/sbom.conf "${NGINX_DEFAULT_CONF_PATH}"

# Start the web server
CMD ["nginx", "-g", "daemon off;"]