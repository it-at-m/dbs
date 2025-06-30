<template>
  <main class="container">
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite"/>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite"/>
    <h2
      style="display: flex; align-items: center; margin-bottom: 24px;"
    >
      <muc-icon
          style="width: 32px; height: 32px; margin-right: 8px;"
          icon="order-bool-ascending"
      ></muc-icon>
      Aktive Checklisten ({{ checklists.length }})
    </h2>
    <muc-card-container
        class="checklist-card-container"
    >
      <skeleton-loader
          v-for="() in [1,2,3,4]"
          v-if="loading"
      >
      </skeleton-loader>
      <checklist-card
          v-else
          v-for="(checklist, index) in checklists"
          :key="index"
          :checklist="checklist">
      </checklist-card>
    </muc-card-container>
  </main>
</template>

<script setup lang="ts">
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import {onMounted, ref} from "vue";
import {MucCardContainer, MucIcon} from "@muenchen/muc-patternlab-vue";
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";
import DummyChecklistService from "@/api/dummyservice/DummyChecklistService.ts";
import ChecklistCard from "@/components/ChecklistCard.vue";
import SkeletonLoader from "@/components/common/skeleton-loader.vue";

const props = defineProps<{
  checklistDetailUrl: string,
  newChecklistUrl: string
}>();

const checklists = ref<DummyChecklist[]>([]);
const loading = ref(false);

onMounted(() => {
  loading.value = true;
  const dcl = new DummyChecklistService();
  dcl.getChecklists()
      .then((checklist) => {
        checklists.value = checklist;
      })
      .finally(() => loading.value = false);
})

</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>

<style scoped>
.checklist-card-container {
  grid-template-columns: repeat(auto-fit, 100%);
}

@media (min-width: 768px) {
  .checklist-card-container {
    grid-template-columns: repeat(auto-fit,589px);
  }
}

</style>
