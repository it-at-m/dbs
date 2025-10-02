<template>
  <muc-card-container
    v-if="allChecklists.length == 0"
    class="checklist-card-container"
  >
    <add-checklist-card
      class="mobile-card-height"
      title="Neue Checkliste"
      :new-checklist-url="newChecklistUrl"
    >
      <template #content>
        <icon-add-checklist />
      </template>
    </add-checklist-card>
  </muc-card-container>
  <div v-else-if="isMobile">
    <muc-slider
      v-if="allChecklists.length < 2"
      class="slider-content"
    >
      <muc-slider-item
        v-for="(checklist, index) in allChecklists"
        :key="index"
      >
        <checklist-card
          :checklist="checklist"
          :checklist-detail-url="checklistDetailUrl"
          :class="{ 'card-color': displayedOnDetailScreen }"
          class="mobile-card-height"
          tabindex="0"
        />
      </muc-slider-item>
      <muc-slider-item v-if="!displayedOnDetailScreen">
        <add-checklist-card
          class="mobile-card-height"
          title="Neue Checkliste"
          :new-checklist-url="newChecklistUrl"
        >
          <template #content>
            <icon-add-checklist />
          </template>
        </add-checklist-card>
      </muc-slider-item>
    </muc-slider>
    <muc-slider
      v-else
      class="slider-content"
    >
      <muc-slider-item
        v-for="(checklist, index) in allChecklists.slice(0, 2)"
        :key="index"
      >
        <checklist-card
          :checklist="checklist"
          :checklist-detail-url="checklistDetailUrl"
          :class="{ 'card-color': displayedOnDetailScreen }"
          class="mobile-card-height"
          tabindex="0"
        />
      </muc-slider-item>
    </muc-slider>
  </div>
  <div v-else>
    <muc-card-container
      v-if="allChecklists.length < 2"
      class="checklist-card-container"
    >
      <checklist-card
        v-for="(checklist, index) in allChecklists"
        :key="index"
        :checklist="checklist"
        :checklist-detail-url="checklistDetailUrl"
        :class="{ 'card-color': displayedOnDetailScreen }"
        class="mobile-card-height"
        tabindex="0"
      />
      <add-checklist-card
        v-if="!displayedOnDetailScreen"
        class="mobile-card-height"
        title="Neue Checkliste"
        :new-checklist-url="newChecklistUrl"
      >
        <template #content>
          <icon-add-checklist />
        </template>
      </add-checklist-card>
    </muc-card-container>
    <muc-card-container
      v-else
      class="checklist-card-container"
    >
      <checklist-card
        v-for="(checklist, index) in allChecklists.slice(0, 2)"
        :key="index"
        :checklist="checklist"
        :checklist-detail-url="checklistDetailUrl"
        :class="{ 'card-color': displayedOnDetailScreen }"
        class="mobile-card-height"
        tabindex="0"
      />
    </muc-card-container>
  </div>
</template>

<script setup lang="ts">
import type Checklist from "@/api/persservice/Checklist.ts";

import {
  MucCardContainer,
  MucSlider,
  MucSliderItem,
} from "@muenchen/muc-patternlab-vue";

import AddChecklistCard from "@/components/AddChecklistCard.vue";
import ChecklistCard from "@/components/ChecklistCard.vue";
import IconAddChecklist from "@/components/icons/IconAddChecklist.vue";

defineProps<{
  allChecklists: Checklist[];
  isMobile: boolean;
  newChecklistUrl: string;
  checklistDetailUrl: string;
  displayedOnDetailScreen: boolean;
}>();
</script>

<style scoped>
/* No extra padding in MucSlider */
.m-component {
  padding: 0 !important;
}

/* Content of the slider extends to the edge of the screen */
.slider-content {
  margin-left: -1.5rem;
  margin-right: -1.5rem;
}

/* Background color of the cards */
.card-color {
  background-color: white !important;
}

/* Height of the cards in the mobile view, so that all cards have the same height */
.mobile-card-height {
  height: 100%;
}

.card:hover {
  background-color: var(--mde-color-neutral-beau-blue-x-light) !important;
}

.checklist-card-container {
  grid-template-columns: repeat(auto-fit, 100%) !important;
}

@media (min-width: 768px) {
  .checklist-card-container {
    grid-template-columns: repeat(auto-fit, 589px) !important;
  }
}
</style>
