<template>
  <muc-button
    :class="{
      'light-button': isDropdownOpen,
      'mobile-menu-trigger': isMobile,
    }"
    :icon="buttonIcon"
    :icon-shown-left="!isMobile"
    :variant="!isMobile ? 'primary' : 'icon'"
    @click="handleClick"
  >
    <span
      v-if="isMobile"
      class="visually-hidden"
    >
      {{ isLoggedIn ? "Mein Bereich" : "Anmeldung" }}
    </span>
    <span v-if="!isMobile">
      {{ buttonLabel }}
    </span>
  </muc-button>
</template>

<script setup lang="ts">
import { MucButton } from "@muenchen/muc-patternlab-vue";
import { computed } from "vue";

import { useIdpHint } from "@/composables/idphint";

const { isDropdownOpen, isMobile, isLoggedIn } = defineProps<{
  isDropdownOpen: boolean;
  isMobile: boolean;
  isLoggedIn: boolean;
}>();

const emit = defineEmits<(e: "login" | "toggleDropdown") => void>();

const { idpConfig } = useIdpHint();

const handleClick = () => {
  if (!isLoggedIn && !isMobile) {
    emit("login");
  } else {
    emit("toggleDropdown");
  }
};

const buttonLabel = computed(() =>
  isLoggedIn
    ? "Mein Bereich"
    : idpConfig.value
    ? `Mit ${idpConfig.value.displayName} anmelden`
    : "Anmelden"
);

const buttonIcon = computed(() =>
  isDropdownOpen ? "close" : isLoggedIn ? "user-fill" : "user"
);
</script>

<style scoped>
/* Add light button color*/
.light-button,
.light-button:hover,
.light-button:focus {
  background-color: var(--color-neutrals-blue-xlight);
  color: var(--color-brand-main-blue);
  border-color: transparent;
}
</style>
