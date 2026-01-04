<template>
  <fieldset class="form-fieldset">
    <legend>Besondere Umstände</legend>
    
    <div v-if="shouldShowField('hasDisability')">
      <yes-no-input
        v-model="hasDisabilityModel"
        label="Behinderung"
        name="hasDisability"
      />
    </div>
    
    <div v-if="shouldShowField('disabilityDegree') && hasDisability" class="m-form-group">
      <label for="disabilityDegree" class="m-label">Grad der Behinderung (%)</label>
      <input 
        id="disabilityDegree" 
        v-model.number="disabilityDegreeModel"
        type="number" 
        min="0" 
        max="100" 
        placeholder="z.B. 50" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('isPregnant')">
      <yes-no-input
        v-model="isPregnantModel"
        label="Schwanger"
        name="isPregnant"
      />
    </div>
    
    <div v-if="shouldShowField('hasCareNeeds')">
      <yes-no-input
        v-model="hasCareNeedsModel"
        label="Pflegebedürftigkeit"
        name="hasCareNeeds"
      />
    </div>
    
    <div v-if="shouldShowField('pensionEligible')">
      <yes-no-input
        v-model="pensionEligibleModel"
        label="Rentenberechtigt (Rentenalter erreicht)"
        name="pensionEligible"
      />
    </div>
    
    <div v-if="shouldShowField('citizenBenefitLast3Years')">
      <yes-no-input
        v-model="citizenBenefitLast3YearsModel"
        label="Bürgergeld in den letzten 3 Jahren bezogen"
        name="citizenBenefitLast3Years"
      />
    </div>
    
    <div v-if="shouldShowField('hasFinancialHardship')">
      <yes-no-input
        v-model="hasFinancialHardshipModel"
        label="Finanzielle Notlage"
        name="hasFinancialHardship"
      />
    </div>
    
    <div v-if="shouldShowField('workAbility')" class="m-form-group">
      <label for="workAbility" class="m-label">Arbeitsfähigkeit</label>
      <select 
        id="workAbility" 
        v-model="workAbilityModel"
        class="m-textfield"
      >
        <option :value="undefined">Bitte wählen</option>
        <option value="full">Voll arbeitsfähig</option>
        <option value="limited">Eingeschränkt arbeitsfähig</option>
        <option value="none">Nicht arbeitsfähig</option>
      </select>
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { FormDataField } from "@/types/EligibilityCheckInterface";
import YesNoInput from "@/components/YesNoInput.vue";

const props = defineProps<{
  hasDisability?: boolean;
  disabilityDegree?: number;
  isPregnant?: boolean;
  hasCareNeeds?: boolean;
  pensionEligible?: boolean;
  citizenBenefitLast3Years?: boolean;
  hasFinancialHardship?: boolean;
  workAbility?: 'full' | 'limited' | 'none';
  shouldShowField: (field: FormDataField) => boolean;
}>();

const emit = defineEmits<{
  'update:hasDisability': [value: boolean | undefined];
  'update:disabilityDegree': [value: number | undefined];
  'update:isPregnant': [value: boolean | undefined];
  'update:hasCareNeeds': [value: boolean | undefined];
  'update:pensionEligible': [value: boolean | undefined];
  'update:citizenBenefitLast3Years': [value: boolean | undefined];
  'update:hasFinancialHardship': [value: boolean | undefined];
  'update:workAbility': [value: 'full' | 'limited' | 'none' | undefined];
}>();

// Create computed properties with getters/setters for v-model
const hasDisabilityModel = computed({
  get: () => props.hasDisability,
  set: (value) => emit('update:hasDisability', value)
});

const disabilityDegreeModel = computed({
  get: () => props.disabilityDegree,
  set: (value) => emit('update:disabilityDegree', value || undefined)
});

const isPregnantModel = computed({
  get: () => props.isPregnant,
  set: (value) => emit('update:isPregnant', value)
});

const hasCareNeedsModel = computed({
  get: () => props.hasCareNeeds,
  set: (value) => emit('update:hasCareNeeds', value)
});

const pensionEligibleModel = computed({
  get: () => props.pensionEligible,
  set: (value) => emit('update:pensionEligible', value)
});

const citizenBenefitLast3YearsModel = computed({
  get: () => props.citizenBenefitLast3Years,
  set: (value) => emit('update:citizenBenefitLast3Years', value)
});

const hasFinancialHardshipModel = computed({
  get: () => props.hasFinancialHardship,
  set: (value) => emit('update:hasFinancialHardship', value)
});

const workAbilityModel = computed({
  get: () => props.workAbility,
  set: (value) => emit('update:workAbility', value || undefined)
});
</script>
