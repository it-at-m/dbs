<template>
  <fieldset class="form-fieldset">
    <legend>Persönliche Informationen</legend>
    
    <div v-if="shouldShowField('firstName')" class="m-form-group">
      <label for="firstName" class="m-label">Vorname</label>
      <input 
        id="firstName" 
        v-model="firstNameModel"
        type="text" 
        placeholder="Vorname eingeben" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('lastName')" class="m-form-group">
      <label for="lastName" class="m-label">Nachname</label>
      <input 
        id="lastName" 
        v-model="lastNameModel"
        type="text" 
        placeholder="Nachname eingeben" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('age')" class="m-form-group">
      <label for="dateOfBirth" class="m-label">Geburtsdatum</label>
      <input 
        id="dateOfBirth" 
        v-model="dateOfBirthModel"
        type="date" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('gender')" class="m-form-group">
      <label for="gender" class="m-label">Geschlecht</label>
      <select 
        id="gender" 
        v-model="genderModel"
        class="m-textfield"
      >
        <option :value="undefined">Bitte wählen</option>
        <option value="male">Männlich</option>
        <option value="female">Weiblich</option>
        <option value="diverse">Divers</option>
        <option value="unspecified">Keine Angabe</option>
      </select>
    </div>
    
    <div v-if="shouldShowField('maritalStatus')" class="m-form-group">
      <label for="maritalStatus" class="m-label">Familienstand</label>
      <select 
        id="maritalStatus" 
        v-model="maritalStatusModel"
        class="m-textfield"
      >
        <option :value="undefined">Bitte wählen</option>
        <option value="single">Ledig</option>
        <option value="married">Verheiratet</option>
        <option value="divorced">Geschieden</option>
        <option value="widowed">Verwitwet</option>
        <option value="separated">Getrennt</option>
      </select>
    </div>
    
    <div v-if="shouldShowField('nationality')" class="m-form-group">
      <label for="nationality" class="m-label">Staatsangehörigkeit</label>
      <select 
        id="nationality" 
        v-model="nationalityModel"
        class="m-textfield"
      >
        <option :value="undefined">Bitte wählen</option>
        <option value="German">Deutsch</option>
        <option value="EU">Europäisch (EU)</option>
        <option value="Non-EU">Nicht-EU</option>
      </select>
    </div>
    
    <div v-if="shouldShowField('residenceStatus') && nationality !== 'German'" class="m-form-group">
      <label for="residenceStatus" class="m-label">Aufenthaltsstatus</label>
      <select 
        id="residenceStatus" 
        v-model="residenceStatusModel"
        class="m-textfield"
      >
        <option :value="undefined">Bitte wählen</option>
        <option value="residence_permit">Aufenthaltserlaubnis</option>
        <option value="permanent_residence">Niederlassungserlaubnis</option>
        <option value="none">Keine</option>
      </select>
    </div>
    
    <div v-if="shouldShowField('residenceInGermany')">
      <yes-no-input
        v-model="residenceInGermanyModel"
        label="Gewöhnlicher Aufenthalt in Deutschland"
        name="residenceInGermany"
      />
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { FormDataField } from "@/types/EligibilityCheckInterface";
import YesNoInput from "@/components/YesNoInput.vue";

const props = defineProps<{
  firstName?: string;
  lastName?: string;
  dateOfBirth?: string;
  gender?: 'male' | 'female' | 'diverse' | 'unspecified';
  maritalStatus?: 'single' | 'married' | 'divorced' | 'widowed' | 'separated';
  nationality?: 'German' | 'EU' | 'Non-EU';
  residenceStatus?: 'residence_permit' | 'permanent_residence' | 'none';
  residenceInGermany?: boolean;
  shouldShowField: (field: FormDataField) => boolean;
}>();

const emit = defineEmits<{
  'update:firstName': [value: string | undefined];
  'update:lastName': [value: string | undefined];
  'update:dateOfBirth': [value: string | undefined];
  'update:gender': [value: 'male' | 'female' | 'diverse' | 'unspecified' | undefined];
  'update:maritalStatus': [value: 'single' | 'married' | 'divorced' | 'widowed' | 'separated' | undefined];
  'update:nationality': [value: 'German' | 'EU' | 'Non-EU' | undefined];
  'update:residenceStatus': [value: 'residence_permit' | 'permanent_residence' | 'none' | undefined];
  'update:residenceInGermany': [value: boolean | undefined];
}>();

// Create computed properties with getters/setters for v-model
const firstNameModel = computed({
  get: () => props.firstName,
  set: (value) => emit('update:firstName', value || undefined)
});

const lastNameModel = computed({
  get: () => props.lastName,
  set: (value) => emit('update:lastName', value || undefined)
});

const dateOfBirthModel = computed({
  get: () => props.dateOfBirth,
  set: (value) => emit('update:dateOfBirth', value || undefined)
});

const genderModel = computed({
  get: () => props.gender,
  set: (value) => emit('update:gender', value || undefined)
});

const maritalStatusModel = computed({
  get: () => props.maritalStatus,
  set: (value) => emit('update:maritalStatus', value || undefined)
});

const nationalityModel = computed({
  get: () => props.nationality,
  set: (value) => emit('update:nationality', value || undefined)
});

const residenceStatusModel = computed({
  get: () => props.residenceStatus,
  set: (value) => emit('update:residenceStatus', value || undefined)
});

const residenceInGermanyModel = computed({
  get: () => props.residenceInGermany,
  set: (value) => emit('update:residenceInGermany', value)
});
</script>
