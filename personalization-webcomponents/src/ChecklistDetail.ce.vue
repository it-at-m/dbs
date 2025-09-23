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
              <h2 class="headline">
                Offene Aufgaben ({{ openCheckList.length }})
              </h2>

              <checklist-list
                v-if="openCheckList.length !== 0"
                v-model="openCheckList"
                @checked="onCheckedOpen"
              ></checklist-list>
              <muc-banner
                v-else
                class="banner"
                type="success"
                >Herzlichen Glückwunsch, Sie haben alle Aufgaben erledigt! Wir
                bewahren diese Checkliste noch bis zum {{ deletionDate }} für
                Sie auf. Danach wird sie automatisch gelöscht.
              </muc-banner>
              <h2 class="headline">
                Erledigte Aufgaben ({{ closedCheckList.length }})
              </h2>
              <checklist-list
                v-if="closedCheckList.length !== 0"
                v-model="closedCheckList"
                @checked="onCheckedClosed"
                :is-draggable="false"
              ></checklist-list>
              <muc-banner
                v-else
                class="banner"
                type="info"
                >Sie haben noch keine erledigten Aufgaben. Haken Sie Aufgaben in
                der Checkliste ab, um sie als erledigt zu markieren.
              </muc-banner>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type Checklist from "@/api/persservice/Checklist.ts";
import type AuthorizationEventDetails from "@/types/AuthorizationEventDetails.ts";

import { MucBanner } from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { computed, ref } from "vue";

import ChecklistService from "@/api/persservice/ChecklistService.ts";
import ChecklistHeader from "@/components/ChecklistHeader.vue";
import ChecklistList from "@/components/ChecklistList.vue";
import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import { useDBSLoginWebcomponentPlugin } from "@/composables/DBSLoginWebcomponentPlugin.ts";
import { QUERY_PARAM_CHECKLIST_ID, setAccessToken } from "@/util/Constants.ts";

const checklist = ref<Checklist | null>(null);
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

    const urlParams = new URLSearchParams(window.location.search);
    const checklistId = urlParams.get(QUERY_PARAM_CHECKLIST_ID);

    if (checklistId) {
      service
        .getChecklist(checklistId)
        .then((resp) => {
          if (resp.ok) {
            resp.json().then((checklistResponse: Checklist) => {
              checklist.value = checklistResponse;
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
      //todo show error no query param in url
    }
  }
}

const openCheckList = computed(() => {
  if (checklist.value && checklist.value.checklistItems) {
    return checklist.value.checklistItems.filter((value) => !value.checked);
  } else {
    return [];
  }
});

const closedCheckList = computed(() => {
  if (checklist.value && checklist.value.checklistItems) {
    return checklist.value.checklistItems.filter((value) => value.checked);
  } else {
    return [];
  }
});

const deletionDate = computed(oldValue => {
  if(checklist && checklist.value && checklist.value.lastUpdate) {
    let date = new Date(checklist.value.lastUpdate);
    date.setFullYear(date.getFullYear() + 1);
    return date.toLocaleString("de-DE", {year: 'numeric', month: 'numeric', day: 'numeric'});
  } else {
    return "nächsten Jahr";
  }
})

function onCheckedOpen(serviceID: string) {
  if (checklist.value) {
    loading.value = true;
    const service = new ChecklistService();
    service
      .checkChecklistentry(checklist.value.id, serviceID)
      .then((resp) => {
        if (resp.ok) {
          resp.json().then((newChecklist) => {
            checklist.value = newChecklist;
          });
        } else {
          resp.text().then((errBody) => {
            throw Error(errBody);
          });
        }
      })
      .catch((err) => {
        console.debug(err);
      })
      .finally(() => (loading.value = false));
  }
}

function onCheckedClosed(serviceID: string) {
  if (checklist.value) {
    loading.value = true;
    const service = new ChecklistService();
    service
      .uncheckChecklistentry(checklist.value.id, serviceID)
      .then((resp) => {
        if (resp.ok) {
          resp.json().then((newChecklist) => {
            checklist.value = newChecklist;
          });
        } else {
          resp.text().then((errBody) => {
            throw Error(errBody);
          });
        }
      })
      .catch((err) => {
        console.debug(err);
      })
      .finally(() => (loading.value = false));
  }
}
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
