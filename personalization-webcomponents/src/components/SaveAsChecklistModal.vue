<template>
  <muc-modal
    :open="open"
    @close="$emit('close')"
    @cancel="$emit('close')"
  >
    <template #title>Speichern als Checkliste</template>
    <template #body>
      <div class="m-content">
        <div class="m-checkboxes">
          <div class="m-checkboxes__item">
            <input
              id="checkbox-privacy-policy"
              class="m-checkboxes__input"
              name="checkbox-privacy-policy"
              type="checkbox"
              aria-required="true"
              v-model="dseAccepted"
            />
            <label
              class="m-label m-checkboxes__label"
              for="checkbox-privacy-policy"
            >
              Ich stimme der Speicherung der Checkliste
              <strong>„{{ title }}”</strong>
              gemäß der
              <a
                href="https://stadt.muenchen.de/dam/DSGVO/Datenschutzhinweise-Checklisten.pdf"
                target="_blank"
                >Datenschutzerklärung</a
              >
              und der
              <a
                href="https://stadt.muenchen.de/infos/elektronische-kommunikation.html"
                target="_blank"
                >Hinweise zur elektronischen Kommunikation</a
              >
              zu. Meine Zustimmung kann ich jederzeit widerrufen.
            </label>
          </div>
        </div>
      </div>
      <muc-banner
        v-if="loadingError"
        type="emergency"
        variant="content"
      >
        Es ist ein Fehler beim Speichern der Checkliste aufgetreten. Bitte
        versuchen Sie es zu einem späteren Zeitpunkt noch einmal.
      </muc-banner>
    </template>
    <template #buttons>
      <muc-button
        :disabled="!dseAccepted || loading"
        :icon="loading ? '' : 'order-bool-ascending'"
        @click="$emit('save')"
      >
        Checkliste speichern
        <muc-spinner
          v-if="loading"
          style="color: white"
          size="24px"
        >
        </muc-spinner>
      </muc-button>
    </template>
  </muc-modal>
</template>

<script setup lang="ts">
import {
  MucBanner,
  MucButton,
  MucModal,
  MucSpinner,
} from "@muenchen/muc-patternlab-vue";
import { ref, watch } from "vue";

// State
const dseAccepted = ref(false);

const props = defineProps<{
  open: boolean;
  title: string;
  loading: boolean;
  loadingError: string;
}>();

defineEmits(["close", "save"]);

/**
 * Reset dseAccepted when dialog was closed
 */
watch(
  () => props.open,
  (newOpen) => {
    if (!newOpen) {
      dseAccepted.value = false;
    }
  }
);
</script>
