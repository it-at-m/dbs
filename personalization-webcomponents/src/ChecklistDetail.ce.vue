<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />

    <muc-modal
      v-if="requestToDeleteItem"
      :open="openAcceptDeleteDialog"
      @close="openAcceptDeleteDialog = false"
      @cancel="openAcceptDeleteDialog = false"
    >
      <template #title>
        Löschen der Aufgabe
      </template>

      <template #body>
        <muc-banner
          type="warning"
        >
          <p>
            Mit dieser Aktion entfernen Sie die Aufgabe <strong>{{ requestToDeleteItem.title }}</strong> endgültig aus Ihrer Checkliste.
          </p>
        </muc-banner>
      </template>
      <template #buttons>
        <muc-button
          icon="trash"
          @click="deleteItem(requestToDeleteItem)"
        >
          Aufgabe löschen
        </muc-button>
        <muc-button
            variant="secondary"
            @click="openAcceptDeleteDialog = false"
        >
          Abbrechen
        </muc-button>
      </template>
    </muc-modal>

    <main v-if="loading">
      <skeleton-loader />
    </main>
    <main v-else>
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
                :checklist-items="openCheckList"
                :disabled="loadingUpdate || loadingCheck"
                @checked="onCheckedOpen"
                @delete="onRequestDeleteItem"
                @sort="onSortOpen"
              ></checklist-list>
              <muc-banner
                v-else
                class="banner"
                type="success"
              >
                Herzlichen Glückwunsch, Sie haben alle Aufgaben erledigt! Wir
                bewahren diese Checkliste noch bis zum {{ deletionDate }} für
                Sie auf. Danach wird sie automatisch gelöscht.
              </muc-banner>
              <h2 class="headline">
                Erledigte Aufgaben ({{ closedCheckList.length }})
              </h2>
              <checklist-list
                v-if="closedCheckList.length !== 0"
                :disabled="loadingUpdate || loadingCheck"
                :checklist-items="closedCheckList"
                @checked="onCheckedClosed"
                @delete="onRequestDeleteItem"
                :is-draggable="false"
              ></checklist-list>
              <muc-banner
                v-else
                class="banner"
                type="info"
              >
                Sie haben noch keine erledigten Aufgaben. Haken Sie Aufgaben in
                der Checkliste ab, um sie als erledigt zu markieren.
              </muc-banner>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import type Checklist from "@/api/persservice/Checklist.ts";
import type ChecklistItemServiceNavigator from "@/api/persservice/ChecklistItemServiceNavigator.ts";
import type ChecklistServiceNavigator from "@/api/persservice/ChecklistServiceNavigator.ts";
import type AuthorizationEventDetails from "@/types/AuthorizationEventDetails.ts";

import {MucBanner, MucButton, MucModal} from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import {computed, type Ref, ref, type UnwrapRef} from "vue";

import ChecklistService from "@/api/persservice/ChecklistService.ts";
import ChecklistHeader from "@/components/ChecklistHeader.vue";
import ChecklistList from "@/components/ChecklistList.vue";
import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import { useDBSLoginWebcomponentPlugin } from "@/composables/DBSLoginWebcomponentPlugin.ts";
import { QUERY_PARAM_CHECKLIST_ID, setAccessToken } from "@/util/Constants.ts";

const checklist = ref<ChecklistServiceNavigator | null>(null);
const loading = ref(true);
const loadingUpdate = ref(false);
const loadingCheck = ref(false);

const requestToDeleteItem = ref<ChecklistItemServiceNavigator | null>(null);
const openAcceptDeleteDialog = ref(false);

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
            resp.json().then((checklistResponse: ChecklistServiceNavigator) => {
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
  if (checklist.value && checklist.value.checklistItemServiceNavigatorDtos) {
    return checklist.value.checklistItemServiceNavigatorDtos.filter(
      (value) => !value.checked
    );
  } else {
    return [];
  }
});

const closedCheckList = computed(() => {
  if (checklist.value && checklist.value.checklistItemServiceNavigatorDtos) {
    return checklist.value.checklistItemServiceNavigatorDtos.filter(
      (value) => value.checked
    );
  } else {
    return [];
  }
});

const deletionDate = computed(() => {
  if (checklist.value && checklist.value.lastUpdate) {
    const date = new Date(checklist.value.lastUpdate);
    date.setFullYear(date.getFullYear() + 1);
    return date.toLocaleString("de-DE", {
      year: "numeric",
      month: "numeric",
      day: "numeric",
    });
  } else {
    return "nächsten Jahr";
  }
});

function onCheckedOpen(serviceID: string) {
  if (checklist.value) {
    loadingCheck.value = true;
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
      .finally(() => (loadingCheck.value = false));
  }
}

function onCheckedClosed(serviceID: string) {
  if (checklist.value) {
    loadingCheck.value = true;
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
      .finally(() => (loadingCheck.value = false));
  }
}

function onRequestDeleteItem(checklistItem: ChecklistItemServiceNavigator) {
  requestToDeleteItem.value = checklistItem;
  openAcceptDeleteDialog.value = true;
}

function deleteItem(checklistItem: ChecklistItemServiceNavigator) {
  openAcceptDeleteDialog.value = false;
  loadingUpdate.value = true;
  const indexOfItem = checklist.value?.checklistItemServiceNavigatorDtos.findIndex(
      (item) => {
        return item.serviceID === checklistItem.serviceID;
      }
  ) as number;
  if(checklist.value && indexOfItem > -1) {
    checklist.value.checklistItemServiceNavigatorDtos.splice(indexOfItem, 1);
    _updateChecklist(checklist.value);
  }
}

/**
 * When sort-event occurs, we need to find out the new order of elements.
 *
 * As we can only sort open items, we check the "distance" the item was sorted and move it accordingly in the checklist.checklistItems list.
 *
 * @param evt Sort-Event which contains the old and new index of the checklist-item.
 */
function onSortOpen(evt: { oldIndex: number; newIndex: number }) {
  const elementToSort = openCheckList.value[
    evt.oldIndex
  ] as ChecklistItemServiceNavigator;
  const oldIndex = checklist.value?.checklistItemServiceNavigatorDtos.findIndex(
    (item) => {
      return item.serviceID === elementToSort.serviceID;
    }
  ) as number;
  if (oldIndex >= 0 && checklist.value) {
    loadingUpdate.value = true;

    const newIndex = oldIndex + (evt.newIndex - evt.oldIndex);
    const element = checklist.value.checklistItemServiceNavigatorDtos[
      oldIndex
    ] as ChecklistItemServiceNavigator;
    checklist.value.checklistItemServiceNavigatorDtos.splice(oldIndex, 1);
    checklist.value.checklistItemServiceNavigatorDtos.splice(
      newIndex,
      0,
      element
    );

    _updateChecklist(checklist.value);
  }
}

function _updateChecklist(checklist: ChecklistServiceNavigator) {
  const updateChecklist = {} as Checklist;
  updateChecklist.id = checklist.id;
  updateChecklist.title = checklist.title;
  updateChecklist.lhmExtId = checklist.lhmExtId;
  updateChecklist.situationId = checklist.situationId;
  updateChecklist.checklistItems = checklist.checklistItemServiceNavigatorDtos;

  const service = new ChecklistService();
  service
      .updateChecklist(updateChecklist)
      .then((resp) => {
        if (resp.ok) {
          resp.json().then((newChecklist) => {
            checklist = newChecklist;
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
      .finally(() => (loadingUpdate.value = false));
}
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
@import "../public/checklist-styles.css";

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

main {
  padding: 40px 0;
}

@media (min-width: 576px) {
  main {
    padding: 56px 0;
  }
}
</style>
