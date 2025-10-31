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
                <strong>„{{ lebenslageTitle }}”</strong>
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
        >
          Es ist ein Fehler beim Speichern der Checkliste aufgetreten. Bitte
          versuchen Sie es zu einem späteren Zeitpunkt noch einmal.
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
      variant="detail"
    >
      <div v-if="!localStorageError">
        <p>
          Vielen Dank für Ihre Angaben! Basierend auf Ihren Antworten könnten
          folgende Leistungen für Sie passen.
        </p>
        <div style="padding-top: 32px">
          <muc-button
            icon="order-bool-ascending"
            style="margin-right: 16px; margin-bottom: 16px"
            @click="saveChecklistClicked"
          >
            Als Checkliste speichern
          </muc-button>
          <muc-button
            @click="copyUrl"
            variant="secondary"
            icon="copy-link"
            spin-icon-on-click
          >
            Link kopieren
          </muc-button>
        </div>
      </div>
    </muc-intro>

    <div
      class="container"
      style="padding-top: 2rem"
    >
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
            <a :href="newChecklistUrl">
              <muc-button
                icon="arrow-right"
                iconAnimated
              >
                Abfrage neu starten
              </muc-button>
            </a>
          </div>

          <div v-else-if="!localStorageError && !loadingError && snServices">
            <h2 style="padding-bottom: 32px">
              Vorgeschlagene Leistungen ({{ snServices.length }})
            </h2>
            <ul class="snServiceList">
              <li
                class="snServiceElement"
                v-for="service in snServices"
                :key="service.serviceID"
                @click="openService(service)"
                tabindex="0"
                @keydown.enter="openService(service)"
                :aria-label="
                  service.required
                    ? service.title + ' – verpflichtend'
                    : service.title
                "
              >
                {{ service.title }}
                <span
                  class="required-label"
                  v-if="service.required"
                  >– verpflichtend</span
                >
              </li>
            </ul>
          </div>

          <div v-else>
            <muc-callout type="error">
              <template #header>
                Die Checkliste kann nicht geladen werden.
              </template>
              <template #content>
                Es gibt aktuell leider ein technisches Problem mit dieser
                Funktion. Bitte versuchen Sie es später noch einmal.
              </template>
            </muc-callout>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type ChecklistItemServiceNavigator from "@/api/persservice/ChecklistItemServiceNavigator.ts";
import type { ServiceNavigatorResult } from "@/api/servicenavigator/ServiceNavigatorResult.ts";
import type AuthorizationEventDetails from "@/types/AuthorizationEventDetails.ts";

import {
  MucBanner,
  MucButton,
  MucCallout,
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

const LOCALSTORAGE_KEY_LOGGED_IN = "logged.in";

// Modal states
const serviceInfoModalOpen = ref(false);
const requestLoginModalOpen = ref(false);
const saveChecklistModalOpen = ref(false);

// State
const dseAccepted = ref(false);
const lebenslageTitle = ref("Meine Lebenslage");
const lebenslageId = ref("");
const snServices = ref<ChecklistItemServiceNavigator[] | null>(null);
const selectedService = ref<ChecklistItemServiceNavigator | null>(null);

const { loggedIn } = useDBSLoginWebcomponentPlugin(_authChangedCallback);

const props = defineProps<{
  checklistDetailUrl: string;
  newChecklistUrl: string;
}>();

onMounted(() => {
  loading.value = true;
  loadingError.value = "";

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
      headers: {
        Accept: "application/json",
      },
    })
      .then((resp) => {
        if (resp.ok) {
          resp
            .json()
            .then((snServicesBody: ChecklistItemServiceNavigator[]) => {
              snServices.value = snServicesBody;
            });
        } else {
          resp.text().then((errorText) => {
            console.debug("Error loading checklist: ", errorText);
            loadingError.value = errorText;
          });
        }
      })
      .catch((error) => {
        console.debug("Error loading checklist: ", error);
        loadingError.value = error;
      })
      .finally(() => (loading.value = false));
  } else {
    localStorageError.value =
      "No Data found in LocalStorage with key " +
      LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT;
    loading.value = false;
  }

  if (localStorage.getItem(LOCALSTORAGE_KEY_LOGGED_IN)) {
    saveChecklistModalOpen.value = true;
    localStorage.removeItem(LOCALSTORAGE_KEY_LOGGED_IN);
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
  localStorage.setItem(LOCALSTORAGE_KEY_LOGGED_IN, "true");
}

function _saveChecklistAcceptedDSE() {
  //todo replace with openapi generated client when backend is finished
  loading.value = true;
  loadingError.value = "";
  const url = getAPIBaseURL() + "/clients/api/p13n-backend/checklist";
  const checklistItemsDtos = snServices.value?.map((service) => {
    return {
      serviceID: service.serviceID,
      checked: undefined,
      title: service.title,
      note: service.note,
      required: service.required,
    };
  });
  const body = JSON.stringify({
    title: lebenslageTitle.value,
    situationId: lebenslageId.value,
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

function openService(service: ChecklistItemServiceNavigator) {
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
@import url("https://assets.muenchen.de/mde/1.1.6/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>

<style scoped>
.m-intro {
  margin-bottom: 40px;
}

.snServiceList {
  list-style-type: none;
  padding-left: 0;
  margin: 0;
}

.snServiceElement {
  cursor: pointer;
  padding: 16px 0;
  border-top: 1px solid var(--mde-color-neutral-beau-blue-x-light);
  font-size: 1rem;
  color: var(--mde-color-brand-mde-blue);
  font-weight: 700;
  line-height: 150%;
}

.snServiceElement:hover,
.snServiceElement:focus {
  text-decoration: underline;
  text-decoration-color: var(--mde-color-brand-mde-blue-x-dark);
}

.snServiceElement .required-label {
  color: var(--mde-color-neutral-grey);
  font-family: "Open Sans", sans-serif;
  font-size: 1rem;
  font-style: normal;
  font-weight: 400;
  line-height: 150%;
}

.snServiceElement:hover .required-label,
.snServiceElement:focus .required-label {
  color: var(--mde-color-neutral-grey-light);
}
</style>
