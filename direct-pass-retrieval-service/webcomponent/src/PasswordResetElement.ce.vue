<template>
    <main>
        <div>
            <div v-html="mucIconsSprite" />
            <div v-html="customIconsSprit" />

            <remaining-time-banner
                :loading="loading"
                :valid-until="validUntil!"
                :exceeded="errorExceeded"
                @exceeded="errorExceeded = true"
            />

            <div class="pw-container">
                <div class="pw-modal">
                    <h2 style="padding-bottom: 16px">
                        Passwort für Ihr Anliegen
                    </h2>

                    <div
                        v-if="
                            errorExceeded &&
                            !loading &&
                            !errorNoQueryParam &&
                            !errorWrongData &&
                            !errorLoadingData
                        "
                    >
                        <request-new-link-content
                            :loading="loadingReset"
                            :reset-success="resetSuccess"
                            :reset-failure="resetFailure"
                            @click-mail="onClickMail"
                        />
                    </div>

                    <div v-else>
                        <copy-password-content
                            :loading="loading"
                            :direct-pass="directPass"
                            :error-no-query-param="errorNoQueryParam"
                            :error-wrong-data="errorWrongData"
                            :error-loading-data="errorLoadingData"
                        />
                    </div>
                </div>
            </div>
        </div>
    </main>
</template>

<script setup lang="ts">
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import customIconsSprit from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import { onMounted, ref } from "vue";
import { getPassword, resetPassword } from "@/api/PRServiceAPI";
import { PasswordDTO } from "@/api/PasswordDTO";
import RemainingTimeBanner from "@/components/RemainingTimeBanner.vue";
import RequestNewLinkContent from "@/components/RequestNewLinkContent.vue";
import CopyPasswordContent from "@/components/CopyPasswordContent.vue";

const loading = ref(true);

const directPass = ref("");
const validUntil = ref<Date | null>(null);

const errorNoQueryParam = ref(false);
const errorLoadingData = ref(false);
const errorWrongData = ref(false);
const errorExceeded = ref(false);

const QUERY_PARAM_RESETKEY = "q";
let resetKey: string | undefined;

const loadingReset = ref(false);
const resetSuccess = ref(false);
const resetFailure = ref(false);

onMounted(async () => {
    const queryParams = new URLSearchParams(window.location.search);
    if (queryParams.has(QUERY_PARAM_RESETKEY)) {
        errorNoQueryParam.value = false;
        resetKey = queryParams.get(QUERY_PARAM_RESETKEY) ?? undefined;
        if (resetKey) {
            await getPass(resetKey);
        }
    } else {
        loading.value = false;
        errorNoQueryParam.value = true;
    }
});

async function getPass(key: string): Promise<void> {
    loading.value = true;
    try {
        const response = await getPassword(key);
        if (!response.ok) {
            directPass.value = "";
            validUntil.value = null;
            errorWrongData.value = true;
            return;
        }

        const password = (await response.json()) as PasswordDTO;
        directPass.value = password.password;
        validUntil.value = new Date(password.validUntil);
        errorWrongData.value = false;
        errorLoadingData.value = false;
        errorExceeded.value = false;
    } catch {
        directPass.value = "";
        validUntil.value = null;
        errorLoadingData.value = true;
    } finally {
        loading.value = false;
    }
}

async function onClickMail(): Promise<void> {
    if (!resetKey) {
        resetFailure.value = true;
        return;
    }

    loadingReset.value = true;
    try {
        const response = await resetPassword(resetKey);
        resetSuccess.value = response.ok;
        resetFailure.value = !response.ok;
    } catch {
        resetSuccess.value = false;
        resetFailure.value = true;
    } finally {
        loadingReset.value = false;
    }
}
</script>

<style>
@import "https://assets.muenchen.de/mde/1.1.23/css/style.css";
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
@import "@/assets/coreui-spinner.css";

:host {
    font-family:
        Open Sans,
        sans-serif;
}

main {
    padding-bottom: 32px;
}

.pw-container {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.pw-modal {
    max-width: 592px;
    margin-top: 66px;
    padding: 24px;
    flex-direction: column;
    justify-content: flex-start;
    align-items: flex-start;
    display: inline-flex;
}

@media screen and (min-width: 560px) {
    .pw-modal {
        padding: 32px;
        box-shadow: 0 0 15px rgba(58, 83, 104, 0.25);
        border: 1px white solid;
    }
}
</style>
