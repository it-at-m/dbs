# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi10/nginx-126:10.1-1776906374@sha256:77d9ff8c889d48f1f068d0599f115b797154c5a14bb0fb57d45f809e4a244245

# Copy built web components and SBOM result
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/compression.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/files.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/health.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/sbom.conf "${NGINX_DEFAULT_CONF_PATH}"

# Start the web server
CMD ["nginx", "-g", "daemon off;"]