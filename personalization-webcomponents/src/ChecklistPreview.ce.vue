<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />

    <service-info-modal
      :open="serviceInfoModalOpen"
      :service="selectedService!"
      :t="t"
      @close="serviceInfoModalOpen = false"
      @cancel="serviceInfoModalOpen = false"
    />

    <request-login-modal
      :open="requestLoginModalOpen"
      @close="requestLoginModalOpen = false"
      @sign-in="_requestLogin"
    />

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
        <p v-if="loadingServices && showLoader">
          {{ t("preview.introTextLoading") }}
        </p>
        <p v-else>
          {{ t("preview.introText") }}
        </p>

        <checklist-preview-action-buttons
          v-if="!loadingServices"
          :currentLang="currentLang"
          :t="t"
          @save="saveChecklistClicked"
        />
      </div>
    </muc-intro>

    <div
      class="container"
      style="padding-top: 2rem"
    >
      <div class="m-intro-vertical__grid">
        <div class="m-intro-vertical__grid-inner">
          <div v-if="loadingServices && (!firstLoad || !showLoader)">
            <skeleton-loader />
          </div>

          <div
            v-if="loadingServices && firstLoad && showLoader"
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

          <local-storage-callout
            v-else-if="localStorageError"
            :newChecklistUrl="newChecklistUrl"
            :t="t"
          />

          <no-results-callout
            v-else-if="noResultsError"
            :currentLang="currentLang"
            :t="t"
          />

          <checklist-preview-list
            v-else-if="!localStorageError && !loadingError && snServices"
            :snServices="snServices"
            :t="t"
            @openService="openService($event)"
            style="padding-bottom: 40px"
          />

          <technical-issues-callout
            v-else-if="loadingError"
            :t="t"
          />

          <save-as-checklist-callout
            v-if="
              !localStorageError &&
              !loadingError &&
              snServices &&
              currentLang !== DEFAULT_LANGUAGE
            "
            :t="t"
          />
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

import { MucIntro, MucSpinner } from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";

import {
  useChecklistsApi,
  usePublicServiceNavigatorEndpoints,
} from "@/api/compositions/UseChecklistsApi.ts";
import ChecklistPreviewActionButtons from "@/components/checklistPreview/ChecklistPreviewActionButtons.vue";
import ChecklistPreviewList from "@/components/checklistPreview/ChecklistPreviewList.vue";
import LocalStorageCallout from "@/components/checklistPreview/LocalStorageCallout.vue";
import NoResultsCallout from "@/components/checklistPreview/NoResultsCallout.vue";
import RequestLoginModal from "@/components/checklistPreview/RequestLoginModal.vue";
import SaveAsChecklistCallout from "@/components/checklistPreview/SaveAsChecklistCallout.vue";
import TechnicalIssuesCallout from "@/components/checklistPreview/TechnicalIssuesCallout.vue";
import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import SaveAsChecklistModal from "@/components/SaveAsChecklistModal.vue";
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
const firstLoad = ref(true);

/**
 * Minimum time the loader is shown in milliseconds
 * even if the request to load the services is faster
 */
const minLoaderTimeInMs = 3000;

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
          setTimeout(
              resolve,
              firstLoad.value ? minLoaderTimeInMs : 0
          )
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
    updateLebenslage.textContent =
      t("preview.pageTitleLifeSituation") + " " + lebenslageTitle.value;
  }

  document.title =
    t("preview.pageTitleLifeSituation") +
    " " +
    lebenslageTitle.value +
    " - Landeshauptstadt München";
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
    firstLoad.value = false;
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
