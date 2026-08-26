<template>
  <muc-modal
      :open="open"
      @close="emit('close')"
      @cancel="emit('close')"
  >
    <template #title>Speichern als Checkliste</template>
    <template #body>
      Melden Sie sich beim Bürgerservice an, um Ihre Leistungen als Checkliste
      in Ihrem Bereich zu speichern.
    </template>
    <template #buttons>
      <muc-button
          icon="sign-in"
          @click="emit('sign-in')"
      >
        Anmelden
      </muc-button>
    </template>
  </muc-modal>
</template>

<script setup lang="ts">
import {MucButton, MucModal} from "@muenchen/muc-patternlab-vue";

const { open = false } = defineProps<{
  open?: boolean;
}>();

const emit = defineEmits(["close", "cancel", "sign-in"]);
</script>
