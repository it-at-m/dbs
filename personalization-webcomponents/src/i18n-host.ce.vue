<template>
  <slot />
</template>

<script setup lang="ts">
import { provide } from "vue";
import { createI18n, I18nInjectionKey } from "vue-i18n";

import ar from "./util/ar.json";
import deDE from "./util/de-DE.json";
import enGB from "./util/en-GB.json";
import esES from "./util/es-ES.json";
import frFR from "./util/fr-FR.json";
import ptPT from "./util/pt-PT.json";
import ruRU from "./util/ru-RU.json";
import trTR from "./util/tr-TR.json";
import ukUA from "./util/uk-UA.json";
import viVN from "./util/vi-VN.json";
import zhCN from "./util/zh-CN.json";

const i18n = createI18n({
  locale: "de-DE",
  messages: {
    "de-DE": deDE,
    "en-GB": enGB,
    "fr-FR": frFR,
    "tr-TR": trTR,
    "es-ES": esES,
    "uk-UA": ukUA,
    "ru-RU": ruRU,
    ar: ar,
    "pt-PT": ptPT,
    "vi-VN": viVN,
    "zh-CN": zhCN,
  },
});

/**
 * provide i18n instance with `I18nInjectionKey` for other web components
 */
provide(I18nInjectionKey, i18n);
</script>
