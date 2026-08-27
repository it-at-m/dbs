<template>
  <muc-callout type="info">
    <template #header>
      {{ t("preview.saveAsChecklist.header") }}
    </template>
    <template #content>
      <p>
        {{ t("preview.saveAsChecklist.body") }}
      </p>
    </template>
    <template #buttons>
      <a :href="getGermanLebenslagenLink()">
        <muc-button
          icon="arrow-right"
          icon-animated
        >
          {{ t("preview.saveAsChecklist.switchLanguageButton") }}
        </muc-button>
      </a>
    </template>
  </muc-callout>
</template>

<script setup lang="ts">
import { MucButton, MucCallout } from "@muenchen/muc-patternlab-vue";

defineProps<{
  t: (key: string) => string;
}>();

function getGermanLebenslagenLink() {
  const url = window.location.href;

  try {
    const urlObj = new URL(url);
    const pathSegments = urlObj.pathname
      .split("/")
      .filter((segment) => segment.length > 0);

    // Remove the first path segment
    pathSegments.shift();

    // Assemble the new path
    urlObj.pathname = "/" + pathSegments.join("/");

    return urlObj.toString();
  } catch {
    return url;
  }
}
</script>
