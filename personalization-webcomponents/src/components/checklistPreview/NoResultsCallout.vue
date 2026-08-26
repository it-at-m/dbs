<template>
  <muc-callout
      type="warning"
  >
    <template #header>
      {{ t("preview.errorNoServicesFoundHeader") }}
    </template>
    <template #content>
      <p>
        {{ t("preview.errorNoServicesFoundContentBeforeLink") }}
        <muc-link
            class="mde-bold"
            :href="
                    currentLang == DEFAULT_LANGUAGE
                      ? 'https://stadt.muenchen.de/rathaus/kontakt.html'
                      : 'https://stadt.muenchen.de/infos/welcome-center.html?lang=de'
                  "
            :label="t('preview.errorNoServicesFoundLink')"
            noUnderline
        />
        {{ t("preview.errorNoServicesFoundContentAfterLink") }}
      </p>
    </template>
  </muc-callout>
</template>

<script setup lang="ts">
import {MucCallout, MucLink} from "@muenchen/muc-patternlab-vue";
import {DEFAULT_LANGUAGE} from "@/util/Constants.ts";

defineProps<{
  currentLang: string;
  t: (key: string) => string;
}>();
</script>
