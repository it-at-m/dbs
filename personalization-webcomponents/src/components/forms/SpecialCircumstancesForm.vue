<template>
  <fieldset class="form-fieldset">
    <legend>Besondere Umstände</legend>
    
    <div v-if="shouldShowField('hasDisability')">
      <YesNoInput
        :model-value="hasDisability"
        @update:model-value="$emit('update:hasDisability', $event)"
        label="Behinderung"
        name="hasDisability"
      />
    </div>
    
    <div v-if="shouldShowField('disabilityDegree') && hasDisability" class="m-form-group">
      <label for="disabilityDegree" class="m-label">Grad der Behinderung (%)</label>
      <input 
        id="disabilityDegree" 
        :value="disabilityDegree" 
        @input="$emit('update:disabilityDegree', parseNumberOrUndefined(($event.target as HTMLInputElement).value))"
        type="number" 
        min="0" 
        max="100" 
        placeholder="z.B. 50" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('isPregnant')">
      <YesNoInput
        :model-value="isPregnant"
        @update:model-value="$emit('update:isPregnant', $event)"
        label="Schwanger"
        name="isPregnant"
      />
    </div>
    
    <div v-if="shouldShowField('hasCareNeeds')">
      <YesNoInput
        :model-value="hasCareNeeds"
        @update:model-value="$emit('update:hasCareNeeds', $event)"
        label="Pflegebedürftigkeit"
        name="hasCareNeeds"
      />
    </div>
    
    <div v-if="shouldShowField('pensionEligible')">
      <YesNoInput
        :model-value="pensionEligible"
        @update:model-value="$emit('update:pensionEligible', $event)"
        label="Rentenberechtigt (Rentenalter erreicht)"
        name="pensionEligible"
      />
    </div>
    
    <div v-if="shouldShowField('citizenBenefitLast3Years')">
      <YesNoInput
        :model-value="citizenBenefitLast3Years"
        @update:model-value="$emit('update:citizenBenefitLast3Years', $event)"
        label="Bürgergeld in den letzten 3 Jahren bezogen"
        name="citizenBenefitLast3Years"
      />
    </div>
    
    <div v-if="shouldShowField('hasFinancialHardship')">
      <YesNoInput
        :model-value="hasFinancialHardship"
        @update:model-value="$emit('update:hasFinancialHardship', $event)"
        label="Finanzielle Notlage"
        name="hasFinancialHardship"
      />
    </div>
    
    <div v-if="shouldShowField('workAbility')" class="m-form-group">
      <label for="workAbility" class="m-label">Arbeitsfähigkeit</label>
      <select 
        id="workAbility" 
        :value="workAbility" 
        @change="$emit('update:workAbility', ($event.target as HTMLSelectElement).value || undefined)"
        class="m-textfield"
      >
        <option :value="undefined">Bitte wählen</option>
        <option value="voll">Voll arbeitsfähig</option>
        <option value="eingeschraenkt">Eingeschränkt arbeitsfähig</option>
        <option value="keine">Nicht arbeitsfähig</option>
      </select>
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import type { FormDataField } from "@/types/EligibilityCheckInterface";
import YesNoInput from "@/components/YesNoInput.vue";

defineProps<{
  hasDisability?: boolean;
  disabilityDegree?: number;
  isPregnant?: boolean;
  hasCareNeeds?: boolean;
  pensionEligible?: boolean;
  citizenBenefitLast3Years?: boolean;
  hasFinancialHardship?: boolean;
  workAbility?: 'voll' | 'eingeschraenkt' | 'keine';
  shouldShowField: (field: FormDataField) => boolean;
}>();

defineEmits<{
  'update:hasDisability': [value: boolean | undefined];
  'update:disabilityDegree': [value: number | undefined];
  'update:isPregnant': [value: boolean | undefined];
  'update:hasCareNeeds': [value: boolean | undefined];
  'update:pensionEligible': [value: boolean | undefined];
  'update:citizenBenefitLast3Years': [value: boolean | undefined];
  'update:hasFinancialHardship': [value: boolean | undefined];
  'update:workAbility': [value: 'voll' | 'eingeschraenkt' | 'keine' | undefined];
}>();

const parseNumberOrUndefined = (value: string): number | undefined => {
  const parsed = parseFloat(value);
  return isNaN(parsed) ? undefined : parsed;
};
</script>
