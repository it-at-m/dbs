# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.24-3@sha256:f4519929e630051b1a207013ba81700898cf2c4605dfc3c85effc7c14b768e59

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar