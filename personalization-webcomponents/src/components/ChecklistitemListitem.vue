<template>
  <div style="display: flex; align-items: center">
    <checklist-checked-circle
      style="margin-right: 12px"
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
.item-title {
  font-family: "Open Sans", "sans-serif";
  font-size: 18px;
}
.item-title-checked {
  color: var(--mde-color-neutral-grey-x-light);
}
</style>
