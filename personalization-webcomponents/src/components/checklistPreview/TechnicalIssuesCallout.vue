<template>
  <muc-callout type="error">
    <template #header>
      {{ t("preview.errorTechnicalIssueHeader") }}
    </template>
    <template #content>
      <p>
        {{ t("preview.errorTechnicalIssueContent") }}
      </p>
    </template>
  </muc-callout>
</template>

<script setup lang="ts">
import {MucCallout} from "@muenchen/muc-patternlab-vue";

defineProps<{
  t: (key: string) => string;
}>();
</script>
