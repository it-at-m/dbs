<template>
  <fieldset class="form-fieldset">
    <legend>Finanzielle Angaben</legend>
    
    <div v-if="shouldShowField('grossMonthlyIncome')" class="m-form-group">
      <label for="grossMonthlyIncome" class="m-label">Monatliches Bruttoeinkommen (€)</label>
      <input 
        id="grossMonthlyIncome" 
        v-model.number="grossMonthlyIncomeModel"
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
        v-model.number="netMonthlyIncomeModel"
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
        v-model.number="assetsModel"
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
        v-model.number="monthlyRentModel"
        type="number" 
        step="0.01" 
        placeholder="z.B. 800" 
        class="m-textfield" 
      />
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { FormDataField } from "@/types/EligibilityCheckInterface";

const props = defineProps<{
  grossMonthlyIncome?: number;
  netMonthlyIncome?: number;
  assets?: number;
  monthlyRent?: number;
  shouldShowField: (field: FormDataField) => boolean;
}>();

const emit = defineEmits<{
  'update:grossMonthlyIncome': [value: number | undefined];
  'update:netMonthlyIncome': [value: number | undefined];
  'update:assets': [value: number | undefined];
  'update:monthlyRent': [value: number | undefined];
}>();

// Create computed properties with getters/setters for v-model
const grossMonthlyIncomeModel = computed({
  get: () => props.grossMonthlyIncome,
  set: (value) => emit('update:grossMonthlyIncome', value || undefined)
});

const netMonthlyIncomeModel = computed({
  get: () => props.netMonthlyIncome,
  set: (value) => emit('update:netMonthlyIncome', value || undefined)
});

const assetsModel = computed({
  get: () => props.assets,
  set: (value) => emit('update:assets', value || undefined)
});

const monthlyRentModel = computed({
  get: () => props.monthlyRent,
  set: (value) => emit('update:monthlyRent', value || undefined)
});
</script>

