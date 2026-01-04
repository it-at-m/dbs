<template>
  <fieldset class="form-fieldset">
    <legend>Bildung & Beschäftigung</legend>
    
    <div v-if="shouldShowField('employmentStatus')" class="m-form-group">
      <label for="employmentStatus" class="m-label">Beschäftigungsstatus</label>
      <select 
        id="employmentStatus" 
        :value="employmentStatus" 
        @change="$emit('update:employmentStatus', ($event.target as HTMLSelectElement).value || undefined)"
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
        :value="educationLevel" 
        @change="$emit('update:educationLevel', ($event.target as HTMLSelectElement).value || undefined)"
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
        :model-value="isStudent"
        @update:model-value="$emit('update:isStudent', $event)"
        label="Ich bin Student/in"
        name="isStudent"
      />
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import type { FormDataField } from "@/types/EligibilityCheckInterface";
import YesNoInput from "@/components/YesNoInput.vue";

defineProps<{
  employmentStatus?: 'angestellt' | 'selbststaendig' | 'arbeitslos' | 'student' | 'rentner' | 'sonstiges';
  educationLevel?: 'kein_abschluss' | 'hauptschule' | 'realschule' | 'abitur' | 'ausbildung' | 'studium';
  isStudent?: boolean;
  shouldShowField: (field: FormDataField) => boolean;
}>();

defineEmits<{
  'update:employmentStatus': [value: 'angestellt' | 'selbststaendig' | 'arbeitslos' | 'student' | 'rentner' | 'sonstiges' | undefined];
  'update:educationLevel': [value: 'kein_abschluss' | 'hauptschule' | 'realschule' | 'abitur' | 'ausbildung' | 'studium' | undefined];
  'update:isStudent': [value: boolean | undefined];
}>();
</script>

