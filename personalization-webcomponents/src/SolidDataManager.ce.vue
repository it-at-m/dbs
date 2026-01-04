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
            <form class="form-content">
              <template
                v-for="section in visibleSections"
                :key="section"
              >
                <personal-information-form
                  v-if="section === 'personalInfo'"
                  v-model:firstName="formFields.firstName"
                  v-model:lastName="formFields.lastName"
                  v-model:dateOfBirth="formFields.dateOfBirth"
                  v-model:gender="formFields.gender"
                  v-model:maritalStatus="formFields.maritalStatus"
                  v-model:nationality="formFields.nationality"
                  v-model:residenceStatus="formFields.residenceStatus"
                  v-model:residenceInGermany="formFields.residenceInGermany"
                  :shouldShowField="shouldShowField"
                />

                <financial-information-form
                  v-if="section === 'financialInfo'"
                  v-model:grossMonthlyIncome="formFields.grossMonthlyIncome"
                  v-model:netMonthlyIncome="formFields.netMonthlyIncome"
                  v-model:assets="formFields.assets"
                  v-model:monthlyRent="formFields.monthlyRent"
                  :shouldShowField="shouldShowField"
                />

                <household-information-form
                  v-if="section === 'householdInfo'"
                  v-model:householdSize="formFields.householdSize"
                  v-model:numberOfChildren="formFields.numberOfChildren"
                  v-model:childrenAges="formFields.childrenAges"
                  v-model:isSingleParent="formFields.isSingleParent"
                  v-model:livesWithParents="formFields.livesWithParents"
                  :shouldShowField="shouldShowField"
                />

                <education-employment-form
                  v-if="section === 'educationEmployment'"
                  v-model:employmentStatus="formFields.employmentStatus"
                  v-model:educationLevel="formFields.educationLevel"
                  v-model:isStudent="formFields.isStudent"
                  :shouldShowField="shouldShowField"
                />

                <special-circumstances-form
                  v-if="section === 'specialCircumstances'"
                  v-model:hasDisability="formFields.hasDisability"
                  v-model:disabilityDegree="formFields.disabilityDegree"
                  v-model:isPregnant="formFields.isPregnant"
                  v-model:hasCareNeeds="formFields.hasCareNeeds"
                  v-model:pensionEligible="formFields.pensionEligible"
                  v-model:citizenBenefitLast3Years="
                    formFields.citizenBenefitLast3Years
                  "
                  v-model:hasFinancialHardship="formFields.hasFinancialHardship"
                  v-model:workAbility="formFields.workAbility"
                  :shouldShowField="shouldShowField"
                />

                <insurance-benefits-form
                  v-if="section === 'insuranceBenefits'"
                  v-model:healthInsurance="formFields.healthInsurance"
                  v-model:hasCareInsurance="formFields.hasCareInsurance"
                  v-model:receivesUnemploymentBenefit1="
                    formFields.receivesUnemploymentBenefit1
                  "
                  v-model:receivesUnemploymentBenefit2="
                    formFields.receivesUnemploymentBenefit2
                  "
                  v-model:receivesPension="formFields.receivesPension"
                  v-model:receivesChildBenefit="formFields.receivesChildBenefit"
                  v-model:receivesHousingBenefit="
                    formFields.receivesHousingBenefit
                  "
                  v-model:receivesStudentAid="formFields.receivesStudentAid"
                  :shouldShowField="shouldShowField"
                />
              </template>
            </form>
          </div>

          <!-- Right Column: Results -->
          <div class="right-column">
            <!-- Solid Pod Integration Hub -->
            <div
              class="solid-sidebar-section"
              style="
                margin-bottom: 24px;
                padding: 24px;
                background: var(--mde-color-neutral-beau-blue-x-light);
                border-radius: 4px;
              "
            >
              <h2
                style="
                  font-size: 1.25rem;
                  font-weight: 700;
                  margin-bottom: 16px;
                  color: var(--mde-color-neutral-grey-dark);
                "
              >
                Solid Pod
              </h2>

              <div v-if="!isSolidConnected">
                <div
                  class="m-form-group"
                  style="margin-bottom: 16px"
                >
                  <label class="m-label">Provider URL</label>
                  <input
                    v-model="solidIssuer"
                    type="text"
                    class="m-textfield"
                    placeholder="https://solidcommunity.net"
                    style="width: 100%"
                  />
                </div>
                <muc-button
                  @click="connectToSolid"
                  :disabled="solidLoading"
                  icon="login"
                  style="width: 100%"
                >
                  Anmelden
                </muc-button>
              </div>

              <div
                v-else
                style="display: flex; flex-direction: column; gap: 12px"
              >
                <div
                  style="
                    font-size: 0.85em;
                    color: var(--mde-color-neutral-grey-dark);
                    background: rgba(255, 255, 255, 0.5);
                    padding: 8px;
                    border-radius: 4px;
                    display: flex;
                    justify-content: space-between;
                    align-items: flex-start;
                    gap: 8px;
                  "
                >
                  <div style="word-break: break-all">
                    <span style="font-weight: 600">Verbunden als:</span><br />
                    {{ solidWebId }}
                  </div>
                  <muc-button
                    variant="ghost"
                    @click="disconnectSolid"
                    :loading="solidLoading"
                    icon="logout"
                    size="small"
                    style="flex-shrink: 0"
                  >
                    Abmelden
                  </muc-button>
                </div>

                <div style="display: flex; gap: 8px">
                  <muc-button
                    variant="primary"
                    @click="saveToPod"
                    :loading="solidLoading"
                    icon="upload"
                    style="flex: 1"
                  >
                    Speichern
                  </muc-button>
                </div>
              </div>
            </div>

            <div
              v-if="
                eligibilityResults.length > 0 ||
                allEligibilityResults.length > 0
              "
              class="eligibility-results"
            >
              <div class="eligibility-header">
                <div>
                  <h2 class="eligibility-title">
                    Mögliche Leistungen ({{ eligibilityResults.length }})
                  </h2>
                  <p class="eligibility-subtitle">
                    Basierend auf Ihren Angaben könnten folgende Leistungen für
                    Sie in Frage kommen:
                  </p>
                </div>
                <muc-button
                  type="button"
                  variant="ghost"
                  @click="showAllResults = !showAllResults"
                  class="toggle-all-button"
                >
                  {{
                    showAllResults
                      ? "Nur berechtigte anzeigen"
                      : "Alle Leistungen anzeigen"
                  }}
                </muc-button>
              </div>

              <div
                v-for="result in showAllResults
                  ? allEligibilityResults
                  : eligibilityResults"
                :key="result.subsidyName"
                :class="[
                  'eligibility-card',
                  { 'not-eligible': !result.eligible },
                ]"
              >
                <div class="eligibility-card-header">
                  <h3 class="eligibility-card-title">
                    {{ result.subsidyName }}
                  </h3>
                  <span
                    :class="[
                      'eligibility-badge',
                      result.eligible ? 'eligible' : 'not-eligible',
                    ]"
                  >
                    {{ result.eligible ? "Berechtigt" : "Nicht berechtigt" }}
                  </span>
                </div>
                <p
                  v-if="result.reason"
                  class="eligibility-card-reason"
                >
                  {{ result.reason }}
                </p>
                <div
                  v-if="result.url && result.eligible"
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
              v-else-if="formFields.firstName || formFields.lastName"
              class="no-results-placeholder"
            >
              <muc-callout type="info">
                <template #header> Keine Daten zur Prüfung vorhanden</template>
                <template #content>
                  <p>
                    Bitte füllen Sie das Formular aus und klicken Sie auf
                    "Speichern", um Ihre Berechtigung zu prüfen.
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
import type {
  FormSection,
  PrefilledFields,
} from "@/eligibility/EligibilityCheckRegistry";
import type {
  EligibilityResult,
  FormData,
  FormDataField,
} from "@/types/EligibilityCheckInterface";

import { getFile, overwriteFile } from "@inrupt/solid-client";
// Solid Imports
import {
  getDefaultSession,
  handleIncomingRedirect,
  login,
  logout,
  fetch as solidFetch,
} from "@inrupt/solid-client-authn-browser";
import {
  MucBanner,
  MucButton,
  MucCallout,
  MucIntro,
} from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { computed, onMounted, ref, watch } from "vue";

import EducationEmploymentForm from "@/components/forms/education-employment-form.vue";
import FinancialInformationForm from "@/components/forms/financial-information-form.vue";
import HouseholdInformationForm from "@/components/forms/household-information-form.vue";
import InsuranceBenefitsForm from "@/components/forms/insurance-benefits-form.vue";
// Import form components
import PersonalInformationForm from "@/components/forms/personal-information-form.vue";
import SpecialCircumstancesForm from "@/components/forms/special-circumstances-form.vue";
import { EligibilityCheckRegistry } from "@/eligibility/EligibilityCheckRegistry";

const SOLID_DATA_FILE = "private/personalization/eligibility-data.json";

// Solid State
const solidIssuer = ref("https://solidcommunity.net");
const solidSession = getDefaultSession();
const isSolidConnected = ref(false);
const solidWebId = ref<string | undefined>(undefined);
const solidLoading = ref(false);
const solidPodData = ref<FormData | undefined>(undefined);

async function connectToSolid() {
  solidLoading.value = true;
  try {
    await login({
      oidcIssuer: solidIssuer.value,
      redirectUrl: window.location.href,
      clientName: "Solid Data Manager",
    });
  } catch (err) {
    showMessage("Fehler bei der Solid-Anmeldung: " + err, "emergency");
  } finally {
    solidLoading.value = false;
  }
}

async function disconnectSolid() {
  solidLoading.value = true;
  try {
    await logout();
    isSolidConnected.value = false;
    solidWebId.value = undefined;
    showMessage("Erfolgreich abgemeldet", "info");
  } catch (err) {
    showMessage("Fehler beim Abmelden: " + err, "emergency");
  } finally {
    solidLoading.value = false;
  }
}

function getPodRoot(webId: string): string {
  // Simple heuristic: assume Pod root is the origin
  // In a production app, we would query the profile for ws:storage
  return new URL(webId).origin + "/";
}

async function saveToPod() {
  if (!solidWebId.value) return;

  solidLoading.value = true;
  try {
    const podRoot = getPodRoot(solidWebId.value);
    const fileUrl = `${podRoot}${SOLID_DATA_FILE}`;

    const blob = new Blob([JSON.stringify(formFields.value, null, 2)], {
      type: "application/json",
    });

    await overwriteFile(fileUrl, blob, { fetch: solidFetch });

    showMessage("Daten erfolgreich im Solid Pod gespeichert!", "success");
  } catch (err) {
    showMessage("Fehler beim Speichern im Pod: " + err, "emergency");
  } finally {
    solidLoading.value = false;
  }
}

async function loadFromPod() {
  if (!solidWebId.value) return;

  solidLoading.value = true;
  try {
    const podRoot = getPodRoot(solidWebId.value);
    const fileUrl = `${podRoot}${SOLID_DATA_FILE}`;

    const file = await getFile(fileUrl, { fetch: solidFetch });
    const text = await file.text();
    const loadedData: FormData = JSON.parse(text);

    solidPodData.value = loadedData;

    showMessage("Daten erfolgreich aus dem Solid Pod geladen!", "success");
  } catch (err) {
    showMessage(
      "Fehler beim Laden aus dem Pod (Existiert die Datei?): " + err,
      "warning"
    );
  } finally {
    solidLoading.value = false;
  }
}

const formFields = ref<FormData>({});

const message = ref("");
const messageType = ref<"success" | "info" | "warning" | "emergency">(
  "success"
);
const eligibilityResults = ref<EligibilityResult[]>([]);
const allEligibilityResults = ref<EligibilityResult[]>([]);
const visibleFields = ref<FormDataField[]>([]);
const showAllResults = ref(false);
const prefilledFields = ref<PrefilledFields>({});
const eligibilityRegistry = new EligibilityCheckRegistry();

const isCheckingEligibility = ref(false);

const visibleSections = ref<FormSection[]>();

watch(
  formFields,
  () => {
    checkEligibility();
  },
  { deep: true }
);

const shouldShowField = (fieldName: FormDataField): boolean => {
  return visibleFields.value.includes(fieldName);
};

onMounted(async () => {
  // Handle Solid Redirect
  try {
    await handleIncomingRedirect({
      restorePreviousSession: true,
    });
    isSolidConnected.value = solidSession.info.isLoggedIn;
    solidWebId.value = solidSession.info.webId;
    await loadFromPod();
    checkEligibility();
  } catch (err) {
    console.error("Solid Redirect handling error:", err);
  }
});

function checkEligibility() {
  // Use the registry to evaluate all checks
  if (isCheckingEligibility.value) {
    return;
  }
  isCheckingEligibility.value = true;

  console.log("solid", solidPodData.value);
  const result = eligibilityRegistry.refreshEligibilityForm(
    formFields.value,
    solidPodData.value
  );

  const prefillData = result.prefilledFields;
  console.log("prefill", result.prefilledFields);

  formFields.value = { ...formFields.value, ...prefillData };
  console.log("form", formFields.value);

  visibleSections.value = result.visibleSections;
  prefilledFields.value = result.prefilledFields;
  allEligibilityResults.value = result.all;
  eligibilityResults.value = result.eligible;
  visibleFields.value = result.visibleFields;
  console.log("form", visibleFields.value);

  setTimeout(() => {
    isCheckingEligibility.value = false;
  }, 0);
}

function showMessage(
  msg: string,
  type: "success" | "info" | "warning" | "emergency"
) {
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

/* Form styles - unscoped so they apply to child components */
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

.eligibility-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.toggle-all-button {
  flex-shrink: 0;
  white-space: nowrap;
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
  margin-bottom: 0;
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

.eligibility-card.not-eligible {
  border-color: #e0e0e0;
  opacity: 0.85;
}

.eligibility-card:last-child {
  margin-bottom: 0;
}

.eligibility-card:hover {
  border-color: var(--mde-color-brand-mde-blue);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.eligibility-card.not-eligible:hover {
  border-color: #c0c0c0;
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

.eligibility-badge.not-eligible {
  background-color: var(--mde-color-neutral-grey);
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
</style>
