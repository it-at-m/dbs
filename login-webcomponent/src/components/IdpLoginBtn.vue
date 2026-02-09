<script setup lang="ts">
import type { IDP } from "@/composables/idphint";

import bayernIDLogoURL from "@/assets/svg/logo/bayernid.png";
import bundidLogoUrl from "@/assets/svg/logo/bundid.png";
import elsterLogoURL from "@/assets/svg/logo/elster.svg";
import IconLogin from "@/components/icons/icon-login.vue";
import {
  IDP_BAYERN_ID,
  IDP_BUND_ID,
  IDP_ELSTER,
  idphintMapping,
} from "@/composables/idphint";

defineEmits(["login"]);

defineProps<{
  idp: IDP;
}>();
</script>

<template>
  <div style="display: flex; flex-direction: row; align-items: center">
    <div>
      <button
        class="m-button m-button--primary"
        @click="$emit('login')"
      >
        <img
          v-if="idp === IDP_BAYERN_ID"
          :src="bayernIDLogoURL"
        />
        <img
          v-if="idp === IDP_ELSTER"
          :src="elsterLogoURL"
        />
        <img
          v-if="idp === IDP_BUND_ID"
          :src="bundidLogoUrl"
        />
        <span>Mit {{ idphintMapping?.get(idp)?.displayName }} anmelden</span>
        <icon-login
          class="m-button__icon"
          style="margin-left: 8px"
        />
      </button>
    </div>
  </div>
</template>

<style scoped>
img {
  margin-right: 16px;
  width: 24px;
  height: 24px;
}
</style>
