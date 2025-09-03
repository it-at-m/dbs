import { fileURLToPath, URL } from "node:url";

import vue from "@vitejs/plugin-vue";
import { defineConfig } from "vite";
import vueDevTools from "vite-plugin-vue-devtools";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const isDevelopment = mode === "development";
  return {
    plugins: [
      vue({
        features: {
          customElement: true,
          optionsAPI: isDevelopment,
        },
      }),
      vueDevTools(),
    ],
    server: {
      port: 8082,
      proxy: {
        "/api": "http://localhost:8083",
        "/actuator": "http://localhost:8083",
        "/clients": "http://localhost:8083",
      },
    },
    resolve: {
      dedupe: ["vue"],
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url)),
      },
    },
    build: {
      manifest: true, // required for post build logic in 'processes' folder
      minify: true,
      rollupOptions: {
        input: {
          "checklist-detail": "./src/checklist-detail-webcomponent.ts",
          "checklist-overview": "./src/checklist-overview-webcomponent.ts",
          "checklist-preview": "./src/checklist-preview-webcomponent.ts",
          "my-checklists": "./src/my-checklists-webcomponent.ts",
        },
        output: {
          entryFileNames: "entry-[name]-[hash].js",
          dir: "dist/src",
        },
      },
    },
  };
});
