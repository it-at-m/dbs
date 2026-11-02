import * as fs from "node:fs";
import * as path from "node:path";

import { beforeAll, describe, expect, test } from "vitest";

const dirPath = "./src/util";
const EXPECTED_TRANSLATION_FILES = 11;

function getAllKeys(obj, prefix = "") {
  let keys = [];
  for (const key in obj) {
    const fullKey = prefix ? `${prefix}.${key}` : key;
    keys.push(fullKey);
    if (
      typeof obj[key] === "object" &&
      obj[key] !== null &&
      !Array.isArray(obj[key])
    ) {
      keys = keys.concat(getAllKeys(obj[key], fullKey));
    }
  }
  return keys;
}

describe("Übersetzungsdateien Konsistenz", () => {
  let files: string[];
  let referenceKeys: string[];

  beforeAll(() => {
    // Alle JSON-Dateien im Verzeichnis laden
    files = fs.readdirSync(dirPath).filter((file) => file.endsWith(".json"));

    if (files.length === 0) {
      throw new Error("No translations found.");
    }
  });

  test("All translations files exist and are readable.", () => {
    for (const file of files) {
      const filePath = path.join(dirPath, file);
      expect(fs.existsSync(filePath)).toBe(true);
      expect(() => fs.readFileSync(filePath, "utf-8")).not.toThrow();
      expect(files.length).toBe(EXPECTED_TRANSLATION_FILES);
    }
  });

  test("All files have the same keys in the same order.", () => {
    files.forEach((file, index) => {
      const filePath = path.join(dirPath, file);
      const jsonData = JSON.parse(fs.readFileSync(filePath, "utf-8"));
      const allKeys = getAllKeys(jsonData);

      if (index === 0) {
        // First file as reference
        referenceKeys = allKeys;
      } else {
        // Check if all keys are the same
        const missingKeys = referenceKeys.filter((k) => !allKeys.includes(k));
        const extraKeys = allKeys.filter((k) => !referenceKeys.includes(k));

        if (missingKeys.length > 0) {
          throw new Error(
            `Datei "${file}" fehlt Keys: ${missingKeys.join(", ")}`
          );
        }

        if (extraKeys.length > 0) {
          throw new Error(
            `Datei "${file}" hat zusätzliche Keys: ${extraKeys.join(", ")}`
          );
        }

        // Check correct order of all keys
        expect(allKeys).toEqual(referenceKeys);
      }
    });
  });
});
