<template>
  <link
    href="https://assets.muenchen.de/mde/1.1.15/css/style.css"
    rel="stylesheet"
    @load="stylesLoaded = true"
  />
  <div v-if="stylesLoaded">
    <dbs-login
      :kc-url="kcUrl"
      :kc-realm="kcRealm"
      :kc-client-id="kcClientId"
      :cookie-domain="cookieDomain"
      :links="getLinkProperties"
    />
    <svg
      style="display: none"
      v-html="customIconsSprite"
    />
    <svg
      style="display: none"
      v-html="mucIconsSprite"
    />
  </div>
</template>

<script lang="ts" setup>
import type LinkProperties from "@/types/LinkProperties.ts";
import type { ComputedRef } from "vue";

import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { computed, ref } from "vue";

import DbsLogin from "@/components/dbs-login.vue";

const props = defineProps({
  kcUrl: {
    type: String,
    required: true,
  },
  kcRealm: {
    type: String,
    required: true,
  },
  kcClientId: {
    type: String,
    required: true,
  },
  cookieDomain: {
    type: String || undefined,
    default: undefined,
  },
  overviewLink: {
    type: String || undefined,
    default: undefined,
  },
  ticketLink: {
    type: String || undefined,
    default: undefined,
  },
  appointmentLink: {
    type: String || undefined,
    default: undefined,
  },
  checklistLink: {
    type: String || undefined,
    default: undefined,
  },
});

//@ts-expect-error cant return a computed ref - only an object
const getLinkProperties: ComputedRef<LinkProperties> = computed(() => {
  return {
    overviewLink: props.overviewLink,
    ticketLink: props.ticketLink,
    appointmentLink: props.appointmentLink,
    checklistLink: props.checklistLink,
  };
});

const stylesLoaded = ref(false);
</script>

<style>
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
@import "@coreui/coreui/dist/css/coreui.min.css";

:host {
  font-family:
    Open Sans,
    sans-serif;
}

/**
 * Fix for MDE-Patternlab Z-Index Bug on Desktop
 */
.offcanvas {
  z-index: 155 !important;
  transition: opacity 0.15s linear !important;
  opacity: 0;
}

.offcanvas.show {
  opacity: 1;
}
.offcanvas-backdrop {
  z-index: 150 !important;
}
</style>
