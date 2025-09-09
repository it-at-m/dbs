<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />
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
              <template #header> Fehler </template>
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

import { MucButton, MucCallout, MucIntro } from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";

import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import {
  getAPIBaseURL,
  LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT,
  QUERY_PARAM_SN_RESULT_ID,
  QUERY_PARAM_SN_RESULT_NAME,
  QUERY_PARAM_SN_RESULT_SERVICES,
} from "@/util/Constants.ts";

const loading = ref(true);
const localStorageError = ref("");
const loadingError = ref("");

const lebenslageTitle = ref("Meine Lebenslage");
const snServices = ref<SNService[] | null>(null);

onMounted(() => {
  loading.value = true;

  const snResult = getSnResults();

  if (snResult) {
    lebenslageTitle.value = snResult.name;
    const url =
      getAPIBaseURL() +
      "/public/api/p13n-backend/servicenavigator?ids=" +
      snResult.services.join(",");
    fetch(url)
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
  //TODO use correct modal dialog to show information from vue-patternlab
  window.alert(`
  Service: ${service.serviceName}
  Summary: ${service.summary}
  `);
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
</style>
