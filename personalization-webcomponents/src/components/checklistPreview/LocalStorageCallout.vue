<template>
  <div>
    <h2 style="padding-bottom: 16px">
      {{ t("preview.errorEmptyLocalStorageHeader") }}
    </h2>
    <p style="padding-bottom: 32px">
      {{ t("preview.errorEmptyLocalStorageContent") }}
    </p>
    <a :href="newChecklistUrl">
      <muc-button
        icon="arrow-right"
        iconAnimated
      >
        {{ t("preview.restartQuestionnaire") }}
      </muc-button>
    </a>
  </div>
</template>

<script setup lang="ts">
import { MucButton } from "@muenchen/muc-patternlab-vue";

defineProps<{
  newChecklistUrl: string;
  t: (key: string) => string;
}>();
</script>
