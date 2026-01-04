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
        <option value="employed">Angestellt</option>
        <option value="self_employed">Selbstständig</option>
        <option value="unemployed">Arbeitslos</option>
        <option value="student">Student/in</option>
        <option value="retired">Rentner/in</option>
        <option value="other">Sonstiges</option>
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
        <option value="no_degree">Kein Abschluss</option>
        <option value="lower_secondary">Hauptschule</option>
        <option value="secondary">Realschule</option>
        <option value="high_school">Abitur</option>
        <option value="vocational_training">Ausbildung</option>
        <option value="university">Studium</option>
      </select>
    </div>
    
    <div v-if="shouldShowField('isStudent')">
      <yes-no-input
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
  employmentStatus?: 'employed' | 'self_employed' | 'unemployed' | 'student' | 'retired' | 'other';
  educationLevel?: 'no_degree' | 'lower_secondary' | 'secondary' | 'high_school' | 'vocational_training' | 'university';
  isStudent?: boolean;
  shouldShowField: (field: FormDataField) => boolean;
}>();

const emit = defineEmits<{
  'update:employmentStatus': [value: 'employed' | 'self_employed' | 'unemployed' | 'student' | 'retired' | 'other' | undefined];
  'update:educationLevel': [value: 'no_degree' | 'lower_secondary' | 'secondary' | 'high_school' | 'vocational_training' | 'university' | undefined];
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
