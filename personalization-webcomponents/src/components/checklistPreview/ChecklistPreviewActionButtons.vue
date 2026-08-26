<template>
  <div
      class="m-button-group hide-in-print"
      style="padding-top: 32px"
  >
    <muc-button
        v-if="currentLang == DEFAULT_LANGUAGE"
        icon="order-bool-ascending"
        @click="emit('save')"
    >
      Speichern
    </muc-button>
    <muc-button
        @click="copyUrl"
        variant="secondary"
        icon="copy-link"
        :icon-only="isMobile"
        :aria-label="t('preview.copyLink')"
        spin-icon-on-click
    >
      <template v-if="!isMobile">
        {{ t("preview.copyLink") }}
      </template>
    </muc-button>
    <p
        class="visually-hidden"
        role="status"
    >
      {{ linkStateMessage }}
    </p>
    <muc-button
        @click="print"
        variant="secondary"
        icon="printer"
        :icon-only="isMobile"
        :aria-label="t('preview.print')"
    >
      <template v-if="!isMobile">
        {{ t("preview.print") }}
      </template>
    </muc-button>
  </div>
</template>

<script setup lang="ts">
import {MucButton} from "@muenchen/muc-patternlab-vue";
import {DEFAULT_LANGUAGE, IS_WIDE_MOBILE_MEDIA_QUERY} from "@/util/Constants.ts";
import {useMediaQuery} from "@vueuse/core";
import {ref} from "vue";

const props = defineProps<{
  currentLang: string;
  t: (key: string) => string;
}>();

const emit = defineEmits(["save"]);

const linkStateMessage = ref("");

const isMobile = useMediaQuery(IS_WIDE_MOBILE_MEDIA_QUERY);

async function copyUrl() {
  const type = "text/plain";
  const clipboardItemData = {
    [type]: window.location.href,
  };
  const clipboardItem = new ClipboardItem(clipboardItemData);
  await navigator.clipboard.write([clipboardItem]).then(() => {
    linkStateMessage.value = props.t("preview.copyLinkSuccess");
    setTimeout(() => {
      linkStateMessage.value = "";
    }, 5000);
  });
}

async function print() {
  window.print();
}
</script>

<style scoped>
@media print {
  .hide-in-print {
    display: none;
  }
}
</style>