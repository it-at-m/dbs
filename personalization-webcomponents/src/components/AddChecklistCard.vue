<template>
  <div class="add-card">
    <a :href="newChecklistUrl">
      <div class="add-card-content">
        <div class="add-card-header">
          <h3>{{ title }}</h3>
        </div>
        <slot name="content" />
        <div class="add-card-button">
          <muc-button
            class="add-card-muc-button"
            icon="arrow-right"
            variant="primary"
            tabindex="-1"
            >Checkliste erstellen</muc-button
          >
        </div>
      </div>
    </a>
  </div>
</template>

<script setup lang="ts">
import { MucButton } from "@muenchen/muc-patternlab-vue";

defineProps<{
  title: string;
  newChecklistUrl: string;
}>();

defineSlots<{
  /**
   * Content beneath the heading shown as text.
   */
  content(): unknown;
}>();
</script>

<style scoped>
.add-card {
  cursor: pointer;
  border: solid 1px var(--mde-color-neutral-beau-blue);
  border-bottom: solid 5px var(--mde-color-brand-mde-blue);
  transition: background-color ease-in 150ms;
  background-color: var(--mde-color-neutral-beau-blue-x-light);
}

.add-card a {
  text-decoration: none !important;
  color: var(--mde-color-neutral-grey) !important;
  display: block;
  height: 100%;
}

.add-card:hover {
  background-color: var(--mde-color-neutral-beau-blue-light);
}

.add-card:hover .add-card-muc-button {
  background-color: var(--mde-color-brand-mde-blue-dark);
}

.add-card-content {
  text-align: center;
  padding: 32px 24px;
}

.add-card-header {
  margin-bottom: 16px;
}

.add-card-button {
  margin-top: 24px;
}
</style>
