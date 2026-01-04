<template>
  <fieldset class="form-fieldset">
    <legend>Haushalt</legend>
    
    <div v-if="shouldShowField('householdSize')" class="m-form-group">
      <label for="householdSize" class="m-label">Anzahl Personen im Haushalt</label>
      <input 
        id="householdSize" 
        v-model.number="householdSizeModel"
        type="number" 
        min="1" 
        placeholder="z.B. 2" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('numberOfChildren')" class="m-form-group">
      <label for="numberOfChildren" class="m-label">Anzahl Kinder</label>
      <input 
        id="numberOfChildren" 
        v-model.number="numberOfChildrenModel"
        type="number" 
        min="0" 
        placeholder="z.B. 1" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('childrenAges')" class="m-form-group">
      <label for="childrenAges" class="m-label">Alter der Kinder (kommagetrennt)</label>
      <input 
        id="childrenAges" 
        v-model="childrenAgesModel"
        type="text" 
        placeholder="z.B. 5, 8, 12" 
        class="m-textfield" 
      />
    </div>
    
    <div v-if="shouldShowField('isSingleParent')">
      <YesNoInput
        v-model="isSingleParentModel"
        label="Alleinerziehend"
        name="isSingleParent"
      />
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { FormDataField } from "@/types/EligibilityCheckInterface";
import YesNoInput from "@/components/YesNoInput.vue";

const props = defineProps<{
  householdSize?: number;
  numberOfChildren?: number;
  childrenAges?: number[];
  isSingleParent?: boolean;
  shouldShowField: (field: FormDataField) => boolean;
}>();

const emit = defineEmits<{
  'update:householdSize': [value: number | undefined];
  'update:numberOfChildren': [value: number | undefined];
  'update:childrenAges': [value: number[] | undefined];
  'update:isSingleParent': [value: boolean | undefined];
}>();

const parseAgesArray = (value: string): number[] | undefined => {
  if (!value || value.trim() === '') return undefined;
  const ages = value.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
  return ages.length > 0 ? ages : undefined;
};

// Create computed properties with getters/setters for v-model
const householdSizeModel = computed({
  get: () => props.householdSize,
  set: (value) => emit('update:householdSize', value || undefined)
});

const numberOfChildrenModel = computed({
  get: () => props.numberOfChildren,
  set: (value) => emit('update:numberOfChildren', value || undefined)
});

const childrenAgesModel = computed({
  get: () => props.childrenAges?.join(', ') || '',
  set: (value) => emit('update:childrenAges', parseAgesArray(value))
});

const isSingleParentModel = computed({
  get: () => props.isSingleParent,
  set: (value) => emit('update:isSingleParent', value)
});
</script>

