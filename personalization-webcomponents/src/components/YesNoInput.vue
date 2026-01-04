<template>
  <div class="yes-no-input">
    <label v-if="label" class="m-label">{{ label }}</label>
    <div class="yes-no-options">
      <label class="yes-no-option">
        <input
          type="radio"
          :name="name"
          :value="true"
          :checked="modelValue === true"
          @change="$emit('update:modelValue', true)"
          class="yes-no-radio"
        />
        <span class="yes-no-label">Ja</span>
      </label>
      <label class="yes-no-option">
        <input
          type="radio"
          :name="name"
          :value="false"
          :checked="modelValue === false"
          @change="$emit('update:modelValue', false)"
          class="yes-no-radio"
        />
        <span class="yes-no-label">Nein</span>
      </label>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  modelValue?: boolean;
  label?: string;
  name: string;
}>();

defineEmits<{
  'update:modelValue': [value: boolean | undefined];
}>();
</script>

<style scoped>
.yes-no-input {
  margin-bottom: 24px;
}

.m-label {
  display: block;
  margin-bottom: 12px;
  font-weight: 600;
  color: var(--mde-color-neutral-grey-dark);
  font-size: 1rem;
}

.yes-no-options {
  display: flex;
  gap: 24px;
  align-items: center;
}

.yes-no-option {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
  padding: 12px 20px;
  border: 2px solid var(--mde-color-neutral-grey-light);
  border-radius: 4px;
  transition: all 0.3s ease;
  background: white;
  min-width: 100px;
  justify-content: center;
}

.yes-no-option:hover {
  border-color: var(--mde-color-brand-mde-blue-light);
  background: var(--mde-color-neutral-beau-blue-x-light);
}

.yes-no-option:has(input:checked) {
  border-color: var(--mde-color-brand-mde-blue);
  background: var(--mde-color-brand-mde-blue);
  color: white;
}

.yes-no-option:has(input:checked) .yes-no-label {
  color: white;
  font-weight: 600;
}

.yes-no-radio {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}

.yes-no-label {
  font-size: 1rem;
  font-weight: 500;
  color: var(--mde-color-neutral-grey-dark);
  transition: color 0.3s ease;
}

@media (max-width: 768px) {
  .yes-no-options {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .yes-no-option {
    width: 100%;
  }
}
</style>

