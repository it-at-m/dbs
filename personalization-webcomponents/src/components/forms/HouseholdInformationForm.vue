<template>
  <fieldset class="form-fieldset">
    <legend>Haushalt</legend>
    
    <div v-if="shouldShowField('householdSize')" class="m-form-group">
      <label for="householdSize" class="m-label">Anzahl Personen im Haushalt</label>
      <input 
        id="householdSize" 
        :value="householdSize" 
        @input="$emit('update:householdSize', parseNumberOrUndefined(($event.target as HTMLInputElement).value))"
        type="number" 
        min="1" 
        placeholder="z.B. 2" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('numberOfChildren') && householdSize && householdSize > 1" class="m-form-group">
      <label for="numberOfChildren" class="m-label">Anzahl Kinder</label>
      <input 
        id="numberOfChildren" 
        :value="numberOfChildren" 
        @input="$emit('update:numberOfChildren', parseNumberOrUndefined(($event.target as HTMLInputElement).value))"
        type="number" 
        min="0" 
        placeholder="z.B. 1" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('childrenAges') && numberOfChildren && numberOfChildren > 0" class="m-form-group">
      <label for="childrenAges" class="m-label">Alter der Kinder (kommagetrennt)</label>
      <input 
        id="childrenAges" 
        :value="childrenAges?.join(', ')" 
        @input="$emit('update:childrenAges', parseAgesArray(($event.target as HTMLInputElement).value))"
        type="text" 
        placeholder="z.B. 5, 8, 12" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('isSingleParent')">
      <YesNoInput
        :model-value="isSingleParent"
        @update:model-value="$emit('update:isSingleParent', $event)"
        label="Alleinerziehend"
        name="isSingleParent"
      />
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import type { FormDataField } from "@/types/EligibilityCheckInterface";
import YesNoInput from "@/components/YesNoInput.vue";

defineProps<{
  householdSize?: number;
  numberOfChildren?: number;
  childrenAges?: number[];
  isSingleParent?: boolean;
  shouldShowField: (field: FormDataField) => boolean;
}>();

defineEmits<{
  'update:householdSize': [value: number | undefined];
  'update:numberOfChildren': [value: number | undefined];
  'update:childrenAges': [value: number[] | undefined];
  'update:isSingleParent': [value: boolean | undefined];
}>();

const parseNumberOrUndefined = (value: string): number | undefined => {
  const parsed = parseFloat(value);
  return isNaN(parsed) ? undefined : parsed;
};

const parseAgesArray = (value: string): number[] | undefined => {
  if (!value || value.trim() === '') return undefined;
  const ages = value.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
  return ages.length > 0 ? ages : undefined;
};
</script>

