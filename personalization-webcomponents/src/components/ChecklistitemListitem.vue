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
import type DummyChecklistItem from "@/api/dummyservice/DummyChecklistItem.ts";

import ChecklistCheckedCircle from "@/components/ChecklistCheckedCircle.vue";

defineProps<{
  checklistItem: DummyChecklistItem;
}>();
</script>

<style scoped>
.item-title {
  font-family: "Open Sans", "sans-serif";
  font-size: 18px;
}
.item-title-checked {
  color: #7a8d9f;
}
</style>
