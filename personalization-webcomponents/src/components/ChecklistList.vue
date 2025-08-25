<template>
  <div class="container">
    <sortable
      :list="modelValue"
      tag="ul"
      class="list"
      :animation="200"
      :handle="isDraggable ? '.drag-handle' : undefined"
      :disabled="!isDraggable"
      @start="drag = true"
      @end="drag = false"
      :ghost-class="'drag-ghost'"
      item-key="serviceID"
    >
      <template #item="{ element }">
        <li
          class="list-item"
          :class="{ muted: element.checked !== null }"
        >
          <input
            type="checkbox"
            :id="'cb-' + element.serviceID"
            class="radio-look"
            :checked="element.checked !== null"
            :disabled="disabled"
            @change="() => onSelectChange(element.serviceID)"
          />
          <span
            class="label-text"
            @click.prevent="openDialog(element)"
            style="cursor: pointer"
          >
            <b>{{ element.title }}</b>
          </span>

          <!-- Drag-Handle Icon -->
          <span
            v-if="isDraggable"
            class="drag-handle"
            title="Element verschieben"
          >
            <muc-icon icon="drag-vertical" />
          </span>
        </li>
      </template>
    </sortable>

    <!--todo-->
    <!-- Placeholder simple dialog box (modal)-->
    <div
      v-if="dialogVisible"
      class="modal-overlay"
      @click.self="closeDialog"
    >
      <div class="modal-content">
        <h3>Information zu "{{ dialogItem?.title }}"</h3>
        <p>Hier kannst du beliebige Inhalte anzeigen.</p>
        <button @click="closeDialog">Schließen</button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type DummyChecklistItem from "@/api/dummyservice/DummyChecklistItem.ts";

import { MucIcon } from "@muenchen/muc-patternlab-vue";
import { Sortable } from "sortablejs-vue3";
import { defineEmits, ref } from "vue";

withDefaults(
  defineProps<{
    modelValue: DummyChecklistItem[];
    isDraggable?: boolean;
    disabled?: boolean;
  }>(),
  {
    isDraggable: true,
    disabled: false,
  }
);
const emit = defineEmits(["checked", "label-click"]);
const drag = ref(false);

const dialogVisible = ref(false);
const dialogItem = ref<DummyChecklistItem | null>(null);

function onSelectChange(serviceID: string) {
  emit("checked", serviceID);
}

function openDialog(item: DummyChecklistItem) {
  dialogItem.value = item;
  dialogVisible.value = true;
  emit("label-click", item);
}

function closeDialog() {
  dialogVisible.value = false;
}
</script>

<style scoped>
.drag-ghost {
  background-color: #e1f0fc !important;
  box-shadow: 0 2px 8px #007acc30;
}
.container {
  max-width: 600px;
  margin: 1rem auto;
  padding-left: 0;
}

.list {
  list-style: none;
  padding: 0;
  margin: 0;
  border-top: 1px solid #ddd;
}

.list-item {
  display: flex;
  align-items: center;
  padding: 0.5rem 1rem;
  border-bottom: 1px solid #ddd;
  user-select: none;
  cursor: grab;
  color: var(--color-brand-main-blue);
}

.list-item:last-child {
  border-bottom: none;
}

/* text grayed out when selected */
.muted {
  color: #7a8d9f;
}

.radio-look {
  appearance: none;
  -webkit-appearance: none;
  flex: 0 0 16px;
  width: 16px;
  height: 16px;
  border: 2px solid var(--color-neutrals-grey);
  border-radius: 50%;
  background: white;
  box-sizing: border-box;
  margin-right: 0.8rem;
  position: relative;
  cursor: pointer;
  outline-offset: 2px;
  transition:
    border-color 0.2s ease,
    background-color 0.2s ease;
}

.radio-look:hover {
  border-color: var(--color-brand-main-blue);
  background-color: #cce4ff;
}

/* blue circle inside on hover (slightly transparent) */
.radio-look:hover::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 10px;
  height: 10px;
  background-color: var(--color-brand-main-blue);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  opacity: 0.3;
  pointer-events: none;
  transition: opacity 0.2s ease;
}

/* blue circle with white tick when selected */
.radio-look:checked {
  border-color: var(--color-brand-main-blue);
  background-color: var(--color-brand-main-blue);
}

.radio-look:checked::before {
  content: "✓";
  position: absolute;
  top: 50%;
  left: 50%;
  color: white;
  font-weight: bold;
  font-size: 14px;
  line-height: 1;
  transform: translate(-50%, -45%);
  pointer-events: none;
  user-select: none;
  transition: color 0.2s ease;
}

.radio-look:checked:hover::before {
  opacity: 0.8;
}

.label-text {
  flex-grow: 1;
  user-select: none;
}

.drag-handle {
  cursor: grab;
  user-select: none;
  font-size: 24px;
  margin-left: auto;
  color: var(--color-neutrals-grey);
  display: flex;
  align-items: center;
}

.drag-handle:active {
  cursor: grabbing;
  color: var(--color-brand-main-blue);
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 1.5rem 2rem;
  border-radius: 8px;
  max-width: 90%;
  max-height: 80%;
  overflow-y: auto;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}
</style>
