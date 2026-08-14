# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.24-3.1786536504@sha256:41cfee8b4373d0be1c6724ab80a9942e1e4d10bba62c5876455a8028a7a953a2

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar