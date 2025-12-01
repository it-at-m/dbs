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
                
                <div class="m-form-group">
                  <label for="geschlecht" class="m-label">Geschlecht</label>
                  <select id="geschlecht" v-model="geschlecht" class="m-textfield">
                    <option :value="undefined">Bitte wählen</option>
                    <option value="männlich">Männlich</option>
                    <option value="weiblich">Weiblich</option>
                    <option value="divers">Divers</option>
                    <option value="keine Angabe">Keine Angabe</option>
                  </select>
                </div>
                
                <div class="m-form-group">
                  <label for="familienstand" class="m-label">Familienstand</label>
                  <select id="familienstand" v-model="familienstand" class="m-textfield">
                    <option :value="undefined">Bitte wählen</option>
                    <option value="ledig">Ledig</option>
                    <option value="verheiratet">Verheiratet</option>
                    <option value="geschieden">Geschieden</option>
                    <option value="verwitwet">Verwitwet</option>
                    <option value="getrennt">Getrennt</option>
                  </select>
                </div>
                
                <div class="m-form-group">
                  <label for="staatsangehoerigkeit" class="m-label">Staatsangehörigkeit</label>
                  <select id="staatsangehoerigkeit" v-model="staatsangehoerigkeit" class="m-textfield">
                    <option :value="undefined">Bitte wählen</option>
                    <option value="Deutsch">Deutsch</option>
                    <option value="EU">Europäisch (EU)</option>
                    <option value="Nicht-EU">Nicht-EU</option>
                  </select>
                </div>
              </fieldset>

              <!-- Fieldset 2: Address Information -->
              <fieldset class="form-fieldset">
                <legend>Adresse</legend>
                
                <div class="m-form-group">
                  <label for="strasse" class="m-label">Straße</label>
                  <input id="strasse" v-model="strasse" type="text" 
                         placeholder="Straßenname" class="m-textfield" />
                </div>
                
                <div class="m-form-group">
                  <label for="hausnummer" class="m-label">Hausnummer</label>
                  <input id="hausnummer" v-model="hausnummer" type="text" 
                         placeholder="z.B. 42a" class="m-textfield" />
                </div>
                
                <div class="m-form-group">
                  <label for="postleitzahl" class="m-label">Postleitzahl</label>
                  <input id="postleitzahl" v-model="postleitzahl" type="text" 
                         placeholder="z.B. 80331" class="m-textfield" />
                </div>
                
                <div class="m-form-group">
                  <label for="stadt" class="m-label">Stadt</label>
                  <input id="stadt" v-model="stadt" type="text" 
                         placeholder="z.B. München" class="m-textfield" />
                </div>
                
                <div class="m-form-group">
                  <label for="bundesland" class="m-label">Bundesland</label>
                  <input id="bundesland" v-model="bundesland" type="text" 
                         placeholder="z.B. Bayern" class="m-textfield" />
                </div>
                
                <div class="m-form-group">
                  <label for="wohnflaeche" class="m-label">Wohnfläche (m²)</label>
                  <input id="wohnflaeche" v-model.number="wohnflaeche" 
                         type="number" step="0.01" placeholder="z.B. 65" class="m-textfield" />
                </div>
              </fieldset>

              <!-- Fieldset 3: Financial Information -->
              <fieldset class="form-fieldset">
                <legend>Finanzielle Angaben</legend>
                
                <div class="m-form-group">
                  <label for="bruttoEinkommen" class="m-label">Monatliches Bruttoeinkommen (€)</label>
                  <input id="bruttoEinkommen" v-model.number="bruttoEinkommenMonatlich" 
                         type="number" step="0.01" placeholder="z.B. 2000" class="m-textfield" />
                </div>
                
                <div class="m-form-group">
                  <label for="nettoEinkommen" class="m-label">Monatliches Nettoeinkommen (€)</label>
                  <input id="nettoEinkommen" v-model.number="nettoEinkommenMonatlich" 
                         type="number" step="0.01" placeholder="z.B. 1500" class="m-textfield" />
                </div>
                
                <div class="m-form-group">
                  <label for="vermoegen" class="m-label">Vermögen (€)</label>
                  <input id="vermoegen" v-model.number="vermoegen" 
                         type="number" step="0.01" placeholder="z.B. 5000" class="m-textfield" />
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
                
                <div v-if="anzahlPersonenHaushalt && anzahlPersonenHaushalt > 1" class="m-form-group">
                  <label for="kinder" class="m-label">Anzahl Kinder</label>
                  <input id="kinder" v-model.number="anzahlKinder" 
                         type="number" min="0" placeholder="z.B. 1" class="m-textfield" />
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="istAlleinerziehend" class="m-checkbox" />
                    <span>Alleinerziehend</span>
                  </label>
                </div>
              </fieldset>

              <!-- Fieldset 5: Education & Employment -->
              <fieldset class="form-fieldset">
                <legend>Bildung & Beschäftigung</legend>
                
                <div class="m-form-group">
                  <label for="beschaeftigungsstatus" class="m-label">Beschäftigungsstatus</label>
                  <select id="beschaeftigungsstatus" v-model="beschaeftigungsstatus" class="m-textfield">
                    <option :value="undefined">Bitte wählen</option>
                    <option value="angestellt">Angestellt</option>
                    <option value="selbststaendig">Selbstständig</option>
                    <option value="arbeitslos">Arbeitslos</option>
                    <option value="student">Student/in</option>
                    <option value="rentner">Rentner/in</option>
                    <option value="sonstiges">Sonstiges</option>
                  </select>
                </div>
                
                <div class="m-form-group">
                  <label for="bildungsstand" class="m-label">Bildungsstand</label>
                  <select id="bildungsstand" v-model="bildungsstand" class="m-textfield">
                    <option :value="undefined">Bitte wählen</option>
                    <option value="kein_abschluss">Kein Abschluss</option>
                    <option value="hauptschule">Hauptschule</option>
                    <option value="realschule">Realschule</option>
                    <option value="abitur">Abitur</option>
                    <option value="ausbildung">Ausbildung</option>
                    <option value="studium">Studium</option>
                  </select>
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="istStudent" class="m-checkbox" />
                    <span>Ich bin Student/in</span>
                  </label>
                </div>
              </fieldset>

              <!-- Fieldset 6: Special Circumstances -->
              <fieldset class="form-fieldset">
                <legend>Besondere Umstände</legend>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="hatBehinderung" class="m-checkbox" />
                    <span>Behinderung</span>
                  </label>
                </div>
                
                <div v-if="hatBehinderung" class="m-form-group">
                  <label for="gradDerBehinderung" class="m-label">Grad der Behinderung (%)</label>
                  <input id="gradDerBehinderung" v-model.number="gradDerBehinderung" 
                         type="number" min="0" max="100" placeholder="z.B. 50" class="m-textfield" />
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="istSchwanger" class="m-checkbox" />
                    <span>Schwanger</span>
                  </label>
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="hatPflegebeduerftigkeit" class="m-checkbox" />
                    <span>Pflegebedürftigkeit</span>
                  </label>
                </div>
              </fieldset>

              <!-- Fieldset 7: Insurance & Benefits -->
              <fieldset class="form-fieldset">
                <legend>Versicherung & Leistungen</legend>
                
                <div class="m-form-group">
                  <label for="krankenversicherung" class="m-label">Krankenversicherung</label>
                  <select id="krankenversicherung" v-model="krankenversicherung" class="m-textfield">
                    <option :value="undefined">Bitte wählen</option>
                    <option value="gesetzlich">Gesetzlich</option>
                    <option value="privat">Privat</option>
                    <option value="keine">Keine</option>
                  </select>
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="beziehtAlg1" class="m-checkbox" />
                    <span>Beziehe Arbeitslosengeld I</span>
                  </label>
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="beziehtAlg2" class="m-checkbox" />
                    <span>Beziehe Bürgergeld (ALG II)</span>
                  </label>
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="beziehtRente" class="m-checkbox" />
                    <span>Beziehe Rente</span>
                  </label>
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="beziehtKindergeld" class="m-checkbox" />
                    <span>Beziehe Kindergeld</span>
                  </label>
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="beziehtWohngeld" class="m-checkbox" />
                    <span>Beziehe Wohngeld</span>
                  </label>
                </div>
                
                <div class="m-form-group m-form-group-checkbox">
                  <label class="m-checkbox-label">
                    <input type="checkbox" v-model="beziehtBafög" class="m-checkbox" />
                    <span>Beziehe BAföG</span>
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
              v-if="eligibilityResults.length > 0 || allEligibilityResults.length > 0"
              class="eligibility-results"
            >
              <div class="eligibility-header">
                <div>
                  <h2 class="eligibility-title">
                    Mögliche Leistungen ({{ eligibilityResults.length }})
                  </h2>
                  <p class="eligibility-subtitle">
                    Basierend auf Ihren Angaben könnten folgende Leistungen für Sie
                    in Frage kommen:
                  </p>
                </div>
                <muc-button
                  type="button"
                  variant="ghost"
                  @click="showAllResults = !showAllResults"
                  class="toggle-all-button"
                >
                  {{ showAllResults ? 'Nur berechtigte anzeigen' : 'Alle Leistungen anzeigen' }}
                </muc-button>
              </div>
              
              <div
                v-for="result in (showAllResults ? allEligibilityResults : eligibilityResults)"
                :key="result.subsidyName"
                :class="['eligibility-card', { 'not-eligible': !result.eligible }]"
              >
                <div class="eligibility-card-header">
                  <h3 class="eligibility-card-title">
                    {{ result.subsidyName }}
                  </h3>
                  <span :class="['eligibility-badge', result.eligible ? 'eligible' : 'not-eligible']">
                    {{ result.eligible ? 'Berechtigt' : 'Nicht berechtigt' }}
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
                    Bitte füllen Sie das Formular aus und klicken Sie auf "Speichern",
                    um Ihre Berechtigung zu prüfen.
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
import { onMounted, ref, computed } from "vue";

import { EligibilityCheckRegistry } from "@/eligibility/EligibilityCheckRegistry";
import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

const LOCALSTORAGE_KEY_FORMDATA = "user.formData";

// Personal Information
const firstName = ref("");
const lastName = ref("");
const geburtsdatum = ref("");
const alter = ref<number | undefined>(undefined);
const geschlecht = ref<'männlich' | 'weiblich' | 'divers' | 'keine Angabe' | undefined>(undefined);
const familienstand = ref<'ledig' | 'verheiratet' | 'geschieden' | 'verwitwet' | 'getrennt' | undefined>(undefined);
const staatsangehoerigkeit = ref<'Deutsch' | 'EU' | 'Nicht-EU' | undefined>(undefined);

// Address Information
const strasse = ref("");
const hausnummer = ref("");
const postleitzahl = ref("");
const stadt = ref("");
const bundesland = ref("");
const wohnflaeche = ref<number | undefined>(undefined);

// Financial Information
const bruttoEinkommenMonatlich = ref<number | undefined>(undefined);
const nettoEinkommenMonatlich = ref<number | undefined>(undefined);
const vermoegen = ref<number | undefined>(undefined);
const mieteMietzinsMonatlich = ref<number | undefined>(undefined);

// Household Information
const anzahlPersonenHaushalt = ref<number | undefined>(undefined);
const anzahlKinder = ref<number | undefined>(undefined);
const kinderAlter = ref<number[]>([]);

// Education & Employment
const beschaeftigungsstatus = ref<'angestellt' | 'selbststaendig' | 'arbeitslos' | 'student' | 'rentner' | 'sonstiges' | undefined>(undefined);
const bildungsstand = ref<'kein_abschluss' | 'hauptschule' | 'realschule' | 'abitur' | 'ausbildung' | 'studium' | undefined>(undefined);
const istStudent = ref(false);

// Special Circumstances
const hatBehinderung = ref(false);
const gradDerBehinderung = ref<number | undefined>(undefined);
const beziehtAlg1 = ref(false);
const beziehtAlg2 = ref(false);
const beziehtRente = ref(false);
const istSchwanger = ref(false);
const istAlleinerziehend = ref(false);
const hatPflegebeduerftigkeit = ref(false);

// Insurance & Benefits
const krankenversicherung = ref<'gesetzlich' | 'privat' | 'keine' | undefined>(undefined);
const beziehtKindergeld = ref(false);
const beziehtWohngeld = ref(false);
const beziehtBafög = ref(false);

const message = ref("");
const messageType = ref<"success" | "info" | "warning" | "emergency">("success");
const eligibilityResults = ref<EligibilityResult[]>([]);
const allEligibilityResults = ref<EligibilityResult[]>([]);
const showAllResults = ref(false);

const eligibilityRegistry = new EligibilityCheckRegistry();

// Helper function to calculate age from birth date
function calculateAge(birthDateString: string): number | undefined {
  if (!birthDateString) return undefined;
  
  const birthDate = new Date(birthDateString);
  const today = new Date();
  
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();
  
  // If birthday hasn't occurred this year yet, subtract 1
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }
  
  return age;
}

// Computed property for age based on birth date
const calculatedAge = computed(() => calculateAge(geburtsdatum.value));

onMounted(() => {
  loadData();
});

function saveData() {
  try {
    const formData: FormData = {
      // Personal Information
      firstName: firstName.value,
      lastName: lastName.value,
      geburtsdatum: geburtsdatum.value || undefined,
      alter: calculatedAge.value, // Use calculated age
      geschlecht: geschlecht.value,
      familienstand: familienstand.value,
      staatsangehoerigkeit: staatsangehoerigkeit.value || undefined,
      
      // Address Information
      strasse: strasse.value || undefined,
      hausnummer: hausnummer.value || undefined,
      postleitzahl: postleitzahl.value || undefined,
      stadt: stadt.value || undefined,
      bundesland: bundesland.value || undefined,
      wohnflaeche: wohnflaeche.value,
      
      // Financial Information
      bruttoEinkommenMonatlich: bruttoEinkommenMonatlich.value,
      nettoEinkommenMonatlich: nettoEinkommenMonatlich.value,
      vermoegen: vermoegen.value,
      mieteMietzinsMonatlich: mieteMietzinsMonatlich.value,
      
      // Household Information
      anzahlPersonenHaushalt: anzahlPersonenHaushalt.value,
      anzahlKinder: anzahlKinder.value,
      kinderAlter: kinderAlter.value,
      
      // Education & Employment
      beschaeftigungsstatus: beschaeftigungsstatus.value,
      bildungsstand: bildungsstand.value,
      istStudent: istStudent.value,
      
      // Special Circumstances
      hatBehinderung: hatBehinderung.value,
      gradDerBehinderung: gradDerBehinderung.value,
      beziehtAlg1: beziehtAlg1.value,
      beziehtAlg2: beziehtAlg2.value,
      beziehtRente: beziehtRente.value,
      istSchwanger: istSchwanger.value,
      istAlleinerziehend: istAlleinerziehend.value,
      hatPflegebeduerftigkeit: hatPflegebeduerftigkeit.value,
      
      // Insurance & Benefits
      krankenversicherung: krankenversicherung.value,
      beziehtKindergeld: beziehtKindergeld.value,
      beziehtWohngeld: beziehtWohngeld.value,
      beziehtBafög: beziehtBafög.value,
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
      firstName.value = formData.firstName || "";
      lastName.value = formData.lastName || "";
      geburtsdatum.value = formData.geburtsdatum || "";
      alter.value = formData.alter;
      geschlecht.value = formData.geschlecht;
      familienstand.value = formData.familienstand;
      staatsangehoerigkeit.value = formData.staatsangehoerigkeit;
      
      // Address Information
      strasse.value = formData.strasse || "";
      hausnummer.value = formData.hausnummer || "";
      postleitzahl.value = formData.postleitzahl || "";
      stadt.value = formData.stadt || "";
      bundesland.value = formData.bundesland || "";
      wohnflaeche.value = formData.wohnflaeche;
      
      // Financial Information
      bruttoEinkommenMonatlich.value = formData.bruttoEinkommenMonatlich;
      nettoEinkommenMonatlich.value = formData.nettoEinkommenMonatlich;
      vermoegen.value = formData.vermoegen;
      mieteMietzinsMonatlich.value = formData.mieteMietzinsMonatlich;
      
      // Household Information
      anzahlPersonenHaushalt.value = formData.anzahlPersonenHaushalt;
      anzahlKinder.value = formData.anzahlKinder;
      kinderAlter.value = formData.kinderAlter || [];
      
      // Education & Employment
      beschaeftigungsstatus.value = formData.beschaeftigungsstatus;
      bildungsstand.value = formData.bildungsstand;
      istStudent.value = formData.istStudent || false;
      
      // Special Circumstances
      hatBehinderung.value = formData.hatBehinderung || false;
      gradDerBehinderung.value = formData.gradDerBehinderung;
      beziehtAlg1.value = formData.beziehtAlg1 || false;
      beziehtAlg2.value = formData.beziehtAlg2 || false;
      beziehtRente.value = formData.beziehtRente || false;
      istSchwanger.value = formData.istSchwanger || false;
      istAlleinerziehend.value = formData.istAlleinerziehend || false;
      hatPflegebeduerftigkeit.value = formData.hatPflegebeduerftigkeit || false;
      
      // Insurance & Benefits
      krankenversicherung.value = formData.krankenversicherung;
      beziehtKindergeld.value = formData.beziehtKindergeld || false;
      beziehtWohngeld.value = formData.beziehtWohngeld || false;
      beziehtBafög.value = formData.beziehtBafög || false;
      
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
    firstName.value = "";
    lastName.value = "";
    geburtsdatum.value = "";
    alter.value = undefined;
    geschlecht.value = undefined;
    familienstand.value = undefined;
    staatsangehoerigkeit.value = undefined;
    
    // Address Information
    strasse.value = "";
    hausnummer.value = "";
    postleitzahl.value = "";
    stadt.value = "";
    bundesland.value = "";
    wohnflaeche.value = undefined;
    
    // Financial Information
    bruttoEinkommenMonatlich.value = undefined;
    nettoEinkommenMonatlich.value = undefined;
    vermoegen.value = undefined;
    mieteMietzinsMonatlich.value = undefined;
    
    // Household Information
    anzahlPersonenHaushalt.value = undefined;
    anzahlKinder.value = undefined;
    kinderAlter.value = [];
    
    // Education & Employment
    beschaeftigungsstatus.value = undefined;
    bildungsstand.value = undefined;
    istStudent.value = false;
    
    // Special Circumstances
    hatBehinderung.value = false;
    gradDerBehinderung.value = undefined;
    beziehtAlg1.value = false;
    beziehtAlg2.value = false;
    beziehtRente.value = false;
    istSchwanger.value = false;
    istAlleinerziehend.value = false;
    hatPflegebeduerftigkeit.value = false;
    
    // Insurance & Benefits
    krankenversicherung.value = undefined;
    beziehtKindergeld.value = false;
    beziehtWohngeld.value = false;
    beziehtBafög.value = false;
    
    eligibilityResults.value = [];
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
    geburtsdatum: geburtsdatum.value || undefined,
    alter: calculatedAge.value, // Use calculated age
    geschlecht: geschlecht.value,
    familienstand: familienstand.value,
    staatsangehoerigkeit: staatsangehoerigkeit.value || undefined,
    
    // Address Information
    strasse: strasse.value || undefined,
    hausnummer: hausnummer.value || undefined,
    postleitzahl: postleitzahl.value || undefined,
    stadt: stadt.value || undefined,
    bundesland: bundesland.value || undefined,
    wohnflaeche: wohnflaeche.value,
    
    // Financial Information
    bruttoEinkommenMonatlich: bruttoEinkommenMonatlich.value,
    nettoEinkommenMonatlich: nettoEinkommenMonatlich.value,
    vermoegen: vermoegen.value,
    mieteMietzinsMonatlich: mieteMietzinsMonatlich.value,
    
    // Household Information
    anzahlPersonenHaushalt: anzahlPersonenHaushalt.value,
    anzahlKinder: anzahlKinder.value,
    kinderAlter: kinderAlter.value,
    
    // Education & Employment
    beschaeftigungsstatus: beschaeftigungsstatus.value,
    bildungsstand: bildungsstand.value,
    istStudent: istStudent.value,
    
    // Special Circumstances
    hatBehinderung: hatBehinderung.value,
    gradDerBehinderung: gradDerBehinderung.value,
    beziehtAlg1: beziehtAlg1.value,
    beziehtAlg2: beziehtAlg2.value,
    beziehtRente: beziehtRente.value,
    istSchwanger: istSchwanger.value,
    istAlleinerziehend: istAlleinerziehend.value,
    hatPflegebeduerftigkeit: hatPflegebeduerftigkeit.value,
    
    // Insurance & Benefits
    krankenversicherung: krankenversicherung.value,
    beziehtKindergeld: beziehtKindergeld.value,
    beziehtWohngeld: beziehtWohngeld.value,
    beziehtBafög: beziehtBafög.value,
  };

  const checks = eligibilityRegistry.getAllChecks();
  const allResults = checks.map((check) => check.evaluate(formData));
  
  allEligibilityResults.value = allResults;
  eligibilityResults.value = allResults.filter((result) => result.eligible);
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
