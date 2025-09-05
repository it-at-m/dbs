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
}

function changeCamelToSnakeCase(camelCased) {
  const snakeCased = camelCased.replace(
    /[A-Z]/g,
    (letter) => `-${letter.toLowerCase()}`
  );
  return snakeCased.startsWith("-") ? snakeCased.substring(1) : snakeCased;
}
