<template>
  <main>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite"/>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite"/>
    <h1>Aktive Checklisten ({{ checklists.length }})</h1>
    <muc-card-container
        style="grid-template-columns: repeat(auto-fit,589px)"
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
import {MucCardContainer, MucCard} from "@muenchen/muc-patternlab-vue";
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";
import DummyChecklistService from "@/api/dummyservice/DummyChecklistService.ts";
import ChecklistCard from "@/components/ChecklistCard.vue";
import SkeletonLoader from "@/components/common/skeleton-loader.vue";

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
