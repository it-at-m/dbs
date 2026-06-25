# Local Development

🚧 WIP

1. Execute the following commands:

```bash
cd stack
podman compose up
# or
# docker compose up
cd ../personalization-service
./runLocal.sh
cd ../personalization-webcomponents
npm i
npm run dev
```

2. Follow <https://github.com/it-at-m/dbs/blob/main/login-webcomponent/README.md#dev-setup>
3. Open browser <http://localhost:8082/>
4. Login with `writer:writer` or `reader:reader`

# P13N

## Generating OpenAPI Docs and API-Client for Webcomponents

We use Spring Docs to generate the OpenAPI docs for our RESTful personalization-service. The Docs are not generated automatically.

When we make changes to the api we need to regenerate the docs:

1. Start personlaization-service locally
2. Open http://localhost:39146/v3/api-docs
3. Download and save the file under `personalization-service/api-docs.json`

Next up the frontends client needs to be updated:

1. Install the generator once globally `npm install @openapitools/openapi-generator-cli -g typescript-fetch`

> [!IMPORTANT]
> When you are behind a proxy the api-gen:generate command needs to have your proxy envs set correctly. E.g.:
> ```shell
> export HTTPS_PROXY="http://<proxy-url>:<proxy-port>"
> ```

```shell
cd personalization-webcomponents
npm i
npm run api-gen:generate
```

