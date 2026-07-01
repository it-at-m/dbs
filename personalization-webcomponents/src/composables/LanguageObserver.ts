import { useMutationObserver } from "@vueuse/core";
import { ref } from "vue";

export function useLanguageObserver() {
  const currentLang = ref<string>(document.documentElement.lang);

  useMutationObserver(
    document.documentElement,
    (mutations) => {
      const langMutation = mutations.find((m) => m.attributeName === "lang");
      if (langMutation) {
        currentLang.value = document.documentElement.lang;
      }
    },
    { attributes: true, attributeFilter: ["lang"] }
  );
  return { currentLang };
}
