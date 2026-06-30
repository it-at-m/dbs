<template>
  <slot />
</template>

<script setup lang="ts">
import { provide } from "vue";
import { createI18n, I18nInjectionKey } from "vue-i18n";

import { DEFAULT_LANGUAGE } from "@/util/Constants.ts";
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
  locale: DEFAULT_LANGUAGE,
  messages: {
    de: deDE,
    en: enGB,
    fr: frFR,
    tr: trTR,
    es: esES,
    uk: ukUA,
    ru: ruRU,
    ar: ar,
    pt: ptPT,
    vi: viVN,
    "zh-hans": zhCN,
  },
});

/**
 * provide i18n instance with `I18nInjectionKey` for other web components
 */
provide(I18nInjectionKey, i18n);
</script>
