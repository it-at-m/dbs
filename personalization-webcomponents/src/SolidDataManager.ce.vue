<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />

    <div class="m-component m-component-form">
      <div class="container">
        <muc-banner
          v-if="message"
          :type="messageType"
          class="message-banner"
        >
          {{ message }}
        </muc-banner>

        <div class="two-column-layout">
          <!-- Left Column: Form -->
          <div class="left-column">
            <form
              @submit.prevent="saveData"
              class="form-content"
            >
              <!-- Fieldset 1: Personal Information -->
              <fieldset class="form-fieldset">
                <legend>Persönliche Informationen</legend>
                
                <div class="m-form-group">
                  <label for="firstName" class="m-label">Vorname</label>
                  <input id="firstName" v-model="firstName" type="text" 
                         placeholder="Vorname eingeben" class="m-textfield" />
                </div>
                
                <div class="m-form-group">
                  <label for="lastName" class="m-label">Nachname</label>
                  <input id="lastName" v-model="lastName" type="text" 
                         placeholder="Nachname eingeben" class="m-textfield" />
                </div>
                
                <div class="m-form-group">
                  <label for="geburtsdatum" class="m-label">Geburtsdatum</label>
                  <input id="geburtsdatum" v-model="geburtsdatum" type="date" class="m-textfield" />
                </div>
              </fieldset>

              <!-- Fieldset 2: Location -->
              <fieldset class="form-fieldset">
                <legend>Wohnort</legend>
                
                <div class="m-form-group">
                  <label for="stadt" class="m-label">Stadt</label>
                  <input id="stadt" v-model="stadt" type="text" 
                         placeholder="z.B. München" class="m-textfield" />
                </div>
              </fieldset>

              <!-- Fieldset 3: Financial Information -->
              <fieldset class="form-fieldset">
                <legend>Finanzielle Angaben</legend>
                
                <div class="m-form-group">
                  <label for="nettoEinkommen" class="m-label">Monatliches Nettoeinkommen (€)</label>
                  <input id="nettoEinkommen" v-model.number="nettoEinkommenMonatlich" 
                         type="number" step="0.01" placeholder="z.B. 1500" class="m-textfield" />
                </div>
                
                <div class="m-form-group">
                  <label for="miete" class="m-label">Monatliche Miete (€)</label>
                  <input id="miete" v-model.number="mieteMietzinsMonatlich" 
                         type="number" step="0.01" placeholder="z.B. 800" class="m-textfield" />
                </div>
              </fieldset>

              <!-- Fieldset 4: Household Information -->
              <fieldset class="form-fieldset">
                <legend>Haushalt</legend>
                
                <div class="m-form-group">
                  <label for="haushalt" class="m-label">Anzahl Personen im Haushalt</label>
                  <input id="haushalt" v-model.number="anzahlPersonenHaushalt" 
                         type="number" min="1" placeholder="z.B. 2" class="m-textfield" />
                </div>
                
                <!-- Conditional field: only show if household > 1 -->
                <div v-if="anzahlPersonenHaushalt && anzahlPersonenHaushalt > 1" class="m-form-group">
                  <label for="kinder" class="m-label">Anzahl Kinder</label>
                  <input id="kinder" v-model.number="anzahlKinder" 
                         type="number" min="0" placeholder="z.B. 1" class="m-textfield" />
                </div>
              </fieldset>

              <!-- Fieldset 5: Status -->
              <fieldset class="form-fieldset">
                <legend>Status</legend>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="istStudent" class="m-checkbox" />
                    <span>Ich bin Student/in</span>
                  </label>
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="beziehtAlg2" class="m-checkbox" />
                    <span>Ich beziehe Bürgergeld (ALG II)</span>
                  </label>
                </div>
              </fieldset>

              <div class="button-group">
                <muc-button
                  type="submit"
                  icon="check"
                >
                  Speichern
                </muc-button>
                <muc-button
                  type="button"
                  variant="secondary"
                  icon="reload"
                  @click="loadData"
                >
                  Laden
                </muc-button>
                <muc-button
                  type="button"
                  variant="ghost"
                  icon="trash"
                  @click="clearData"
                >
                  Löschen
                </muc-button>
              </div>
            </form>
          </div>

          <!-- Right Column: Results -->
          <div class="right-column">
            <div
              v-if="eligibilityResults.length > 0"
              class="eligibility-results"
            >
              <h2 class="eligibility-title">
                Mögliche Leistungen ({{ eligibilityResults.length }})
              </h2>
              <p class="eligibility-subtitle">
                Basierend auf Ihren Angaben könnten folgende Leistungen für Sie
                in Frage kommen:
              </p>
              <div
                v-for="result in eligibilityResults"
                :key="result.subsidyName"
                class="eligibility-card"
              >
                <div class="eligibility-card-header">
                  <h3 class="eligibility-card-title">
                    {{ result.subsidyName }}
                  </h3>
                  <span class="eligibility-badge">Berechtigt</span>
                </div>
                <p
                  v-if="result.reason"
                  class="eligibility-card-reason"
                >
                  {{ result.reason }}
                </p>
                <div
                  v-if="result.url"
                  class="eligibility-card-actions"
                >
                  <a
                    :href="result.url"
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    <muc-button
                      icon="arrow-right"
                      icon-animated
                    >
                      Zum Antrag
                    </muc-button>
                  </a>
                </div>
              </div>
            </div>
            <div
              v-else-if="firstName || lastName"
              class="no-results-placeholder"
            >
              <muc-callout type="info">
                <template #header> Keine passenden Leistungen gefunden</template>
                <template #content>
                  <p>
                    Basierend auf Ihren aktuellen Angaben konnten wir keine
                    passenden Leistungen ermitteln. Bitte überprüfen Sie Ihre
                    Eingaben oder kontaktieren Sie uns für weitere Informationen.
                  </p>
                </template>
              </muc-callout>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  MucBanner,
  MucButton,
  MucCallout,
  MucIntro,
} from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";

import { EligibilityCheckRegistry } from "@/eligibility/EligibilityCheckRegistry";
import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

const LOCALSTORAGE_KEY_FORMDATA = "user.formData";

const firstName = ref("");
const lastName = ref("");
const geburtsdatum = ref("");
const stadt = ref("");
const nettoEinkommenMonatlich = ref<number | undefined>(undefined);
const mieteMietzinsMonatlich = ref<number | undefined>(undefined);
const anzahlPersonenHaushalt = ref<number | undefined>(undefined);
const anzahlKinder = ref<number | undefined>(undefined);
const istStudent = ref(false);
const beziehtAlg2 = ref(false);
const message = ref("");
const messageType = ref<"success" | "info" | "warning" | "emergency">("success");
const eligibilityResults = ref<EligibilityResult[]>([]);

const eligibilityRegistry = new EligibilityCheckRegistry();

onMounted(() => {
  loadData();
});

function saveData() {
  try {
    const formData: FormData = {
      firstName: firstName.value,
      lastName: lastName.value,
      geburtsdatum: geburtsdatum.value || undefined,
      stadt: stadt.value || undefined,
      nettoEinkommenMonatlich: nettoEinkommenMonatlich.value,
      mieteMietzinsMonatlich: mieteMietzinsMonatlich.value,
      anzahlPersonenHaushalt: anzahlPersonenHaushalt.value,
      anzahlKinder: anzahlKinder.value,
      istStudent: istStudent.value,
      beziehtAlg2: beziehtAlg2.value,
    };
    
    localStorage.setItem(LOCALSTORAGE_KEY_FORMDATA, JSON.stringify(formData));
    showMessage("Daten wurden erfolgreich gespeichert!", "success");
    checkEligibility();
  } catch (error) {
    showMessage("Fehler beim Speichern der Daten: " + error, "emergency");
  }
}

function loadData() {
  try {
    const savedData = localStorage.getItem(LOCALSTORAGE_KEY_FORMDATA);
    
    if (savedData) {
      const formData: FormData = JSON.parse(savedData);
      
      firstName.value = formData.firstName || "";
      lastName.value = formData.lastName || "";
      geburtsdatum.value = formData.geburtsdatum || "";
      stadt.value = formData.stadt || "";
      nettoEinkommenMonatlich.value = formData.nettoEinkommenMonatlich;
      mieteMietzinsMonatlich.value = formData.mieteMietzinsMonatlich;
      anzahlPersonenHaushalt.value = formData.anzahlPersonenHaushalt;
      anzahlKinder.value = formData.anzahlKinder;
      istStudent.value = formData.istStudent || false;
      beziehtAlg2.value = formData.beziehtAlg2 || false;
      
      showMessage("Daten wurden aus dem localStorage geladen", "success");
      checkEligibility();
    } else {
      showMessage("Keine gespeicherten Daten gefunden", "info");
    }
  } catch (error) {
    showMessage("Fehler beim Laden der Daten: " + error, "emergency");
  }
}

function clearData() {
  try {
    localStorage.removeItem(LOCALSTORAGE_KEY_FORMDATA);
    firstName.value = "";
    lastName.value = "";
    geburtsdatum.value = "";
    stadt.value = "";
    nettoEinkommenMonatlich.value = undefined;
    mieteMietzinsMonatlich.value = undefined;
    anzahlPersonenHaushalt.value = undefined;
    anzahlKinder.value = undefined;
    istStudent.value = false;
    beziehtAlg2.value = false;
    eligibilityResults.value = [];
    showMessage("Daten wurden erfolgreich gelöscht!", "success");
  } catch (error) {
    showMessage("Fehler beim Löschen der Daten: " + error, "emergency");
  }
}

function checkEligibility() {
  const formData: FormData = {
    firstName: firstName.value,
    lastName: lastName.value,
    geburtsdatum: geburtsdatum.value || undefined,
    stadt: stadt.value || undefined,
    nettoEinkommenMonatlich: nettoEinkommenMonatlich.value,
    mieteMietzinsMonatlich: mieteMietzinsMonatlich.value,
    anzahlPersonenHaushalt: anzahlPersonenHaushalt.value,
    anzahlKinder: anzahlKinder.value,
    istStudent: istStudent.value,
    beziehtAlg2: beziehtAlg2.value,
  };

  const checks = eligibilityRegistry.getAllChecks();
  eligibilityResults.value = checks
    .map((check) => check.evaluate(formData))
    .filter((result) => result.eligible);
}

function showMessage(msg: string, type: "success" | "info" | "warning" | "emergency") {
  message.value = msg;
  messageType.value = type;
  setTimeout(() => {
    message.value = "";
  }, 3000);
}
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.1.6/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
@import "../public/checklist-styles.css";
</style>

<style scoped>
.m-component-form {
  padding-top: 2rem;
}

.message-banner {
  margin-bottom: 32px;
}

.two-column-layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 32px;
  align-items: start;
}

.left-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.right-column {
  position: sticky;
  top: 24px;
  max-height: calc(100vh - 48px);
  overflow-y: auto;
}

.form-content {
  background: white;
  padding: 0;
}

.m-form-group {
  margin-bottom: 24px;
}

.m-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: var(--mde-color-neutral-grey-dark);
  font-size: 1rem;
}

.m-textfield {
  width: 100%;
  padding: 12px 16px;
  font-size: 1rem;
  border: 2px solid var(--mde-color-neutral-grey-light);
  border-radius: 4px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
  font-family: "Open Sans", sans-serif;
}

.m-textfield:focus {
  outline: none;
  border-color: var(--mde-color-brand-mde-blue);
}

.m-textfield:hover {
  border-color: var(--mde-color-brand-mde-blue-light);
}

.button-group {
  display: flex;
  gap: 16px;
  margin-top: 32px;
  flex-wrap: wrap;
}

.data-display {
  padding: 24px;
  background: var(--mde-color-neutral-beau-blue-x-light);
  border-radius: 4px;
}

.data-display-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--mde-color-neutral-grey-dark);
  margin-bottom: 16px;
}

.data-display-content p {
  margin: 8px 0;
  color: var(--mde-color-neutral-grey-dark);
  font-size: 1rem;
  line-height: 1.5;
}

.eligibility-results {
  background: var(--mde-color-neutral-beau-blue-x-light);
  padding: 24px;
  border-radius: 4px;
}

.eligibility-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--mde-color-neutral-grey-dark);
  margin-bottom: 16px;
  margin-top: 0;
}

.eligibility-subtitle {
  font-size: 1rem;
  color: var(--mde-color-neutral-grey);
  margin-bottom: 24px;
  line-height: 1.5;
}

.eligibility-card {
  background: white;
  border: 2px solid var(--mde-color-neutral-beau-blue);
  border-radius: 4px;
  padding: 24px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.eligibility-card:last-child {
  margin-bottom: 0;
}

.eligibility-card:hover {
  border-color: var(--mde-color-brand-mde-blue);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.eligibility-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.eligibility-card-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--mde-color-brand-mde-blue-dark);
  margin: 0;
}

.eligibility-badge {
  display: inline-block;
  padding: 6px 16px;
  background-color: var(--mde-color-signal-success);
  color: white;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 600;
}

.eligibility-card-reason {
  color: var(--mde-color-neutral-grey-dark);
  font-size: 1rem;
  line-height: 1.5;
  margin-bottom: 20px;
}

.eligibility-card-actions {
  margin-top: 16px;
}

.eligibility-card-actions a {
  text-decoration: none;
}

.no-results-placeholder {
  background: var(--mde-color-neutral-beau-blue-x-light);
  padding: 24px;
  border-radius: 4px;
}

/* Desktop: Two columns side by side */
@media (min-width: 992px) {
  .two-column-layout {
    grid-template-columns: 1fr 1fr;
    gap: 48px;
  }
}

/* Tablet and mobile: Stack vertically */
@media (max-width: 991px) {
  .right-column {
    position: static;
    max-height: none;
    overflow-y: visible;
  }
}

@media (max-width: 768px) {
  .button-group {
    flex-direction: column;
  }
  
  .button-group button {
    width: 100%;
  }
  
  .two-column-layout {
    gap: 32px;
  }
}

.form-fieldset {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 24px;
}

.form-fieldset legend {
  font-weight: 600;
  font-size: 1.1rem;
  padding: 0 8px;
  color: #333;
}

.m-form-group-checkbox {
  display: flex;
  align-items: center;
}

.m-checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.m-checkbox {
  margin-right: 8px;
  cursor: pointer;
}
</style>
