<template>
  <main>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />
    <h1>Aktive Checklisten ({{ checklists.length }})</h1>
    <muc-card-container
      style="grid-template-columns: repeat(auto-fit,589px)"
    >
      <checklist-card
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
import {MucCardContainer} from "@muenchen/muc-patternlab-vue";
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";
import DummyChecklistService from "@/api/dummyservice/DummyChecklistService.ts";
import ChecklistCard from "@/components/ChecklistCard.vue";

const checklists = ref<DummyChecklist[]>([]);

onMounted(() => {
  const dcl = new DummyChecklistService();
  checklists.value = dcl.getChecklists();
})

</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>
