# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi10/nginx-126:10.2-1790750813@sha256:c7ba454e04aab177e3d2e283e679412dc90477d77848715ebabd3db9af4b45af

# Copy built web components and SBOM result
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/compression.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/files.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/health.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/sbom.conf "${NGINX_DEFAULT_CONF_PATH}"

# Start the web server
CMD ["nginx", "-g", "daemon off;"]