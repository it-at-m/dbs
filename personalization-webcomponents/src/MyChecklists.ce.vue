<template>
  <main
    v-if="loggedIn"
    class="container"
  >
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />
    <h2 style="display: flex; align-items: center; margin-bottom: 24px">
      <muc-icon
        style="width: 32px; height: 32px; margin-right: 8px"
        icon="order-bool-ascending"
      ></muc-icon>
      Aktive Checklisten ({{ checklists.length }})
    </h2>
    <muc-card-container
      v-if="loading"
      class="checklist-card-container"
    >
      <skeleton-loader
        v-for="elem in [1, 2, 3, 4]"
        :key="elem"
      >
      </skeleton-loader>
    </muc-card-container>
    <muc-card-container
      v-else
      class="checklist-card-container"
    >
      <checklist-card
        v-for="checklist in checklists"
        :key="checklist.id"
        :checklist="checklist"
        :checklist-detail-url="checklistDetailUrl"
      >
      </checklist-card>
      <add-checklist-card
          title="Neue Checkliste"
          :new-checklist-url="newChecklistUrl"
      >
        <template #content>
          <icon-add-checklist />
        </template>
      </add-checklist-card>
    </muc-card-container>
  </main>
</template>

<script setup lang="ts">
import type Checklist from "@/api/persservice/Checklist.ts";
import type AuthorizationEventDetails from "@/types/AuthorizationEventDetails.ts";

import {MucCardContainer, MucIcon} from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { ref } from "vue";

import ChecklistService from "@/api/persservice/ChecklistService.ts";
import ChecklistCard from "@/components/ChecklistCard.vue";
import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import { useDBSLoginWebcomponentPlugin } from "@/composables/DBSLoginWebcomponentPlugin.ts";
import { setAccessToken } from "@/util/Constants.ts";
import IconAddChecklist from "@/components/icons/IconAddChecklist.vue";
import AddChecklistCard from "@/components/AddChecklistCard.vue";

defineProps<{
  checklistDetailUrl: string;
  newChecklistUrl: string;
}>();

const checklists = ref<Checklist[]>([]);
const loading = ref(true);

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
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>

<style scoped>
main {
  padding-top: 40px;
  padding-bottom: 40px;
}

.checklist-card-container {
  grid-template-columns: repeat(auto-fit, 100%);
}

@media (min-width: 576px) {
  main {
    padding-top: 56px;
    padding-bottom: 56px;
  }
}

@media (min-width: 768px) {
  .checklist-card-container {
    grid-template-columns: repeat(auto-fit, 589px);
  }
}
</style>
