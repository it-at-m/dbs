<template>
  <fieldset class="form-fieldset">
    <legend>Bildung & Beschäftigung</legend>
    
    <div v-if="shouldShowField('employmentStatus')" class="m-form-group">
      <label for="employmentStatus" class="m-label">Beschäftigungsstatus</label>
      <select 
        id="employmentStatus" 
        v-model="employmentStatusModel"
        class="m-textfield"
      >
        <option :value="undefined">Bitte wählen</option>
        <option value="angestellt">Angestellt</option>
        <option value="selbststaendig">Selbstständig</option>
        <option value="arbeitslos">Arbeitslos</option>
        <option value="student">Student/in</option>
        <option value="rentner">Rentner/in</option>
        <option value="sonstiges">Sonstiges</option>
      </select>
    </div>
    
    <div v-if="shouldShowField('educationLevel')" class="m-form-group">
      <label for="educationLevel" class="m-label">Bildungsstand</label>
      <select 
        id="educationLevel" 
        v-model="educationLevelModel"
        class="m-textfield"
      >
        <option :value="undefined">Bitte wählen</option>
        <option value="kein_abschluss">Kein Abschluss</option>
        <option value="hauptschule">Hauptschule</option>
        <option value="realschule">Realschule</option>
        <option value="abitur">Abitur</option>
        <option value="ausbildung">Ausbildung</option>
        <option value="studium">Studium</option>
      </select>
    </div>
    
    <div v-if="shouldShowField('isStudent')">
      <YesNoInput
        v-model="isStudentModel"
        label="Ich bin Student/in"
        name="isStudent"
      />
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { FormDataField } from "@/types/EligibilityCheckInterface";
import YesNoInput from "@/components/YesNoInput.vue";

const props = defineProps<{
  employmentStatus?: 'angestellt' | 'selbststaendig' | 'arbeitslos' | 'student' | 'rentner' | 'sonstiges';
  educationLevel?: 'kein_abschluss' | 'hauptschule' | 'realschule' | 'abitur' | 'ausbildung' | 'studium';
  isStudent?: boolean;
  shouldShowField: (field: FormDataField) => boolean;
}>();

const emit = defineEmits<{
  'update:employmentStatus': [value: 'angestellt' | 'selbststaendig' | 'arbeitslos' | 'student' | 'rentner' | 'sonstiges' | undefined];
  'update:educationLevel': [value: 'kein_abschluss' | 'hauptschule' | 'realschule' | 'abitur' | 'ausbildung' | 'studium' | undefined];
  'update:isStudent': [value: boolean | undefined];
}>();

// Create computed properties with getters/setters for v-model
const employmentStatusModel = computed({
  get: () => props.employmentStatus,
  set: (value) => emit('update:employmentStatus', value || undefined)
});

const educationLevelModel = computed({
  get: () => props.educationLevel,
  set: (value) => emit('update:educationLevel', value || undefined)
});

const isStudentModel = computed({
  get: () => props.isStudent,
  set: (value) => emit('update:isStudent', value)
});
</script>

