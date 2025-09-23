<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />

    <service-info-modal
      :open="serviceInfoModalOpen"
      :service="selectedService!"
      @close="serviceInfoModalOpen = false"
      @cancel="serviceInfoModalOpen = false"
    />

    <muc-modal
      :open="requestLoginModalOpen"
      @close="requestLoginModalOpen = false"
      @cancel="requestLoginModalOpen = false"
    >
      <template #title>Bürgerservice-Anmeldung</template>
      <template #body
        >Melden Sie sich an, um die für Sie ermittelten Leistungen als
        Checkliste in Ihrem Bereich zu speichern.
      </template>
      <template #buttons>
        <muc-button
          icon="sing-in"
          @click="_requestLogin"
        >
          Anmelden
        </muc-button>
      </template>
    </muc-modal>

    <muc-modal
      :open="saveChecklistModalOpen"
      @close="saveChecklistModalOpen = false"
      @cancel="saveChecklistModalOpen = false"
    >
      <template #title>Speichern als Checkliste</template>
      <template #body>
        <p>
          Ich stimme der Speicherung der Checkliste
          <b>„{{ lebenslageTitle }}”</b> in meinem Bereich gemäß der
          <a href="https://stadt.muenchen.de/infos/datenschutz.html"
            >Datenschutzerklärung</a
          >
          zu.
        </p>
        <muc-checkbox
          id="dseAcceptCheckbox"
          v-model:="dseAccepted"
          label="Ich stimme zu."
        />

        <muc-banner
          v-if="loadingError"
          type="emergency"
        >
          Es ist ein Fehler beim speichern der Checkliste aufgetreten. Bitte
          versuchen Sie es zu einem späteren Zeitpunkt nochmal.
        </muc-banner>
      </template>
      <template #buttons>
        <muc-button
          :disabled="!dseAccepted"
          :icon="loading ? '' : 'order-bool-ascending'"
          @click="_saveChecklistAcceptedDSE"
        >
          Checkliste speichern
          <muc-percentage-spinner
            v-if="loading"
            style="color: white"
            size="24px"
          />
        </muc-button>
      </template>
    </muc-modal>

    <muc-intro
      :title="lebenslageTitle"
      :divider="false"
      style="margin-bottom: 56px"
    >
      <div v-if="!localStorageError">
        <p>
          Vielen Dank für Ihre Angaben! Basierend auf Ihren Antworten könnten
          folgende Leistungen für Sie passen.
        </p>
        <div style="padding-top: 32px">
          <muc-button
            icon="order-bool-ascending"
            style="margin-right: 16px"
            @click="saveChecklistClicked"
          >
            Als Checkliste speichern
          </muc-button>
          <muc-button
            @click="copyUrl"
            variant="secondary"
            icon="copy"
          >
            Link kopieren
          </muc-button>
        </div>
      </div>
    </muc-intro>

    <div class="container">
      <div class="m-intro-vertical__grid">
        <div class="m-intro-vertical__grid-inner">
          <div v-if="loading">
            <skeleton-loader />
          </div>

          <div v-else-if="localStorageError">
            <h2 style="padding-bottom: 16px">
              Wir konnten Ihre Abfrage-Ergebnisse nicht finden.
            </h2>
            <p style="padding-bottom: 16px">
              Wenn Sie eine Abfrage zu Ihrer Lebenslage durchgeführt haben,
              sollten hier für Sie passende Leistungen gelistet sein. Wir
              konnten aber Ihre Ergebnisse nicht finden.
            </p>
            <p style="padding-bottom: 32px">
              Vielleicht haben Sie Ihre Browserdaten (Chronik) gelöscht oder die
              Speicherung von Cookies blockiert? Um Ihre Ergebnisse zu erhalten,
              passen Sie bitte Ihre Browser-Einstellungen an und starten Sie
              dann die Abfrage erneut.
            </p>
            <muc-button
              icon="arrow-right"
              iconAnimated
            >
              Abfrage neu starten
            </muc-button>
          </div>

          <div v-else-if="!localStorageError && !loadingError && snServices">
            <h2 style="padding-bottom: 32px">
              Vorgeschlagene Leistungen ({{ snServices.length }})
            </h2>
            <div>
              <div
                class="snServiceElement"
                v-for="service in snServices"
                :key="service.id"
                @click="openService(service)"
              >
                <span>
                  {{ service.serviceName }}
                </span>
              </div>
            </div>
          </div>

          <div v-else>
            <muc-callout type="error">
              <template #header> Fehler</template>
              <template #content>
                Beim Laden der Daten ist ein Fehler aufgetreten. Bitte versuchen
                Sie es zu einem späteren Zeitpunkt noch einmal.
              </template>
            </muc-callout>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { SNService } from "@/api/servicenavigator/ServiceNavigatorLookup.ts";
import type { ServiceNavigatorResult } from "@/api/servicenavigator/ServiceNavigatorResult.ts";
import type AuthorizationEventDetails from "@/types/AuthorizationEventDetails.ts";

import {
  MucBanner,
  MucButton,
  MucCheckbox,
  MucIntro,
  MucModal,
  MucPercentageSpinner,
} from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";

import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import ServiceInfoModal from "@/components/ServiceInfoModal.vue";
import { useDBSLoginWebcomponentPlugin } from "@/composables/DBSLoginWebcomponentPlugin.ts";
import {
  getAccessToken,
  getAPIBaseURL,
  getXSRFToken,
  LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT,
  QUERY_PARAM_CHECKLIST_ID,
  QUERY_PARAM_SN_RESULT_ID,
  QUERY_PARAM_SN_RESULT_NAME,
  QUERY_PARAM_SN_RESULT_SERVICES,
  setAccessToken,
} from "@/util/Constants.ts";

// Network activity and results
const loading = ref(false);
const localStorageError = ref("");
const loadingError = ref("");

// Modal states
const serviceInfoModalOpen = ref(false);
const requestLoginModalOpen = ref(false);
const saveChecklistModalOpen = ref(false);

// State
const dseAccepted = ref(false);
const lebenslageTitle = ref("Meine Lebenslage");
const lebenslageId = ref("");
const snServices = ref<SNService[] | null>(null);
const selectedService = ref<SNService | null>(null);

const { loggedIn } = useDBSLoginWebcomponentPlugin(_authChangedCallback);

const props = defineProps<{
  checklistDetailUrl: string;
}>();

onMounted(() => {
  loading.value = true;

  const snResult = getSnResults();

  if (snResult) {
    lebenslageTitle.value = snResult.name;
    lebenslageId.value = snResult.id;

    //todo replace with openapi generated client when backend is finished
    const url =
      getAPIBaseURL() +
      "/public/api/p13n-backend/servicenavigator?ids=" +
      snResult.services.join(",");
    fetch(url, {
      mode: "cors",
    })
      .then((resp) => {
        if (resp.ok) {
          resp.json().then((snServicesBody: SNService[]) => {
            snServices.value = snServicesBody;
          });
        } else {
          resp.text().then((errBody) => {
            throw Error(errBody);
          });
        }
      })
      .catch((error) => {
        console.debug(error);
      })
      .finally(() => (loading.value = false));
  } else {
    localStorageError.value =
      "No Data found in LocalStorage with key " +
      LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT;
    loading.value = false;
  }
});

function _authChangedCallback(authEventDetails?: AuthorizationEventDetails) {
  if (authEventDetails && authEventDetails.accessToken)
    setAccessToken(authEventDetails.accessToken);
}

function _requestLogin() {
  requestLoginModalOpen.value = false;
  document.dispatchEvent(
    new CustomEvent("authorization-request", {
      detail: {
        loginProvider: undefined,
        authLevel: undefined,
      },
    })
  );
}

function _saveChecklistAcceptedDSE() {
  //todo replace with openapi generated client when backend is finished
  loading.value = true;
  const url = getAPIBaseURL() + "/clients/api/p13n-backend/checklist";
  const checklistItemsDtos = snServices.value?.map((service) => {
    return {
      serviceID: service.id,
      checked: undefined,
      title: service.serviceName,
      note: service.summary,
      required: service.mandatory,
    } as any;
  });
  const body = JSON.stringify({
    title: lebenslageTitle.value,
    lebenslageId: lebenslageId.value,
    checklistItems: checklistItemsDtos,
  });

  fetch(url, {
    method: "POST",
    headers: {
      Authorization: "Bearer " + getAccessToken(),
      "Content-Type": "application/json",
      "x-xsrf-token": getXSRFToken(),
    },
    body: body,
    mode: "cors",
    credentials: "include",
  })
    .then((resp) => {
      if (resp.ok) {
        resp.json().then((createResponse: { id: string }) => {
          location.href = `${props.checklistDetailUrl}?${QUERY_PARAM_CHECKLIST_ID}=${createResponse.id}`;
        });
      } else {
        resp.text().then((errBody) => {
          throw Error(errBody);
        });
      }
    })
    .catch((error) => {
      console.debug(error);
      loadingError.value = error;
    })
    .finally(() => (loading.value = false));
}

function saveChecklistClicked() {
  if (loggedIn.value) {
    saveChecklistModalOpen.value = true;
  } else {
    requestLoginModalOpen.value = true;
  }
}

function getSnResults(): ServiceNavigatorResult | null {
  const snResultsFromUrl = getSnResultFromUrl();
  if (snResultsFromUrl) {
    return snResultsFromUrl;
  }
  const serviceNavigatorResultString = localStorage.getItem(
    LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT
  );
  if (serviceNavigatorResultString) {
    const snResult = JSON.parse(
      serviceNavigatorResultString
    ) as ServiceNavigatorResult;
    setUrlParams(snResult);
    localStorage.removeItem(LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT);
    return snResult;
  } else {
    return null;
  }
}

function setUrlParams(snResult: ServiceNavigatorResult) {
  if ("URLSearchParams" in window) {
    const url = new URL(window.location.href);
    url.searchParams.set(QUERY_PARAM_SN_RESULT_ID, snResult.id);
    url.searchParams.set(QUERY_PARAM_SN_RESULT_NAME, snResult.name);
    url.searchParams.set(
      QUERY_PARAM_SN_RESULT_SERVICES,
      snResult.services.join(",")
    );
    history.pushState(null, "", url);
  }
}

function getSnResultFromUrl(): ServiceNavigatorResult | undefined {
  if ("URLSearchParams" in window) {
    const searchParams = new URLSearchParams(window.location.search);
    const snResultId = searchParams.get(QUERY_PARAM_SN_RESULT_ID);
    const snResultName = searchParams.get(QUERY_PARAM_SN_RESULT_NAME);
    const snResultServices = searchParams.get(QUERY_PARAM_SN_RESULT_SERVICES);
    if (snResultName && snResultId && snResultServices) {
      return {
        id: snResultId,
        name: snResultName,
        services: snResultServices.split(",").map((value) => parseInt(value)),
      } as ServiceNavigatorResult;
    } else {
      return undefined;
    }
  }
}

function openService(service: SNService) {
  selectedService.value = service;
  serviceInfoModalOpen.value = true;
}

async function copyUrl() {
  const type = "text/plain";
  const clipboardItemData = {
    [type]: window.location.href,
  };
  const clipboardItem = new ClipboardItem(clipboardItemData);
  await navigator.clipboard.write([clipboardItem]);
}
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>

<style scoped>
.snServiceElement {
  cursor: pointer;
  padding: 16px 0;
  border-top: 1px solid var(--color-neutrals-blue-xlight);
}

.snServiceElement span {
  font-size: 18px;
  color: var(--color-brand-main-blue);
  font-weight: 700;
  line-height: 150%;
}

.mandatory-subtitle {
  font-family: "Open Sans";
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
  line-height: 150%;
  color: var(--color-neutrals-grey-light, #617586);
}
</style>
