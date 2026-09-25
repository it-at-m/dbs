<script setup lang="ts">
import { MucButton, MucBanner } from "@muenchen/muc-patternlab-vue";
import { CSpinner } from "@coreui/vue/dist/esm/components/spinner";

defineProps({
    loading: Boolean,
    resetSuccess: Boolean,
    resetFailure: Boolean,
});

defineEmits(["clickMail"]);
</script>

<template>
    <div>
        <muc-banner
            v-if="resetSuccess"
            type="success"
            variant="content"
        >
            <p>
                Wir haben Ihnen eine E-Mail mit einem Link gesendet. Klicken Sie
                in den nächsten 30 Minuten auf diesen Link, um Ihr Passwort
                erneut einzusehen.
            </p>
        </muc-banner>

        <muc-banner
            v-else-if="resetFailure"
            type="warning"
            variant="content"
        >
            <p>
                Das zusenden Ihre Passworts ist fehlgeschlagen. Bitte versuchen
                Sie es zu einem späteren Zeitpunkt erneut.
            </p>
        </muc-banner>

        <div v-else>
            <p>
                Der Link zu Ihrem Passwort ist leider abgelaufen. Klicken Sie
                auf „E-Mail senden“, um einen neuen Link zu Ihrem Passwort zu
                erhalten. Dieser Link ist dann wieder <b>30 Minuten</b> gültig.
            </p>

            <muc-button
                style="margin-top: 24px"
                :icon="loading ? '' : 'mail'"
                @click="$emit('clickMail')"
            >
                E-Mail senden
                <c-spinner
                    v-if="loading"
                    class="spinner-icon"
                />
            </muc-button>
        </div>
    </div>
</template>

<style scoped>
.spinner-icon {
    margin-left: 0.75rem;
    --cui-spinner-width: 1.5rem;
    --cui-spinner-height: 1.5rem;
    vertical-align: middle;
}
</style>
