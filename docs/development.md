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
