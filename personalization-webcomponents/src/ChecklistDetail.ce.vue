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
      aria-live="polite"
    >
      <template #title> Löschen der Aufgabe</template>

      <template #body>
        <muc-banner
          noIcon
          type="warning"
          variant="content"
        >
          <p>
            Mit dieser Aktion entfernen Sie die Aufgabe
            <strong>{{ requestToDeleteItem.title }}</strong> endgültig aus Ihrer
            Checkliste.
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

    <muc-intro
      v-if="!loggedIn"
      title="Meine Checkliste"
      tagline="Checkliste"
      variant="detail"
    >
      <p style="margin-bottom: 8px">
        <strong>Sie sind nicht angemeldet.</strong>
      </p>
      <p style="margin-bottom: 32px">
        Um Ihre Checkliste einzusehen, melden Sie sich bei dem Konto an, das Sie
        für das Speichern der Liste genutzt haben.
      </p>
      <muc-button
        icon="sign-in"
        icon-animated
        @click="login"
      >
        Anmelden
      </muc-button>
    </muc-intro>
    <checklist-header
      v-else-if="checklist"
      :checklist="checklist"
      :checklist-overview-url="myChecklistsUrl"
    ></checklist-header>
    <muc-intro
      v-else
      title="Meine Checkliste"
      tagline="Checkliste"
      :divider="false"
      variant="detail"
    />
    <div class="m-component m-component-form">
      <div class="container">
        <div class="m-component__grid">
          <div class="m-component__column">
            <muc-callout
              v-if="noQueryParamError"
              type="info"
            >
              <template #header> Keine Checklisten-ID gefunden</template>
              <template #content>
                <p>
                  Bitte überprüfen Sie den Link über den Sie auf diese Seite
                  gelangt sind und stellen Sie sicher, dass Sie die vollständige
                  URL in die Adresszeile Ihres Browsers eingegeben haben.
                </p>
              </template>
            </muc-callout>

            <skeleton-loader v-else-if="loading && loggedIn" />

            <muc-callout
              v-else-if="loadingError && loggedIn"
              type="error"
            >
              <template #header>
                Ihre Checkliste konnte nicht geladen werden.
              </template>
              <template #content>
                <p>
                  Bitte überprüfen Sie, ob Sie den korrekten Link sowie das
                  richtige Konto für die Anmeldung genutzt haben.
                </p>
              </template>
              <template #buttons>
                <a :href="myChecklistsUrl">
                  <muc-button
                    icon="arrow-right"
                    icon-animated
                  >
                    Zurück zur Übersicht
                  </muc-button>
                </a>
              </template>
            </muc-callout>

            <div v-else-if="loggedIn">
              <h2 class="headline">
                Offene Aufgaben ({{ openCheckList.length }})
              </h2>
              <div v-if="openCheckList.length !== 0">
                <checklist-list
                  :checklist-items="openCheckList"
                  :disabled="loadingUpdate || loadingCheck"
                  @checked="onCheckedOpen"
                  @delete="onRequestDeleteItem"
                  @sort="onSortOpen"
                ></checklist-list>
              </div>
              <div
                v-else
                class="banner"
              >
                <muc-banner
                  type="success"
                  variant="content"
                  aria-live="polite"
                >
                  Herzlichen Glückwunsch, Sie haben alle Aufgaben erledigt! Wir
                  bewahren diese Checkliste noch bis zum {{ deletionDate }} für
                  Sie auf. Danach wird sie automatisch gelöscht.
                </muc-banner>
              </div>
              <h2 class="headline">
                Erledigte Aufgaben ({{ closedCheckList.length }})
              </h2>
              <div v-if="closedCheckList.length !== 0">
                <checklist-list
                  :disabled="loadingUpdate || loadingCheck"
                  :checklist-items="closedCheckList"
                  @checked="onCheckedClosed"
                  @delete="onRequestDeleteItem"
                  :is-draggable="false"
                ></checklist-list>
              </div>
              <div
                v-else
                class="banner"
              >
                <muc-banner
                  type="info"
                  variant="content"
                  aria-live="polite"
                >
                  Sie haben noch keine erledigten Aufgaben. Haken Sie Aufgaben
                  in der Checkliste ab, um sie als erledigt zu markieren.
                </muc-banner>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type {
  ChecklistItemServiceNavigatorDTO,
  ChecklistServiceNavigatorReadDTO,
  ChecklistUpdateDTO,
} from "@/api/dbs-clients/generated-p13n-service-api";
import type AuthorizationEventDetails from "@/types/AuthorizationEventDetails.ts";

import {
  MucBanner,
  MucButton,
  MucCallout,
  MucIntro,
  MucModal,
} from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { computed, ref } from "vue";

import { useChecklistsApi } from "@/api/compositions/UseChecklistsApi.ts";
import ChecklistHeader from "@/components/ChecklistHeader.vue";
import ChecklistList from "@/components/ChecklistList.vue";
import SkeletonLoader from "@/components/common/SkeletonLoader.vue";
import { useDBSLoginWebcomponentPlugin } from "@/composables/DBSLoginWebcomponentPlugin.ts";
import { QUERY_PARAM_CHECKLIST_ID, setAccessToken } from "@/util/Constants.ts";

defineProps<{
  myChecklistsUrl: string;
}>();

const checklist = ref<ChecklistServiceNavigatorReadDTO | null>(null);
const loading = ref(true);
const loadingUpdate = ref(false);
const loadingCheck = ref(false);
const loadingError = ref("");
const noQueryParamError = ref(false);

const requestToDeleteItem = ref<ChecklistItemServiceNavigatorDTO | null>(null);
const openAcceptDeleteDialog = ref(false);

const { loggedIn } = useDBSLoginWebcomponentPlugin(_authChangedCallback);

function _authChangedCallback(authEventDetails?: AuthorizationEventDetails) {
  if (authEventDetails && authEventDetails.accessToken) {
    setAccessToken(authEventDetails.accessToken);
    loadChecklists();
  }
}

async function loadChecklists() {
  if (loggedIn.value) {
    loadingError.value = "";
    loading.value = true;
    const checklistsApi = useChecklistsApi();

    const urlParams = new URLSearchParams(window.location.search);
    const checklistId = urlParams.get(QUERY_PARAM_CHECKLIST_ID);

    if (checklistId) {
      try {
        const checklistResponse = await checklistsApi.getChecklist({
          checklistID: checklistId,
        });
        checklist.value = checklistResponse;
      } catch (error) {
        loadingError.value = error as string;
        console.debug("Error loading checklist: ", error);
      } finally {
        loading.value = false;
      }
    } else {
      noQueryParamError.value = true;
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

function login() {
  document.dispatchEvent(
    new CustomEvent("authorization-request", {
      detail: {
        loginProvider: undefined,
        authLevel: undefined,
      },
    })
  );
}

async function onCheckedOpen(serviceID: string) {
  if (checklist.value && checklist.value.id) {
    loadingCheck.value = true;
    const checklistsApi = useChecklistsApi();

    try {
      checklist.value = await checklistsApi.checkChecklistEntry({
        checklistID: checklist.value.id,
        serviceID: serviceID,
      });
    } catch (error) {
      console.debug(error);
    } finally {
      loadingCheck.value = false;
    }
  }
}

async function onCheckedClosed(serviceID: string) {
  if (checklist.value && checklist.value.id) {
    loadingCheck.value = true;

    const checklistsApi = useChecklistsApi();

    try {
      checklist.value = await checklistsApi.uncheckChecklistEntry({
        checklistID: checklist.value.id,
        serviceID: serviceID,
      });
    } catch (error) {
      console.debug(error);
    } finally {
      loadingCheck.value = false;
    }
  }
}

function onRequestDeleteItem(checklistItem: ChecklistItemServiceNavigatorDTO) {
  requestToDeleteItem.value = checklistItem;
  openAcceptDeleteDialog.value = true;
}

function deleteItem(checklistItem: ChecklistItemServiceNavigatorDTO) {
  openAcceptDeleteDialog.value = false;
  loadingUpdate.value = true;
  const indexOfItem =
    checklist.value?.checklistItemServiceNavigatorDtos?.findIndex((item) => {
      return item.serviceID === checklistItem.serviceID;
    }) as number;
  if (checklist.value && indexOfItem > -1) {
    checklist.value.checklistItemServiceNavigatorDtos?.splice(indexOfItem, 1);
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
  ] as ChecklistItemServiceNavigatorDTO;
  const oldIndex =
    checklist.value?.checklistItemServiceNavigatorDtos?.findIndex((item) => {
      return item.serviceID === elementToSort.serviceID;
    }) as number;
  if (
    oldIndex >= 0 &&
    checklist.value &&
    checklist.value.checklistItemServiceNavigatorDtos
  ) {
    loadingUpdate.value = true;

    const newIndex = oldIndex + (evt.newIndex - evt.oldIndex);
    const element = checklist.value.checklistItemServiceNavigatorDtos[
      oldIndex
    ] as ChecklistServiceNavigatorReadDTO;
    checklist.value.checklistItemServiceNavigatorDtos.splice(oldIndex, 1);
    checklist.value.checklistItemServiceNavigatorDtos.splice(
      newIndex,
      0,
      element
    );

    _updateChecklist(checklist.value);
  }
}

async function _updateChecklist(checklist: ChecklistServiceNavigatorReadDTO) {
  if (
    !checklist.id ||
    !checklist.title ||
    !checklist.lhmExtId ||
    !checklist.checklistItemServiceNavigatorDtos
  ) {
    loadingUpdate.value = false;
    return;
  }

  const checklistApi = useChecklistsApi();
  const checklistUpdateDTO = {
    id: checklist.id,
    title: checklist.title,
    lhmExtId: checklist.lhmExtId,
    checklistItems: checklist.checklistItemServiceNavigatorDtos
  } as ChecklistUpdateDTO;

  try {
    await checklistApi.updateChecklist({
      checklistID: checklistUpdateDTO.id,
      checklistUpdateDTO: checklistUpdateDTO,
    });
  } catch (error) {
    console.debug(error);
  } finally {
    loadingUpdate.value = false;
  }
}
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.1.23/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
@import "../public/checklist-styles.css";

.banner {
  padding-bottom: 56px;
}

.headline {
  padding-bottom: 32px;
}
</style>
