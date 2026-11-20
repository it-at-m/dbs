<script setup lang="ts">
import { onMounted, onUnmounted, ref } from "vue";
import { MucBanner } from "@muenchen/muc-patternlab-vue";

const emit = defineEmits(["exceeded"]);

const props = defineProps({
    loading: { type: Boolean, default: false },
    validUntil: {
        type: Date,
        default: new Date(0),
    },
    exceeded: Boolean,
});

const remainingTime = ref("");
const remainingTimeGood = ref(false);

let remainingTimeCalcInterval: NodeJS.Timeout;

onMounted(() => {
    calcRemainingTime();
    remainingTimeCalcInterval = setInterval(calcRemainingTime, 1000);
});

onUnmounted(() => {
    clearInterval(remainingTimeCalcInterval);
});

/**
 * Calculates the remaining valid time of the pw in the format "25 Minuten, 12 Sekunden"
 */
function calcRemainingTime() {
    if (!props.validUntil) {
        setExceeded();
        return;
    }

    const validUntil = props.validUntil;
    const now = new Date();
    const diffInSeconds = (validUntil.getTime() - now.getTime()) / 1000;

    if (diffInSeconds <= 0) {
        setExceeded();
        return;
    }

    const minutes = Math.floor(diffInSeconds / 60);
    const seconds = Math.floor(diffInSeconds % 60);

    let remainingTimeString = "";
    if (minutes > 0) {
        remainingTimeString = `${minutes} Minute${minutes != 1 ? "n" : ""}, `;
        remainingTimeGood.value = true;
    } else {
        remainingTimeGood.value = false;
    }
    remainingTimeString += `${seconds} Sekunde${seconds != 1 ? "n" : ""}`;
    remainingTime.value = remainingTimeString;
}

function setExceeded() {
    remainingTime.value = "keine";
    remainingTimeGood.value = false;
    emit("exceeded");
}
</script>

<template>
    <muc-banner
        class="banner"
        :type="loading || remainingTimeGood ? 'info' : 'warning'"
        variant="header"
    >
        <div v-if="!exceeded">
            <span v-if="!loading"
                >Verbleibende Zeit: <b>{{ remainingTime }}</b></span
            >
            <span v-else>Laden...</span>
        </div>
        <div v-else>Dieser Link ist nicht mehr gültig.</div>
    </muc-banner>
</template>

<style scoped>
@media print {
    .banner {
        visibility: hidden;
    }
}
</style>
