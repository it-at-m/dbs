<template>
  <div style="display: flex; align-items: center">
    <checklist-checked-circle
      style="margin-right: 12px"
      :checked="!!checklistItem.checked"
    >
    </checklist-checked-circle>
    <div class="item-title">
      {{ checklistItem.title }}
    </div>
  </div>
</template>

<script setup lang="ts">
import type DummyChecklistItem from "@/api/dummyservice/DummyChecklistItem.ts";

import ChecklistCheckedCircle from "@/components/checklist-checked-circle.vue";

defineProps<{
  checklistItem: DummyChecklistItem;
}>();
</script>

<style scoped>
.item-title {
  font-family: "Open Sans", "sans-serif";
  font-size: 18px;
}
</style>
