# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi10/nginx-126:10.1-1779165744@sha256:ae4674940af8ccff65eff09e49b88eac9fb02c551016b5b204ac61b11ec3f946

# Copy built web components and SBOM result
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/compression.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/files.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/health.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/sbom.conf "${NGINX_DEFAULT_CONF_PATH}"

# Copy custom nginx config templates
COPY docker/nginx/nginxhttp.conf.template "/opt/app-root/etc/nginx.d"

# Start the web server
COPY docker/entrypoint.sh ./entrypoint.sh
USER 0
RUN chown -R 1001:0 /opt/app-root/src/entrypoint.sh
RUN chown -R 1001:0 /usr/share/nginx/modules
RUN chmod +x /opt/app-root/src/entrypoint.sh
USER 1001

ENTRYPOINT ["/opt/app-root/src/entrypoint.sh"]