<template>
  <fieldset class="form-fieldset">
    <legend>Versicherung & Leistungen</legend>
    
    <div v-if="shouldShowField('healthInsurance')" class="m-form-group">
      <label for="healthInsurance" class="m-label">Krankenversicherung</label>
      <select 
        id="healthInsurance" 
        :value="healthInsurance" 
        @change="$emit('update:healthInsurance', ($event.target as HTMLSelectElement).value || undefined)"
        class="m-textfield"
      >
        <option :value="undefined">Bitte wählen</option>
        <option value="gesetzlich">Gesetzlich</option>
        <option value="privat">Privat</option>
        <option value="keine">Keine</option>
      </select>
    </div>
    
    <div v-if="shouldShowField('hasCareInsurance')">
      <YesNoInput
        :model-value="hasCareInsurance"
        @update:model-value="$emit('update:hasCareInsurance', $event)"
        label="Habe Pflegeversicherung"
        name="hasCareInsurance"
      />
    </div>
    
    <div v-if="shouldShowField('receivesUnemploymentBenefit1')">
      <YesNoInput
        :model-value="receivesUnemploymentBenefit1"
        @update:model-value="$emit('update:receivesUnemploymentBenefit1', $event)"
        label="Beziehe Arbeitslosengeld I"
        name="receivesUnemploymentBenefit1"
      />
    </div>
    
    <div v-if="shouldShowField('receivesUnemploymentBenefit2')">
      <YesNoInput
        :model-value="receivesUnemploymentBenefit2"
        @update:model-value="$emit('update:receivesUnemploymentBenefit2', $event)"
        label="Beziehe Bürgergeld (ALG II)"
        name="receivesUnemploymentBenefit2"
      />
    </div>
    
    <div v-if="shouldShowField('receivesPension')">
      <YesNoInput
        :model-value="receivesPension"
        @update:model-value="$emit('update:receivesPension', $event)"
        label="Beziehe Rente"
        name="receivesPension"
      />
    </div>
    
    <div v-if="shouldShowField('receivesChildBenefit')">
      <YesNoInput
        :model-value="receivesChildBenefit"
        @update:model-value="$emit('update:receivesChildBenefit', $event)"
        label="Beziehe Kindergeld"
        name="receivesChildBenefit"
      />
    </div>
    
    <div v-if="shouldShowField('receivesHousingBenefit')">
      <YesNoInput
        :model-value="receivesHousingBenefit"
        @update:model-value="$emit('update:receivesHousingBenefit', $event)"
        label="Beziehe Wohngeld"
        name="receivesHousingBenefit"
      />
    </div>
    
    <div v-if="shouldShowField('receivesStudentAid')">
      <YesNoInput
        :model-value="receivesStudentAid"
        @update:model-value="$emit('update:receivesStudentAid', $event)"
        label="Beziehe BAföG"
        name="receivesStudentAid"
      />
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import type { FormDataField } from "@/types/EligibilityCheckInterface";
import YesNoInput from "@/components/YesNoInput.vue";

defineProps<{
  healthInsurance?: 'gesetzlich' | 'privat' | 'keine';
  hasCareInsurance?: boolean;
  receivesUnemploymentBenefit1?: boolean;
  receivesUnemploymentBenefit2?: boolean;
  receivesPension?: boolean;
  receivesChildBenefit?: boolean;
  receivesHousingBenefit?: boolean;
  receivesStudentAid?: boolean;
  shouldShowField: (field: FormDataField) => boolean;
}>();

defineEmits<{
  'update:healthInsurance': [value: 'gesetzlich' | 'privat' | 'keine' | undefined];
  'update:hasCareInsurance': [value: boolean | undefined];
  'update:receivesUnemploymentBenefit1': [value: boolean | undefined];
  'update:receivesUnemploymentBenefit2': [value: boolean | undefined];
  'update:receivesPension': [value: boolean | undefined];
  'update:receivesChildBenefit': [value: boolean | undefined];
  'update:receivesHousingBenefit': [value: boolean | undefined];
  'update:receivesStudentAid': [value: boolean | undefined];
}>();
</script>
