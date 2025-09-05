<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />

    <div v-if="loading">
      <skeleton-loader />
    </div>
    <div v-else>
      <checklist-header
        v-if="checklist"
        :checklist="checklist"
      ></checklist-header>
      <div class="m-component m-component-form">
        <div class="container">
          <div class="m-component__grid">
            <div class="m-component__column">
              <h2 class="headline">Offene Aufgaben ({{ todoCount }})</h2>

              <checklist-list
                v-if="todoCount !== 0"
                v-model="openCheckList"
                @checked="onCheckedOpen"
              ></checklist-list>
              <muc-banner
                v-else
                class="banner"
                type="success"
                >Herzlichen Glückwunsch, Sie haben alle Aufgaben erledigt! Wir
                bewahren diese Checkliste noch bis zum 17. September 2026 für
                Sie auf. Danach wird sie automatisch gelöscht.</muc-banner
              >
              <h2 class="headline">Erledigte Aufgaben ({{ doneCount }})</h2>
              <checklist-list
                v-if="doneCount !== 0"
                v-model="closedCheckList"
                @checked="onCheckedClosed"
                :is-draggable="false"
              ></checklist-list>
              <muc-banner
                v-else
                class="banner"
                type="info"
                >Sie haben noch keine erledigten Aufgaben. Haken Sie Aufgaben in
                der Checkliste ab, um sie als erledigt zu markieren.</muc-banner
              >
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";
import type DummyChecklistItem from "@/api/dummyservice/DummyChecklistItem.ts";

import { MucBanner } from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { computed, onMounted, ref } from "vue";

import DummyChecklistService from "@/api/dummyservice/DummyChecklistService.ts";
import ChecklistHeader from "@/components/ChecklistHeader.vue";
import ChecklistList from "@/components/ChecklistList.vue";
import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import { QUERY_PARAM_CHECKLIST_ID } from "@/util/Constants.ts";

const checklist = ref<DummyChecklist>();
const loading = ref(true);
const openCheckList = ref<DummyChecklistItem[]>([]);
const closedCheckList = ref<DummyChecklistItem[]>([]);

const todoCount = computed(() => {
  return openCheckList.value.filter((value) => !value.checked).length;
});

const doneCount = computed(() => {
  return closedCheckList.value.filter((value) => value.checked).length;
});

function onCheckedOpen(serviceID: string) {
  const idx = openCheckList.value.findIndex((i) => i.serviceID === serviceID);
  if (idx === -1) return;

  const [item] = openCheckList.value.splice(idx, 1);
  if (item) {
    item.checked = new Date();

    openCheckList.value = [...openCheckList.value];
    closedCheckList.value = [item, ...closedCheckList.value];
  }
}

function onCheckedClosed(serviceID: string) {
  const idx = closedCheckList.value.findIndex((i) => i.serviceID === serviceID);
  if (idx === -1) return;

  const [item] = closedCheckList.value.splice(idx, 1);
  if (item) {
    item.checked = null;

    closedCheckList.value = [...closedCheckList.value];
    openCheckList.value = [item, ...openCheckList.value];
  }
}

onMounted(() => {
  loading.value = true;
  const dcl = new DummyChecklistService();
  dcl
    .getChecklists()
    .then((checklists) => {
      const urlParams = new URLSearchParams(window.location.search);
      const checklistId = urlParams.get(QUERY_PARAM_CHECKLIST_ID);
      const foundChecklist = checklists.find(
        (checklist) => checklist.id === checklistId
      );

      if (foundChecklist) {
        checklist.value = foundChecklist;
        openCheckList.value = foundChecklist.items.filter(
          (item) => item.checked === null
        );
        closedCheckList.value = foundChecklist.items.filter(
          (item) => item.checked !== null
        );
      } else {
        throw new Error("Checkliste wurde nicht gefunden");
      }
    })
    .finally(() => (loading.value = false));
});
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";

.banner .m-banner .container-fluid {
  margin-left: -60px !important; /* oder eine kleinere Zahl nach Wunsch */
  min-width: 575px !important;
}

.banner {
  padding-bottom: 56px;
}

.headline {
  padding-bottom: 32px;
}
</style>
