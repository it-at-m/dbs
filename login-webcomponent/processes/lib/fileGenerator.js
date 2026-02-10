import { execSync } from "node:child_process";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export function generateLoaderJs(filename, subdirectory, suffix) {
  // read contents of loader.js.template as string
  const loaderJsTemplate = fs.readFileSync(`${__dirname}/loader.js.template`, {
    encoding: "utf-8",
  });
  // replace the correct placeholder with the actual filename
  const loaderJsReplaced = loaderJsTemplate.replaceAll(
    "{{path}}",
    `../${subdirectory}/${filename}`
  );

  const dirName = changeCamelToSnakeCase(suffix);
  // write script to the dist folder as loader.js.template
  fs.mkdirSync(`./dist/${dirName}`, { recursive: true });
  fs.writeFileSync(
    path.resolve(`./dist/${dirName}/loader.js`),
    loaderJsReplaced,
    {
      encoding: "utf-8",
    }
  );

  // Also write a loader.js directly into ./dist that points to the subdirectory
  const loaderJsRootReplaced = loaderJsTemplate.replaceAll(
    "{{path}}",
    `${subdirectory}/${filename}`
  );
  fs.writeFileSync(path.resolve(`./dist/loader.js`), loaderJsRootReplaced, {
    encoding: "utf-8",
  });
}

function changeCamelToSnakeCase(camelCased) {
  const snakeCased = camelCased.replace(
    /[A-Z]/g,
    (letter) => `-${letter.toLowerCase()}`
  );
  return snakeCased.startsWith("-") ? snakeCased.substring(1) : snakeCased;
}

export function generateInfoJson() {
  const commitHash = execSync("git rev-parse --short HEAD")
    .toString()
    .replace(/\r?\n|\r/g, "");
  const commitTimestamp = execSync(
    `git show --no-patch --format=%ci ${commitHash}`
  )
    .toString()
    .replace(/\r?\n|\r/g, "");

  const infoJson = {
    commitHash,
    commitTimestamp,
  };

  fs.writeFileSync(path.resolve("./dist/info.json"), JSON.stringify(infoJson), {
    encoding: "utf-8",
  });
}
