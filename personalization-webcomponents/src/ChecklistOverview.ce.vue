<template>
  <main>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />
    <div
      v-if="checklists.length > 0 || !displayOptionDetailScreen"
      :class="
        displayOptionDetailScreen ? 'details-background' : 'overview-padding'
      "
    >
      <div class="container">
        <div class="header">
          <div class="headline">
            <span class="header-icon">
              <muc-icon icon="calendar" />
            </span>
            <h2 tabindex="0">
              <span v-if="displayOptionDetailScreen"
                >Meine weiteren Checklisten</span
              >
              <span v-else>Meine Checklisten</span>

              <span
                v-if="
                  checklists.length &&
                  !displayOptionDetailScreen &&
                  !loadingError
                "
              >
                ({{ checklists.length }})</span
              >
            </h2>
          </div>
          <muc-link
            v-if="!loadingError && checklists.length > 2 && !isMobile"
            label="Alle Checklisten anzeigen"
            icon="chevron-right"
            target="_self"
            no-underline
            :href="checklistOverviewUrl"
          />
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
          <muc-link
            v-if="!loadingError && checklists.length > 2 && isMobile"
            class="mobile-link"
            label="Alle Checklisten anzeigen"
            icon="chevron-right"
            target="_self"
            no-underline
            :href="checklistOverviewUrl"
          />
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";

import {
  MucCardContainer,
  MucIcon,
  MucLink,
} from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";

import DummyChecklistService from "@/api/dummyservice/DummyChecklistService.ts";
import ChecklistCardViewer from "@/components/ChecklistCardViewer.vue";
import ErrorAlert from "@/components/common/ErrorAlert.vue";
import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import { QUERY_PARAM_CHECKLIST_ID } from "@/util/Constants.ts";

const { displayedOnDetailScreen } = defineProps<{
  checklistDetailUrl: string;
  checklistOverviewUrl: string;
  newChecklistUrl: string;
  displayedOnDetailScreen: string;
}>();

const checklists = ref<DummyChecklist[]>([]);
const loading = ref(true);
const loadingError = ref(false);
const isMobile = ref(false);
const displayOptionDetailScreen =
  displayedOnDetailScreen.toLowerCase() === "true";

const checksMobile = () => {
  isMobile.value = window.matchMedia("(max-width: 767px)").matches;
};

onMounted(() => {
  loading.value = true;
  checksMobile();
  window.addEventListener("resize", checksMobile);

  const dcl = new DummyChecklistService();
  dcl
    .getChecklists()
    .then((checklist) => {
      checklists.value = checklist;

      if (displayOptionDetailScreen) {
        const urlParams = new URLSearchParams(window.location.search);
        const checklistId = urlParams.get(QUERY_PARAM_CHECKLIST_ID);
        checklists.value = checklists.value.filter(
          (checklist) => checklist.id != checklistId
        );
      }
    })
    .finally(() => (loading.value = false));
});
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>

<style scoped>
/* Padding on overview page */
.overview-padding {
  padding-top: 40px;
}

/* Background color on details page */
.details-background {
  background-color: var(--color-neutrals-blue-xlight);
  padding: 24px 0;
}

/* Header styles */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 24px;
}

/* Headline styles */
.headline {
  display: flex;
  align-items: center;
}

.header-icon {
  margin-right: 8px;
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
  .overview-padding {
    padding-top: 40px;
  }

  .details-background {
    padding: 64px 0;
  }

  .checklist-card-container {
    grid-template-columns: repeat(auto-fit, 589px);
  }
}
</style>
