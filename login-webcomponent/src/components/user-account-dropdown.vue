<template class="m-content">
  <!-- Mobile OffCanvas -->
  <c-offcanvas
    v-if="isMobile"
    style="background: transparent"
    placement="end"
    :visible="open"
    @hide="emit('close')"
  >
    <c-offcanvas-header
      :style="{
        height: `${position?.top.value + position?.height.value}px`,
        background: 'transparent',
        padding: 0,
      }"
    >
      <div style="display: flex" />
      <div style="background: white; height: 100%">
        <muc-button
          :style="{
            height: `${position?.height.value}px`,
            width: `${position?.width.value}px`,
            position: 'fixed',
            top: `${position?.top.value}px`,
            left: `${position?.left.value}px`,
          }"
          class="light-button"
          :variant="isMobile ? 'icon' : 'secondary'"
          icon="close"
          icon-shown-left
          @click="emit('close')"
        >
          {{ isMobile ? "" : "Mein Bereich" }}
        </muc-button>
      </div>
    </c-offcanvas-header>
    <c-offcanvas-body
      style="background: var(--color-neutrals-blue-xlight); padding: 0"
    >
      <drop-down-content
        :is-logged-in="isLoggedIn"
        :user-properties="userProperties"
        :links="links"
        @click-sign-in="signIn"
        @click-sign-out="signOut"
      />
    </c-offcanvas-body>
  </c-offcanvas>
  <!-- Desktop dropdown -->
  <div v-else>
    <!-- @vue-ignore ts doesn't like vue property binding -->
    <div
      :class="{
        'muc-dropdown': true,
        show: open,
      }"
      :style="{
        left:
          displayWidth <= 1840
            ? `${
                position?.left.value -
                (DROPDOWN_WIDTH_PXL - position?.width.value)
              }px`
            : null,
        right: displayWidth > 1840 ? '4rem' : null,
        width: `${DROPDOWN_WIDTH_PXL}px`,
      }"
    >
      <drop-down-content
        :is-logged-in="isLoggedIn"
        :user-properties="userProperties"
        :links="links"
        @click-sign-in="signIn"
        @click-sign-out="signOut"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type LinkProperties from "@/types/LinkProperties.ts";
import type UserProperties from "@/types/UserProperties.ts";
import type { Ref } from "vue";

import { COffcanvas, COffcanvasBody, COffcanvasHeader } from "@coreui/vue";
import { MucButton } from "@muenchen/muc-patternlab-vue";
import { useWindowSize } from "@vueuse/core";

import DropDownContent from "@/components/DropDownContent.vue";

const { open, isLoggedIn, isMobile, userProperties, links, position } =
  defineProps<{
    open: boolean;
    isLoggedIn: boolean;
    isMobile: boolean;
    userProperties: UserProperties;
    links: LinkProperties;
    position: {
      height: Ref<number>;
      bottom: Ref<number>;
      left: Ref<number>;
      right: Ref<number>;
      top: Ref<number>;
      width: Ref<number>;
      x: Ref<number>;
      y: Ref<number>;
    };
  }>();

const emit = defineEmits<(e: "login" | "logout" | "close") => void>();

const { width: displayWidth } = useWindowSize();

const DROPDOWN_WIDTH_PXL = 480;

function signIn(): void {
  emit("login");
}

function signOut(): void {
  emit("logout");
}
</script>

<style scoped>
.muc-dropdown {
  background: var(--color-neutrals-blue-xlight);
  display: none;
  position: absolute;
  height: auto;
  box-shadow: 0 8px 16px 0 rgba(0, 0, 0, 0.2);
  z-index: 150;
}

.show {
  display: block;
}

/* Add light button color*/
.light-button,
.light-button:hover,
.light-button:focus {
  background-color: var(--color-neutrals-blue-xlight);
  color: var(--color-brand-main-blue);
  border-color: transparent;
}
</style>
