<template>
  <fieldset class="form-fieldset">
    <legend>Versicherung & Leistungen</legend>
    
    <div v-if="shouldShowField('healthInsurance')" class="m-form-group">
      <label for="healthInsurance" class="m-label">Krankenversicherung</label>
      <select 
        id="healthInsurance" 
        v-model="healthInsuranceModel"
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
        v-model="hasCareInsuranceModel"
        label="Habe Pflegeversicherung"
        name="hasCareInsurance"
      />
    </div>
    
    <div v-if="shouldShowField('receivesUnemploymentBenefit1')">
      <YesNoInput
        v-model="receivesUnemploymentBenefit1Model"
        label="Beziehe Arbeitslosengeld I"
        name="receivesUnemploymentBenefit1"
      />
    </div>
    
    <div v-if="shouldShowField('receivesUnemploymentBenefit2')">
      <YesNoInput
        v-model="receivesUnemploymentBenefit2Model"
        label="Beziehe Bürgergeld (ALG II)"
        name="receivesUnemploymentBenefit2"
      />
    </div>
    
    <div v-if="shouldShowField('receivesPension')">
      <YesNoInput
        v-model="receivesPensionModel"
        label="Beziehe Rente"
        name="receivesPension"
      />
    </div>
    
    <div v-if="shouldShowField('receivesChildBenefit')">
      <YesNoInput
        v-model="receivesChildBenefitModel"
        label="Beziehe Kindergeld"
        name="receivesChildBenefit"
      />
    </div>
    
    <div v-if="shouldShowField('receivesHousingBenefit')">
      <YesNoInput
        v-model="receivesHousingBenefitModel"
        label="Beziehe Wohngeld"
        name="receivesHousingBenefit"
      />
    </div>
    
    <div v-if="shouldShowField('receivesStudentAid')">
      <YesNoInput
        v-model="receivesStudentAidModel"
        label="Beziehe BAföG"
        name="receivesStudentAid"
      />
    </div>
  </fieldset>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { FormDataField } from "@/types/EligibilityCheckInterface";
import YesNoInput from "@/components/YesNoInput.vue";

const props = defineProps<{
  healthInsurance?: 'public' | 'private' | 'none';
  hasCareInsurance?: boolean;
  receivesUnemploymentBenefit1?: boolean;
  receivesUnemploymentBenefit2?: boolean;
  receivesPension?: boolean;
  receivesChildBenefit?: boolean;
  receivesHousingBenefit?: boolean;
  receivesStudentAid?: boolean;
  shouldShowField: (field: FormDataField) => boolean;
}>();

const emit = defineEmits<{
  'update:healthInsurance': [value: 'public' | 'private' | 'none' | undefined];
  'update:hasCareInsurance': [value: boolean | undefined];
  'update:receivesUnemploymentBenefit1': [value: boolean | undefined];
  'update:receivesUnemploymentBenefit2': [value: boolean | undefined];
  'update:receivesPension': [value: boolean | undefined];
  'update:receivesChildBenefit': [value: boolean | undefined];
  'update:receivesHousingBenefit': [value: boolean | undefined];
  'update:receivesStudentAid': [value: boolean | undefined];
}>();

const healthInsuranceModel = computed({
  get: () => props.healthInsurance,
  set: (value) => emit('update:healthInsurance', value || undefined)
});

const hasCareInsuranceModel = computed({
  get: () => props.hasCareInsurance,
  set: (value) => emit('update:hasCareInsurance', value)
});

const receivesUnemploymentBenefit1Model = computed({
  get: () => props.receivesUnemploymentBenefit1,
  set: (value) => emit('update:receivesUnemploymentBenefit1', value)
});

const receivesUnemploymentBenefit2Model = computed({
  get: () => props.receivesUnemploymentBenefit2,
  set: (value) => emit('update:receivesUnemploymentBenefit2', value)
});

const receivesPensionModel = computed({
  get: () => props.receivesPension,
  set: (value) => emit('update:receivesPension', value)
});

const receivesChildBenefitModel = computed({
  get: () => props.receivesChildBenefit,
  set: (value) => emit('update:receivesChildBenefit', value)
});

const receivesHousingBenefitModel = computed({
  get: () => props.receivesHousingBenefit,
  set: (value) => emit('update:receivesHousingBenefit', value)
});

const receivesStudentAidModel = computed({
  get: () => props.receivesStudentAid,
  set: (value) => emit('update:receivesStudentAid', value)
});
</script>
