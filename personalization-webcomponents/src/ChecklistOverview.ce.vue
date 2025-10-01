<template>
  <main v-if="loggedIn">
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />
    <div
      v-if="checklists.length > 0 || !displayOptionDetailScreen"
      :class="
        displayOptionDetailScreen ? 'details-background' : 'overview-margin'
      "
    >
      <div class="container">
        <div class="header">
          <h2
            tabindex="0"
            style="display: flex; align-items: center"
          >
            <muc-icon
              style="width: 32px; height: 32px; margin-right: 8px"
              icon="order-bool-ascending"
            />
            <span v-if="displayOptionDetailScreen"
              >Meine weiteren Checklisten</span
            >
            <span v-else>Meine Checklisten</span>

            <span
              v-if="
                checklists.length && !displayOptionDetailScreen && !loadingError
              "
            >
              ({{ checklists.length }})</span
            >
          </h2>
          <muc-button
            v-if="!loadingError && checklists.length > 2 && !isMobile"
            icon="arrow-right"
            variant="ghost"
            @click="goToChecklistOverviewLink"
          >
            Alle Checklisten anzeigen
          </muc-button>
        </div>
        <error-alert
          v-if="loadingError"
          class="no-padding-top"
          message="Es gibt aktuell leider ein technisches Problem mit dieser Funktion. Bitte versuchen Sie es später noch einmal."
          header="Ihre Checklisten können zur Zeit nicht geladen werden."
        />
        <muc-card-container
          v-else-if="loading"
          class="checklist-card-container"
        >
          <skeleton-loader />
        </muc-card-container>
        <div v-else>
          <checklist-card-viewer
            :all-checklists="checklists"
            :is-mobile="isMobile"
            :new-checklist-url="newChecklistUrl"
            :checklist-detail-url="checklistDetailUrl"
            :displayed-on-detail-screen="displayOptionDetailScreen"
          />
          <muc-button
            v-if="!loadingError && checklists.length > 2 && isMobile"
            class="mobile-link"
            icon="arrow-right"
            variant="ghost"
            @click="goToChecklistOverviewLink"
          >
            Alle Checklisten anzeigen
          </muc-button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import type Checklist from "@/api/persservice/Checklist.ts";
import type AuthorizationEventDetails from "@/types/AuthorizationEventDetails.ts";

import {
  MucButton,
  MucCardContainer,
  MucIcon,
} from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";

import ChecklistService from "@/api/persservice/ChecklistService.ts";
import ChecklistCardViewer from "@/components/ChecklistCardViewer.vue";
import ErrorAlert from "@/components/common/ErrorAlert.vue";
import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import { useDBSLoginWebcomponentPlugin } from "@/composables/DBSLoginWebcomponentPlugin.ts";
import { setAccessToken } from "@/util/Constants.ts";

const { checklistOverviewUrl, displayedOnDetailScreen } = defineProps<{
  checklistDetailUrl: string;
  checklistOverviewUrl: string;
  newChecklistUrl: string;
  displayedOnDetailScreen: string;
}>();

const checklists = ref<Checklist[]>([]);
const loading = ref(true);
const loadingError = ref(false);
const isMobile = ref(false);
const displayOptionDetailScreen =
  displayedOnDetailScreen.toLowerCase() === "true";

const { loggedIn } = useDBSLoginWebcomponentPlugin(_authChangedCallback);

function _authChangedCallback(authEventDetails?: AuthorizationEventDetails) {
  if (authEventDetails && authEventDetails.accessToken) {
    setAccessToken(authEventDetails.accessToken);
    loadChecklists();
  }
}

function loadChecklists() {
  if (loggedIn.value) {
    loading.value = true;
    const service = new ChecklistService();
    service
      .getChecklists()
      .then((resp) => {
        if (resp.ok) {
          resp.json().then((checklistResponse: Checklist[]) => {
            checklists.value = checklistResponse;
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
  }
}

const goToChecklistOverviewLink = () => {
  location.href = checklistOverviewUrl;
};

const checksMobile = () => {
  isMobile.value = window.matchMedia("(max-width: 767px)").matches;
};

onMounted(() => {
  checksMobile();
  window.addEventListener("resize", checksMobile);
});
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>

<style scoped>
/* Margin on overview page */
.overview-margin {
  margin-top: 40px;
  margin-bottom: 48px;
}

/* Background color on details page */
.details-background {
  background-color: var(--mde-color-neutral-beau-blue-x-light);
  padding: 24px 0;
}

/* Header styles */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 24px;
}

/* Mobile link styles */
.mobile-link {
  padding-top: 24px;
}

/* No padding-top on error message */
.no-padding-top {
  padding-top: 0 !important;
}

.checklist-card-container {
  grid-template-columns: repeat(auto-fit, 100%);
}

/* CSS for desktop */
@media (min-width: 768px) {
  .overview-margin {
    margin: 56px 0;
  }

  .details-background {
    padding: 64px 0;
  }

  .checklist-card-container {
    grid-template-columns: repeat(auto-fit, 589px);
  }
}
</style>
