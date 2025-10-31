<template>
  <div class="checklist-container">
    <checklist-checked-circle
      class="checklist-check-circle"
      :checked="!!checklistItem.checked"
    >
    </checklist-checked-circle>
    <div
      class="item-title"
      :class="{ 'item-title-checked': checklistItem.checked }"
    >
      {{ checklistItem.title }}
    </div>
  </div>
</template>

<script setup lang="ts">
import type ChecklistItem from "@/api/persservice/ChecklistItem.ts";

import ChecklistCheckedCircle from "@/components/ChecklistCheckedCircle.vue";

defineProps<{
  checklistItem: ChecklistItem;
}>();
</script>

<style scoped>
.checklist-container {
  display: flex;
  align-items: flex-start;
}

.checklist-check-circle {
  margin-top: 3px;
  margin-right: 12px;
}

.item-title {
  font-family: "Open Sans", sans-serif;
  font-size: 1rem;
}

.item-title-checked {
  color: var(--mde-color-neutral-grey-x-light);
}
</style>
