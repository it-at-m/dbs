<template>
  <fieldset class="form-fieldset">
    <legend>Finanzielle Angaben</legend>
    
    <div v-if="shouldShowField('grossMonthlyIncome')" class="m-form-group">
      <label for="grossMonthlyIncome" class="m-label">Monatliches Bruttoeinkommen (€)</label>
      <input 
        id="grossMonthlyIncome" 
        :value="grossMonthlyIncome" 
        @input="$emit('update:grossMonthlyIncome', parseNumberOrUndefined(($event.target as HTMLInputElement).value))"
        type="number" 
        step="0.01" 
        placeholder="z.B. 2000" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('netMonthlyIncome')" class="m-form-group">
      <label for="netMonthlyIncome" class="m-label">Monatliches Nettoeinkommen (€)</label>
      <input 
        id="netMonthlyIncome" 
        :value="netMonthlyIncome" 
        @input="$emit('update:netMonthlyIncome', parseNumberOrUndefined(($event.target as HTMLInputElement).value))"
        type="number" 
        step="0.01" 
        placeholder="z.B. 1500" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('assets')" class="m-form-group">
      <label for="assets" class="m-label">Vermögen (€)</label>
      <input 
        id="assets" 
        :value="assets" 
        @input="$emit('update:assets', parseNumberOrUndefined(($event.target as HTMLInputElement).value))"
        type="number" 
        step="0.01" 
        placeholder="z.B. 5000" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('monthlyRent')" class="m-form-group">
      <label for="monthlyRent" class="m-label">Monatliche Miete (€)</label>
      <input 
        id="monthlyRent" 
        :value="monthlyRent" 
        @input="$emit('update:monthlyRent', parseNumberOrUndefined(($event.target as HTMLInputElement).value))"
        type="number" 
        step="0.01" 
        placeholder="z.B. 800" 
        class="m-textfield" 
      />
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import type { FormDataField } from "@/types/EligibilityCheckInterface";

defineProps<{
  grossMonthlyIncome?: number;
  netMonthlyIncome?: number;
  assets?: number;
  monthlyRent?: number;
  shouldShowField: (field: FormDataField) => boolean;
}>();

defineEmits<{
  'update:grossMonthlyIncome': [value: number | undefined];
  'update:netMonthlyIncome': [value: number | undefined];
  'update:assets': [value: number | undefined];
  'update:monthlyRent': [value: number | undefined];
}>();

const parseNumberOrUndefined = (value: string): number | undefined => {
  const parsed = parseFloat(value);
  return isNaN(parsed) ? undefined : parsed;
};
</script>

