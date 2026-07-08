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
          icon="sign-in"
          @click="_requestLogin"
        >
          Anmelden
        </muc-button>
      </template>
    </muc-modal>

    <save-as-checklist-modal
      :title="lebenslageTitle"
      :open="saveChecklistModalOpen"
      :loading="loadingSave"
      :loading-error="loadingError"
      @save="_saveChecklistAcceptedDSE"
      @close="saveChecklistModalOpen = false"
    />

    <muc-intro
      :title="lebenslageTitle"
      :divider="false"
      variant="detail"
    >
      <div v-if="!localStorageError && !noResultsError">
        <p v-if="loadingServices">
          {{ t("preview.introTextLoading") }}
        </p>
        <p v-else>
          {{ t("preview.introText") }}
        </p>
        <div
          v-if="!loadingServices"
          style="padding-top: 32px"
        >
          <muc-button
            v-if="currentLang == DEFAULT_LANGUAGE"
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
            {{ t("preview.copyLink") }}
          </muc-button>
          <p
            class="visually-hidden"
            role="status"
          >
            {{ linkStateMessage }}
          </p>
        </div>
      </div>
    </muc-intro>

    <div
      class="container"
      style="padding-top: 2rem"
    >
      <div class="m-intro-vertical__grid">
        <div class="m-intro-vertical__grid-inner">
          <div v-if="loadingServices && !showLoader">
            <skeleton-loader />
          </div>

          <div
            v-if="loadingServices && showLoader"
            class="bluebox"
          >
            <div class="center-container">
              <div>
                <muc-spinner
                  size="265px"
                  :text="t('preview.loadingSpinnerText')"
                >
                </muc-spinner>
              </div>
            </div>
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

          <muc-callout
            v-else-if="noResultsError"
            type="warning"
          >
            <template #header> Keine Leistungen gefunden.</template>
            <template #content>
              <p>
                Für die eingegeben Angaben haben wir keine passenden Leistungen
                gefunden. Wir beraten Sie gerne zu Ihrem speziellen Fall über
                unseren Chatbot <span class="mde-bold">Muckl</span> oder unsere
                anderen
                <muc-link
                  class="mde-bold"
                  href="https://stadt.muenchen.de/rathaus/kontakt.html"
                  label="Kontaktkanäle"
                  noUnderline
                />
                .
              </p>
            </template>
          </muc-callout>

          <div v-else-if="!localStorageError && !loadingError && snServices">
            <h2 style="padding-bottom: 32px">
              {{ t("preview.suggestedServices") }} ({{ snServices.length }})
            </h2>
            <ul class="snServiceList">
              <li
                class="snServiceElement mde-b2 mde-bold"
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
                  class="required-label mde-b2"
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
                <p>
                  Es gibt aktuell leider ein technisches Problem mit dieser
                  Funktion. Bitte versuchen Sie es später noch einmal.
                </p>
              </template>
            </muc-callout>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type {
  ChecklistItemDTO,
  ChecklistItemServiceNavigatorDTO,
} from "@/api/dbs-clients/generated-p13n-service-api";
import type { ServiceNavigatorResult } from "@/api/servicenavigator/ServiceNavigatorResult.ts";
import type AuthorizationEventDetails from "@/types/AuthorizationEventDetails.ts";

import {
  MucButton,
  MucCallout,
  MucIntro,
  MucLink,
  MucModal,
  MucSpinner,
} from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";

import {
  useChecklistsApi,
  usePublicServiceNavigatorEndpoints,
} from "@/api/compositions/UseChecklistsApi.ts";
import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import ServiceInfoModal from "@/components/ServiceInfoModal.vue";
import { useDBSLoginWebcomponentPlugin } from "@/composables/DBSLoginWebcomponentPlugin.ts";
import { useLanguageObserver } from "@/composables/LanguageObserver.ts";
import {
  DEFAULT_LANGUAGE,
  LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT,
  QUERY_PARAM_CHECKLIST_ID,
  QUERY_PARAM_SN_RESULT_ID,
  QUERY_PARAM_SN_RESULT_NAME,
  QUERY_PARAM_SN_RESULT_SERVICES,
  setAccessToken,
} from "@/util/Constants.ts";

// Network activity and results
const loadingServices = ref(false);
const loadingSave = ref(false);
const localStorageError = ref("");
const loadingError = ref("");
const noResultsError = ref("");

const LOCALSTORAGE_KEY_LOGGED_IN = "logged.in";

// Modal states
const serviceInfoModalOpen = ref(false);
const requestLoginModalOpen = ref(false);
const saveChecklistModalOpen = ref(false);

// State
const lebenslageTitle = ref("Meine Lebenslage");
const lebenslageId = ref("");
const snServices = ref<ChecklistItemServiceNavigatorDTO[] | null>(null);
const selectedService = ref<ChecklistItemServiceNavigatorDTO | null>(null);
const linkStateMessage = ref("");

/**
 * Minimum time the loader is shown in milliseconds
 * even if the request to load the services is faster
 */
const minLoaderTimeInMs = 1500;

const { loggedIn } = useDBSLoginWebcomponentPlugin(_authChangedCallback);
const { currentLang } = useLanguageObserver();
const { t, locale, availableLocales } = useI18n();

const props = defineProps<{
  checklistDetailUrl: string;
  newChecklistUrl: string;
  showLoader: boolean;
}>();

onMounted(async () => {
  loadingServices.value = true;
  loadingError.value = "";

  if (availableLocales.includes(currentLang.value)) {
    locale.value = currentLang.value;
  }

  const snResult = getSnResults();

  if (snResult) {
    lebenslageTitle.value = snResult.name;
    lebenslageId.value = snResult.id;

    if (snResult.services.length > 0) {
      const snApi = usePublicServiceNavigatorEndpoints();
      try {
        let requestedLang;
        try {
          const requestedLocale = new Intl.Locale(locale.value);
          if (requestedLocale) {
            requestedLang = requestedLocale.language;
          }
        } catch {
          console.debug(
            "couldn't instantiate language with locale",
            locale.value
          );
        }

        const delayPromise = new Promise<void>((resolve) =>
          setTimeout(resolve, minLoaderTimeInMs)
        );
        const snResponsePromise = snApi.getServicesByIds({
          ids: snResult.services.join(","),
          lang: requestedLang ? requestedLang : undefined,
        });

        const snServicesBody = (
          await Promise.all([delayPromise, snResponsePromise])
        )[1];

        snServices.value = snServicesBody.sort((a, b) => {
          return a.required === b.required ? 0 : a.required ? -1 : 1;
        });
      } catch (error) {
        console.debug("Error loading checklist: ", error);
        loadingError.value = error as string;
      } finally {
        loadingServices.value = false;
      }
    } else {
      loadingServices.value = false;
    }
  } else {
    localStorageError.value =
      "No Data found in LocalStorage with key " +
      LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT;
    loadingServices.value = false;
  }

  if (localStorage.getItem(LOCALSTORAGE_KEY_LOGGED_IN)) {
    saveChecklistModalOpen.value = true;
    localStorage.removeItem(LOCALSTORAGE_KEY_LOGGED_IN);
  }

  const updateLebenslage = document.querySelector(
    ".m-breadcrumb__list-item-current"
  );
  if (updateLebenslage) {
    updateLebenslage.textContent = "Lebenslage: " + lebenslageTitle.value;
  }

  document.title =
    "Lebenslage: " + lebenslageTitle.value + " - Landeshauptstadt München";
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

async function _saveChecklistAcceptedDSE() {
  loadingSave.value = true;
  loadingError.value = "";

  const checklistsApi = useChecklistsApi();
  const checklistItemsDtos: ChecklistItemDTO[] | undefined =
    snServices.value?.map((service) => {
      return {
        serviceID: service.serviceID,
        checked: undefined,
        title: service.title,
        note: service.note,
        required: service.required,
      } as ChecklistItemDTO;
    });
  try {
    if (checklistItemsDtos) {
      const createResponse = await checklistsApi.createChecklist({
        checklistCreateDTO: {
          title: lebenslageTitle.value,
          situationId: lebenslageId.value,
          checklistItems: checklistItemsDtos,
        },
      });
      location.href = `${props.checklistDetailUrl}?${QUERY_PARAM_CHECKLIST_ID}=${createResponse.id}`;
    }
  } catch (error) {
    console.debug(error);
    loadingError.value = error as string;
  } finally {
    loadingSave.value = false;
  }
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
    if (snResult.services.length === 0) {
      noResultsError.value = "No results for this query";
    }
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
    } else if (snResultName && snResultId) {
      noResultsError.value = "No results for this query";
      return {
        id: snResultId,
        name: snResultName,
        services: [],
      } as ServiceNavigatorResult;
    } else {
      return undefined;
    }
  }
}

function openService(service: ChecklistItemServiceNavigatorDTO) {
  selectedService.value = service;
  serviceInfoModalOpen.value = true;
}

async function copyUrl() {
  const type = "text/plain";
  const clipboardItemData = {
    [type]: window.location.href,
  };
  const clipboardItem = new ClipboardItem(clipboardItemData);
  await navigator.clipboard.write([clipboardItem]).then(() => {
    linkStateMessage.value = "Link wurde kopiert";
    setTimeout(() => {
      linkStateMessage.value = "";
    }, 5000);
  });
}
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.1.23/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>

<style scoped>
.m-intro {
  margin-bottom: 40px;
}

.bluebox {
  padding: 16px;
  margin-bottom: 48px;
  background-color: var(--color-neutrals-blue-xlight);
}

.center-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
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
  color: var(--mde-color-brand-mde-blue);
}

.snServiceElement:hover,
.snServiceElement:focus {
  text-decoration: underline;
}

.snServiceElement .required-label {
  color: var(--mde-color-neutral-grey);
}

.snServiceElement:hover .required-label,
.snServiceElement:focus .required-label {
  color: var(--mde-color-neutral-grey-light);
}

/* CSS for desktop */
@media (min-width: 768px) {
  .bluebox {
    margin-bottom: 72px;
  }

  .center-container {
    margin-top: 124px;
    margin-bottom: 124px;
  }
}
</style>
