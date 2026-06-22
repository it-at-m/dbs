AGENTS for This Repo

Purpose: Give future OpenCode agents only the high-signal facts needed to work effectively in this monorepo. Prefer executable sources over prose; commands below are verified from configs and scripts.

Monorepo Layout

- login-webcomponent: Vue 3 webcomponent (Vite). Node-only.
- personalization-webcomponents: Vue 3 webcomponents (Vite). Node-only.
- personalization-service: Spring Boot REST service. Java 21 + Maven, Postgres, Keycloak.
- ticketing-eventing: Spring Boot services
  - eventing-service (service)
  - handler-core (library)
  - mail-handler-service (service)
- ticketing-eai/api-client-internal: Java library generated from OpenAPI.
- stack: docker-compose services for local dev (Postgres, Keycloak, pgAdmin, refarch-gateway).

Local Stack and Ports (from stack/docker-compose.yml and app configs)

- Start infra: docker compose -f ./stack/docker-compose.yml up
- Ports:
  - Postgres: 5432 (db=postgres, user=admin, pw=admin)
  - pgAdmin: 5050
  - Keycloak: 8100 (base path /auth)
  - Gateway: 8083
  - personalization-service (local profile): 39146
- Gateway routes (important): forwards /clients/api/p13n-backend/** and /public/api/p13n-backend/** to host.docker.internal:39146. Keep backend on port 39146 for the proxies to work.

Backends (Maven/Java)

- Java version: 21 across all Maven modules. Set JAVA_HOME accordingly.
- personalization-service (preferred local run):
  - With security disabled to avoid Keycloak DNS issue: personalization-service/runLocalNoSecurity.(sh|bat)
  - With security: personalization-service/runLocal.(sh|bat) but you must set sso.url to http://localhost:8100 or provide host resolution for keycloak; default application-local.yml points to http://keycloak:8100 which is not resolvable from host.
- Build/verify (runs Spotless/PMD/SpotBugs/Jacoco): mvn clean verify (run inside each Maven module directory)
- Formatting fix if Spotless fails: mvn spotless:apply
- Tests use Testcontainers in several services; Docker must be available for test runs.

Frontends (Node/Vite)

- Node engines (strict):
  - login-webcomponent: node >=22.14
  - personalization-webcomponents: node >=22.14 <=24
- Install and run dev servers:
  - cd login-webcomponent && npm i && npm run dev (Vite port 5173)
  - cd personalization-webcomponents && npm i && npm run dev (Vite port 8082)
- Both Vite servers proxy /api, /actuator, /clients to http://localhost:8083 (gateway). Ensure the stack is up and personalization-service is running on 39146.
- Lint/typecheck: npm run lint (prettier check, eslint, vue-tsc noEmit)
- Tests: npm run test (vitest)

OpenAPI and Codegen

- personalization-service and ticketing-eventing/eventing-service include springdoc-openapi-maven-plugin configured for http://localhost:39146/v3/api-docs; service must be running on port 39146 when generating docs.
- ticketing-eai/api-client-internal generates a Java client via openapi-generator from src/main/resources/api-spec-internal-without-security.json during build. Edit the spec, not generated sources; regenerate with mvn generate-sources or mvn clean verify.

CI and Releases (from .github/workflows)

- CI builds each app path (Node or Maven) via Build all on PRs and pushes; images build on main for modules marked build-image: true.
- Releases are GitHub Actions workflows (manual):
  - Release Maven for Java modules (app-path choice). Handles version bump and image build where applicable.
  - Release NPM for Node modules (app-path choice). Handles version bump and optional npm publish and Docker image build.
- Do not manually bump versions; use the workflows which open PRs.

Non-Obvious Gotchas

- Keep personalization-service on port 39146 when developing; gateway routing depends on it.
- If running personalization-service with security enabled, default sso.url uses http://keycloak:8100 which resolves only inside Docker network; from host, use http://localhost:8100 or add a host entry for keycloak. The provided runLocalNoSecurity scripts avoid this.
- Testcontainers require a working Docker environment for Java tests.
- Node engine constraints are strict; using Node 18 or 20 will fail. Use Node 22.14+ (and <=24 for personalization-webcomponents).

Quick Start (local integration loop)

1. docker compose -f ./stack/docker-compose.yml up
2. personalization-service/runLocalNoSecurity.(sh|bat)
3. In separate terminals: start Vite dev servers in login-webcomponent (5173) and personalization-webcomponents (8082)
4. Exercise APIs via gateway at http://localhost:8083; Vite proxies are preconfigured.

References

- README.md (root) for brief component list and getting started.
- stack/docker-compose.yml is the source of truth for local infra and ports.
