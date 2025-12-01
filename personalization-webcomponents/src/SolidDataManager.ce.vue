<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />

    <muc-intro
      title="Benutzerdaten verwalten"
      tagline="Formulardaten"
      :divider="false"
      variant="detail"
    >
      <p>
        Geben Sie Ihre persönlichen Daten ein. Diese werden lokal in Ihrem
        Browser gespeichert.
      </p>
    </muc-intro>

    <div class="m-component m-component-form">
      <div class="container">
        <div class="m-component__grid">
          <div class="m-component__column">
            <muc-banner
              v-if="message"
              :type="messageType"
              class="message-banner"
            >
              {{ message }}
            </muc-banner>

            <form
              @submit.prevent="saveData"
              class="form-content"
            >
              <div class="m-form-group">
                <label
                  for="firstName"
                  class="m-label"
                >
                  Vorname
                </label>
                <input
                  id="firstName"
                  v-model="firstName"
                  type="text"
                  placeholder="Vorname eingeben"
                  class="m-textfield"
                />
              </div>

              <div class="m-form-group">
                <label
                  for="lastName"
                  class="m-label"
                >
                  Nachname
                </label>
                <input
                  id="lastName"
                  v-model="lastName"
                  type="text"
                  placeholder="Nachname eingeben"
                  class="m-textfield"
                />
              </div>

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

            <div
              v-if="firstName || lastName"
              class="data-display"
            >
              <h2 class="data-display-title">Aktuelle Daten:</h2>
              <div class="data-display-content">
                <p><strong>Vorname:</strong> {{ firstName || '(leer)' }}</p>
                <p><strong>Nachname:</strong> {{ lastName || '(leer)' }}</p>
              </div>
            </div>

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
                      Mehr erfahren
                    </muc-button>
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { MucBanner, MucButton, MucIntro } from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";

import { EligibilityCheckRegistry } from "@/eligibility/EligibilityCheckRegistry";
import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

const LOCALSTORAGE_KEY_FIRSTNAME = "user.firstName";
const LOCALSTORAGE_KEY_LASTNAME = "user.lastName";

const firstName = ref("");
const lastName = ref("");
const message = ref("");
const messageType = ref<"success" | "info" | "warning" | "emergency">("success");
const eligibilityResults = ref<EligibilityResult[]>([]);

const eligibilityRegistry = new EligibilityCheckRegistry();

onMounted(() => {
  loadData();
});

function saveData() {
  try {
    localStorage.setItem(LOCALSTORAGE_KEY_FIRSTNAME, firstName.value);
    localStorage.setItem(LOCALSTORAGE_KEY_LASTNAME, lastName.value);
    showMessage("Daten wurden erfolgreich gespeichert!", "success");
    checkEligibility();
  } catch (error) {
    showMessage("Fehler beim Speichern der Daten: " + error, "emergency");
  }
}

function loadData() {
  try {
    const savedFirstName = localStorage.getItem(LOCALSTORAGE_KEY_FIRSTNAME);
    const savedLastName = localStorage.getItem(LOCALSTORAGE_KEY_LASTNAME);
    
    if (savedFirstName !== null) {
      firstName.value = savedFirstName;
    }
    if (savedLastName !== null) {
      lastName.value = savedLastName;
    }
    
    if (savedFirstName || savedLastName) {
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
    localStorage.removeItem(LOCALSTORAGE_KEY_FIRSTNAME);
    localStorage.removeItem(LOCALSTORAGE_KEY_LASTNAME);
    firstName.value = "";
    lastName.value = "";
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
  margin-top: 48px;
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

@media (max-width: 768px) {
  .button-group {
    flex-direction: column;
  }
  
  .button-group button {
    width: 100%;
  }
}

.eligibility-results {
  margin-top: 56px;
  padding-top: 40px;
  border-top: 2px solid var(--mde-color-neutral-beau-blue);
}

.eligibility-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--mde-color-neutral-grey-dark);
  margin-bottom: 16px;
}

.eligibility-subtitle {
  font-size: 1rem;
  color: var(--mde-color-neutral-grey);
  margin-bottom: 32px;
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

</style>


