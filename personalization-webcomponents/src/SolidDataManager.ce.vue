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
              <template
                v-for="section in visibleSections.visibleSections"
                :key="section"
              >
                <personal-information-form
                  v-if="section === 'personalInfo'"
                  v-model:firstName="firstName"
                  v-model:lastName="lastName"
                  v-model:dateOfBirth="dateOfBirth"
                  v-model:gender="gender"
                  v-model:maritalStatus="maritalStatus"
                  v-model:nationality="nationality"
                  v-model:residenceStatus="residenceStatus"
                  v-model:residenceInGermany="residenceInGermany"
                  :shouldShowField="shouldShowField"
                />

                <financial-information-form
                  v-if="section === 'financialInfo'"
                  v-model:grossMonthlyIncome="grossMonthlyIncome"
                  v-model:netMonthlyIncome="netMonthlyIncome"
                  v-model:assets="assets"
                  v-model:monthlyRent="monthlyRent"
                  :shouldShowField="shouldShowField"
                />

                <household-information-form
                  v-if="section === 'householdInfo'"
                  v-model:householdSize="householdSize"
                  v-model:numberOfChildren="numberOfChildren"
                  v-model:childrenAges="childrenAges"
                  v-model:isSingleParent="isSingleParent"
                  :shouldShowField="shouldShowField"
                />

                <education-employment-form
                  v-if="section === 'educationEmployment'"
                  v-model:employmentStatus="employmentStatus"
                  v-model:educationLevel="educationLevel"
                  v-model:isStudent="isStudent"
                  :shouldShowField="shouldShowField"
                />

                <special-circumstances-form
                  v-if="section === 'specialCircumstances'"
                  v-model:hasDisability="hasDisability"
                  v-model:disabilityDegree="disabilityDegree"
                  v-model:isPregnant="isPregnant"
                  v-model:hasCareNeeds="hasCareNeeds"
                  v-model:pensionEligible="pensionEligible"
                  v-model:citizenBenefitLast3Years="citizenBenefitLast3Years"
                  v-model:hasFinancialHardship="hasFinancialHardship"
                  v-model:workAbility="workAbility"
                  :shouldShowField="shouldShowField"
                />

                <insurance-benefits-form
                  v-if="section === 'insuranceBenefits'"
                  v-model:healthInsurance="healthInsurance"
                  v-model:hasCareInsurance="hasCareInsurance"
                  v-model:receivesUnemploymentBenefit1="
                    receivesUnemploymentBenefit1
                  "
                  v-model:receivesUnemploymentBenefit2="
                    receivesUnemploymentBenefit2
                  "
                  v-model:receivesPension="receivesPension"
                  v-model:receivesChildBenefit="receivesChildBenefit"
                  v-model:receivesHousingBenefit="receivesHousingBenefit"
                  v-model:receivesStudentAid="receivesStudentAid"
                  :shouldShowField="shouldShowField"
                />
              </template>

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
                    variant="secondary"
                    @click="loadFromPod"
                    :loading="solidLoading"
                    icon="download"
                    style="flex: 1"
                  >
                    Laden
                  </muc-button>
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
              v-else-if="firstName || lastName"
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

const LOCALSTORAGE_KEY_FORMDATA = "user.formData";
const SOLID_DATA_FILE = "private/personalization/eligibility-data.json";

// Solid State
const solidIssuer = ref("https://solidcommunity.net");
const solidSession = getDefaultSession();
const isSolidConnected = ref(false);
const solidWebId = ref<string | undefined>(undefined);
const solidLoading = ref(false);

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
    const formData: FormData = {
      // Personal Information
      firstName: firstName.value,
      lastName: lastName.value,
      dateOfBirth: dateOfBirth.value,
      age: calculatedAge.value, // Use calculated age
      gender: gender.value,
      maritalStatus: maritalStatus.value,
      nationality: nationality.value,
      residenceStatus: residenceStatus.value,
      residenceInGermany: residenceInGermany.value,

      // Financial Information
      grossMonthlyIncome: grossMonthlyIncome.value,
      netMonthlyIncome: netMonthlyIncome.value,
      assets: assets.value,
      monthlyRent: monthlyRent.value,

      // Household Information
      householdSize: householdSize.value,
      numberOfChildren: numberOfChildren.value,
      childrenAges: childrenAges.value,

      // Education & Employment
      employmentStatus: employmentStatus.value,
      educationLevel: educationLevel.value,
      isStudent: isStudent.value,

      // Special Circumstances
      hasDisability: hasDisability.value,
      disabilityDegree: disabilityDegree.value,
      receivesUnemploymentBenefit1: receivesUnemploymentBenefit1.value,
      receivesUnemploymentBenefit2: receivesUnemploymentBenefit2.value,
      receivesPension: receivesPension.value,
      pensionEligible: pensionEligible.value,
      isPregnant: isPregnant.value,
      isSingleParent: isSingleParent.value,
      hasCareNeeds: hasCareNeeds.value,
      citizenBenefitLast3Years: citizenBenefitLast3Years.value,
      hasFinancialHardship: hasFinancialHardship.value,
      workAbility: workAbility.value,

      // Insurance & Benefits
      healthInsurance: healthInsurance.value,
      hasCareInsurance: hasCareInsurance.value,
      receivesChildBenefit: receivesChildBenefit.value,
      receivesHousingBenefit: receivesHousingBenefit.value,
      receivesStudentAid: receivesStudentAid.value,
    };

    const podRoot = getPodRoot(solidWebId.value);
    const fileUrl = `${podRoot}${SOLID_DATA_FILE}`;

    const blob = new Blob([JSON.stringify(formData, null, 2)], {
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
    const formData: FormData = JSON.parse(text);

    // map to refs
    // Personal Information
    firstName.value = formData.firstName;
    lastName.value = formData.lastName;
    dateOfBirth.value = formData.dateOfBirth;
    age.value = formData.age;
    gender.value = formData.gender;
    maritalStatus.value = formData.maritalStatus;
    nationality.value = formData.nationality;
    residenceStatus.value = formData.residenceStatus;
    residenceInGermany.value = formData.residenceInGermany;

    // Financial Information
    grossMonthlyIncome.value = formData.grossMonthlyIncome;
    netMonthlyIncome.value = formData.netMonthlyIncome;
    assets.value = formData.assets;
    monthlyRent.value = formData.monthlyRent;

    // Household Information
    householdSize.value = formData.householdSize;
    numberOfChildren.value = formData.numberOfChildren;
    childrenAges.value = formData.childrenAges;

    // Education & Employment
    employmentStatus.value = formData.employmentStatus;
    educationLevel.value = formData.educationLevel;
    isStudent.value = formData.isStudent;

    // Special Circumstances
    hasDisability.value = formData.hasDisability;
    disabilityDegree.value = formData.disabilityDegree;
    receivesUnemploymentBenefit1.value = formData.receivesUnemploymentBenefit1;
    receivesUnemploymentBenefit2.value = formData.receivesUnemploymentBenefit2;
    receivesPension.value = formData.receivesPension;
    pensionEligible.value = formData.pensionEligible;
    isPregnant.value = formData.isPregnant;
    isSingleParent.value = formData.isSingleParent;
    hasCareNeeds.value = formData.hasCareNeeds;
    citizenBenefitLast3Years.value = formData.citizenBenefitLast3Years;
    hasFinancialHardship.value = formData.hasFinancialHardship;
    workAbility.value = formData.workAbility;

    // Insurance & Benefits
    healthInsurance.value = formData.healthInsurance;
    hasCareInsurance.value = formData.hasCareInsurance;
    receivesChildBenefit.value = formData.receivesChildBenefit;
    receivesHousingBenefit.value = formData.receivesHousingBenefit;
    receivesStudentAid.value = formData.receivesStudentAid;

    showMessage("Daten erfolgreich aus dem Solid Pod geladen!", "success");
    checkEligibility();
  } catch (err) {
    showMessage(
      "Fehler beim Laden aus dem Pod (Existiert die Datei?): " + err,
      "warning"
    );
  } finally {
    solidLoading.value = false;
  }
}

// Personal Information
const firstName = ref<string | undefined>(undefined);
const lastName = ref<string | undefined>(undefined);
const dateOfBirth = ref<string | undefined>(undefined);
const age = ref<number | undefined>(undefined);
const gender = ref<
  "male" | "female" | "diverse" | "unspecified" | undefined
>(undefined);
const maritalStatus = ref<
  "single" | "married" | "divorced" | "widowed" | "separated" | undefined
>(undefined);
const nationality = ref<"German" | "EU" | "Non-EU" | undefined>(undefined);
const residenceStatus = ref<
  "residence_permit" | "permanent_residence" | "none" | undefined
>(undefined);
const residenceInGermany = ref<boolean | undefined>(undefined);

// Financial Information
const grossMonthlyIncome = ref<number | undefined>(undefined);
const netMonthlyIncome = ref<number | undefined>(undefined);
const assets = ref<number | undefined>(undefined);
const monthlyRent = ref<number | undefined>(undefined);

// Household Information
const householdSize = ref<number | undefined>(undefined);
const numberOfChildren = ref<number | undefined>(undefined);
const childrenAges = ref<number[] | undefined>(undefined);

// Education & Employment
const employmentStatus = ref<
  | "employed"
  | "self_employed"
  | "unemployed"
  | "student"
  | "retired"
  | "other"
  | undefined
>(undefined);
const educationLevel = ref<
  | "no_degree"
  | "lower_secondary"
  | "secondary"
  | "high_school"
  | "vocational_training"
  | "university"
  | undefined
>(undefined);
const isStudent = ref<boolean | undefined>(undefined);

// Special Circumstances
const hasDisability = ref<boolean | undefined>(undefined);
const disabilityDegree = ref<number | undefined>(undefined);
const receivesUnemploymentBenefit1 = ref<boolean | undefined>(undefined);
const receivesUnemploymentBenefit2 = ref<boolean | undefined>(undefined);
const receivesPension = ref<boolean | undefined>(undefined);
const pensionEligible = ref<boolean | undefined>(undefined);
const isPregnant = ref<boolean | undefined>(undefined);
const isSingleParent = ref<boolean | undefined>(undefined);
const hasCareNeeds = ref<boolean | undefined>(undefined);
const citizenBenefitLast3Years = ref<boolean | undefined>(undefined);
const hasFinancialHardship = ref<boolean | undefined>(undefined);
const workAbility = ref<"full" | "limited" | "none" | undefined>(
  undefined
);

// Insurance & Benefits
const healthInsurance = ref<"public" | "private" | "none" | undefined>(
  undefined
);
const hasCareInsurance = ref<boolean | undefined>(undefined);
const receivesChildBenefit = ref<boolean | undefined>(undefined);
const receivesHousingBenefit = ref<boolean | undefined>(undefined);
const receivesStudentAid = ref<boolean | undefined>(undefined);

const message = ref("");
const messageType = ref<"success" | "info" | "warning" | "emergency">(
  "success"
);
const eligibilityResults = ref<EligibilityResult[]>([]);
const allEligibilityResults = ref<EligibilityResult[]>([]);
const visibleFields = ref<FormDataField[]>([]);
const showAllResults = ref(false);

const eligibilityRegistry = new EligibilityCheckRegistry();

// Get current form data as a computed value
const currentFormData = computed(
  (): FormData => ({
    // Personal Information
    firstName: firstName.value,
    lastName: lastName.value,
    dateOfBirth: dateOfBirth.value,
    age: calculatedAge.value,
    gender: gender.value,
    maritalStatus: maritalStatus.value,
    nationality: nationality.value,
    residenceStatus: residenceStatus.value,
    residenceInGermany: residenceInGermany.value,
    // Financial Information
    grossMonthlyIncome: grossMonthlyIncome.value,
    netMonthlyIncome: netMonthlyIncome.value,
    assets: assets.value,
    monthlyRent: monthlyRent.value,
    // Household Information
    householdSize: householdSize.value,
    numberOfChildren: numberOfChildren.value,
    childrenAges: childrenAges.value,
    isSingleParent: isSingleParent.value,
    // Education & Employment
    employmentStatus: employmentStatus.value,
    educationLevel: educationLevel.value,
    isStudent: isStudent.value,
    // Special Circumstances
    hasDisability: hasDisability.value,
    disabilityDegree: disabilityDegree.value,
    receivesUnemploymentBenefit1: receivesUnemploymentBenefit1.value,
    receivesUnemploymentBenefit2: receivesUnemploymentBenefit2.value,
    receivesPension: receivesPension.value,
    pensionEligible: pensionEligible.value,
    isPregnant: isPregnant.value,
    hasCareNeeds: hasCareNeeds.value,
    citizenBenefitLast3Years: citizenBenefitLast3Years.value,
    hasFinancialHardship: hasFinancialHardship.value,
    workAbility: workAbility.value,
    // Insurance & Benefits
    healthInsurance: healthInsurance.value,
    hasCareInsurance: hasCareInsurance.value,
    receivesChildBenefit: receivesChildBenefit.value,
    receivesHousingBenefit: receivesHousingBenefit.value,
    receivesStudentAid: receivesStudentAid.value,
  })
);

// Get visible sections from the registry
const visibleSections = computed(() =>
  eligibilityRegistry.getVisibleSections(currentFormData.value)
);

// Watch all form fields and automatically check eligibility when they change
watch(
  [
    // Personal Information
    firstName,
    lastName,
    dateOfBirth,
    gender,
    maritalStatus,
    nationality,
    residenceStatus,
    residenceInGermany,
    // Financial Information
    grossMonthlyIncome,
    netMonthlyIncome,
    assets,
    monthlyRent,
    // Household Information
    householdSize,
    numberOfChildren,
    childrenAges,
    // Education & Employment
    employmentStatus,
    educationLevel,
    isStudent,
    // Special Circumstances
    hasDisability,
    disabilityDegree,
    receivesUnemploymentBenefit1,
    receivesUnemploymentBenefit2,
    receivesPension,
    pensionEligible,
    isPregnant,
    isSingleParent,
    hasCareNeeds,
    citizenBenefitLast3Years,
    hasFinancialHardship,
    workAbility,
    // Insurance & Benefits
    healthInsurance,
    hasCareInsurance,
    receivesChildBenefit,
    receivesHousingBenefit,
    receivesStudentAid,
  ],
  () => {
    checkEligibility();
  },
  { deep: true }
);

// Helper function to calculate age from birth date
function calculateAge(birthDateString: string | undefined): number | undefined {
  if (!birthDateString) return undefined;

  const birthDate = new Date(birthDateString);
  const today = new Date();

  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  // If birthday hasn't occurred this year yet, subtract 1
  if (
    monthDiff < 0 ||
    (monthDiff === 0 && today.getDate() < birthDate.getDate())
  ) {
    age--;
  }

  return age;
}

// Computed property for age based on birth date
const calculatedAge = computed(() => calculateAge(dateOfBirth.value));

// Helper function to check if a field should be shown
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
  } catch (err) {
    console.error("Solid Redirect handling error:", err);
  }

  loadData();
});

function saveData() {
  try {
    const formData: FormData = {
      // Personal Information
      firstName: firstName.value,
      lastName: lastName.value,
      dateOfBirth: dateOfBirth.value,
      age: calculatedAge.value, // Use calculated age
      gender: gender.value,
      maritalStatus: maritalStatus.value,
      nationality: nationality.value,
      residenceStatus: residenceStatus.value,
      residenceInGermany: residenceInGermany.value,

      // Financial Information
      grossMonthlyIncome: grossMonthlyIncome.value,
      netMonthlyIncome: netMonthlyIncome.value,
      assets: assets.value,
      monthlyRent: monthlyRent.value,

      // Household Information
      householdSize: householdSize.value,
      numberOfChildren: numberOfChildren.value,
      childrenAges: childrenAges.value,

      // Education & Employment
      employmentStatus: employmentStatus.value,
      educationLevel: educationLevel.value,
      isStudent: isStudent.value,

      // Special Circumstances
      hasDisability: hasDisability.value,
      disabilityDegree: disabilityDegree.value,
      receivesUnemploymentBenefit1: receivesUnemploymentBenefit1.value,
      receivesUnemploymentBenefit2: receivesUnemploymentBenefit2.value,
      receivesPension: receivesPension.value,
      pensionEligible: pensionEligible.value,
      isPregnant: isPregnant.value,
      isSingleParent: isSingleParent.value,
      hasCareNeeds: hasCareNeeds.value,
      citizenBenefitLast3Years: citizenBenefitLast3Years.value,
      hasFinancialHardship: hasFinancialHardship.value,
      workAbility: workAbility.value,

      // Insurance & Benefits
      healthInsurance: healthInsurance.value,
      hasCareInsurance: hasCareInsurance.value,
      receivesChildBenefit: receivesChildBenefit.value,
      receivesHousingBenefit: receivesHousingBenefit.value,
      receivesStudentAid: receivesStudentAid.value,
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

      // Personal Information
      firstName.value = formData.firstName;
      lastName.value = formData.lastName;
      dateOfBirth.value = formData.dateOfBirth;
      age.value = formData.age;
      gender.value = formData.gender;
      maritalStatus.value = formData.maritalStatus;
      nationality.value = formData.nationality;
      residenceStatus.value = formData.residenceStatus;
      residenceInGermany.value = formData.residenceInGermany;

      // Financial Information
      grossMonthlyIncome.value = formData.grossMonthlyIncome;
      netMonthlyIncome.value = formData.netMonthlyIncome;
      assets.value = formData.assets;
      monthlyRent.value = formData.monthlyRent;

      // Household Information
      householdSize.value = formData.householdSize;
      numberOfChildren.value = formData.numberOfChildren;
      childrenAges.value = formData.childrenAges;

      // Education & Employment
      employmentStatus.value = formData.employmentStatus;
      educationLevel.value = formData.educationLevel;
      isStudent.value = formData.isStudent;

      // Special Circumstances
      hasDisability.value = formData.hasDisability;
      disabilityDegree.value = formData.disabilityDegree;
      receivesUnemploymentBenefit1.value =
        formData.receivesUnemploymentBenefit1;
      receivesUnemploymentBenefit2.value =
        formData.receivesUnemploymentBenefit2;
      receivesPension.value = formData.receivesPension;
      pensionEligible.value = formData.pensionEligible;
      isPregnant.value = formData.isPregnant;
      isSingleParent.value = formData.isSingleParent;
      hasCareNeeds.value = formData.hasCareNeeds;
      citizenBenefitLast3Years.value = formData.citizenBenefitLast3Years;
      hasFinancialHardship.value = formData.hasFinancialHardship;
      workAbility.value = formData.workAbility;

      // Insurance & Benefits
      healthInsurance.value = formData.healthInsurance;
      hasCareInsurance.value = formData.hasCareInsurance;
      receivesChildBenefit.value = formData.receivesChildBenefit;
      receivesHousingBenefit.value = formData.receivesHousingBenefit;
      receivesStudentAid.value = formData.receivesStudentAid;

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

    // Personal Information
    firstName.value = undefined;
    lastName.value = undefined;
    dateOfBirth.value = undefined;
    age.value = undefined;
    gender.value = undefined;
    maritalStatus.value = undefined;
    nationality.value = undefined;
    residenceStatus.value = undefined;
    residenceInGermany.value = undefined;

    // Financial Information
    grossMonthlyIncome.value = undefined;
    netMonthlyIncome.value = undefined;
    assets.value = undefined;
    monthlyRent.value = undefined;

    // Household Information
    householdSize.value = undefined;
    numberOfChildren.value = undefined;
    childrenAges.value = undefined;

    // Education & Employment
    employmentStatus.value = undefined;
    educationLevel.value = undefined;
    isStudent.value = undefined;

    // Special Circumstances
    hasDisability.value = undefined;
    disabilityDegree.value = undefined;
    receivesUnemploymentBenefit1.value = undefined;
    receivesUnemploymentBenefit2.value = undefined;
    receivesPension.value = undefined;
    pensionEligible.value = undefined;
    isPregnant.value = undefined;
    isSingleParent.value = undefined;
    hasCareNeeds.value = undefined;
    citizenBenefitLast3Years.value = undefined;
    hasFinancialHardship.value = undefined;
    workAbility.value = undefined;

    // Insurance & Benefits
    healthInsurance.value = undefined;
    hasCareInsurance.value = undefined;
    receivesChildBenefit.value = undefined;
    receivesHousingBenefit.value = undefined;
    receivesStudentAid.value = undefined;

    eligibilityResults.value = [];
    visibleFields.value = [];
    showMessage("Daten wurden erfolgreich gelöscht!", "success");
  } catch (error) {
    showMessage("Fehler beim Löschen der Daten: " + error, "emergency");
  }
}

function checkEligibility() {
  const formData: FormData = {
    // Personal Information
    firstName: firstName.value,
    lastName: lastName.value,
    dateOfBirth: dateOfBirth.value,
    age: calculatedAge.value, // Use calculated age
    gender: gender.value,
    maritalStatus: maritalStatus.value,
    nationality: nationality.value,
    residenceStatus: residenceStatus.value,
    residenceInGermany: residenceInGermany.value,

    // Financial Information
    grossMonthlyIncome: grossMonthlyIncome.value,
    netMonthlyIncome: netMonthlyIncome.value,
    assets: assets.value,
    monthlyRent: monthlyRent.value,

    // Household Information
    householdSize: householdSize.value,
    numberOfChildren: numberOfChildren.value,
    childrenAges: childrenAges.value,

    // Education & Employment
    employmentStatus: employmentStatus.value,
    educationLevel: educationLevel.value,
    isStudent: isStudent.value,

    // Special Circumstances
    hasDisability: hasDisability.value,
    disabilityDegree: disabilityDegree.value,
    receivesUnemploymentBenefit1: receivesUnemploymentBenefit1.value,
    receivesUnemploymentBenefit2: receivesUnemploymentBenefit2.value,
    receivesPension: receivesPension.value,
    pensionEligible: pensionEligible.value,
    isPregnant: isPregnant.value,
    isSingleParent: isSingleParent.value,
    hasCareNeeds: hasCareNeeds.value,
    citizenBenefitLast3Years: citizenBenefitLast3Years.value,
    hasFinancialHardship: hasFinancialHardship.value,
    workAbility: workAbility.value,

    // Insurance & Benefits
    healthInsurance: healthInsurance.value,
    hasCareInsurance: hasCareInsurance.value,
    receivesChildBenefit: receivesChildBenefit.value,
    receivesHousingBenefit: receivesHousingBenefit.value,
    receivesStudentAid: receivesStudentAid.value,
  };

  // Use the registry to evaluate all checks
  const result = eligibilityRegistry.getVisibleSections(formData);

  console.log(result)
  allEligibilityResults.value = result.all;
  eligibilityResults.value = result.eligible;
  visibleFields.value = result.visibleFields;
}
checkEligibility();

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
