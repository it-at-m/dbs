<template>
  <div
    class="container"
    tabindex="0"
    role="list"
  >
    <sortable
      :list="modelValue"
      tag="ul"
      class="list"
      :options="sortableOptions"
      @start="drag = true"
      @end="drag = false"
      item-key="serviceID"
    >
      <template #item="{ element, index }">
        <li
          class="list-item"
          role="listitem"
          aria-roledescription="sortierbares Listenelement"
          :class="{
            muted: element.checked !== null,
            'keyboard-dragging': draggedIndex === index,
          }"
          :aria-grabbed="draggedIndex === index ? 'true' : 'false'"
          :aria-label="`${element.title}, Position ${index + 1} von ${modelValue.length}`"
          tabindex="0"
          @focus="focusedIndex = index"
          :key="element.serviceID"
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
          <span
            v-if="isDraggable"
            class="drag-handle"
            title="Element verschieben"
          >
            <template v-if="draggedIndex === index">
              <muc-icon icon="arrow-up-down" />
            </template>
            <template v-else>
              <muc-icon icon="drag-vertical" />
            </template>
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
import type ChecklistItem from "@/api/persservice/ChecklistItem.ts";

import { MucIcon } from "@muenchen/muc-patternlab-vue";
import { Sortable } from "sortablejs-vue3";
import { computed, onBeforeUnmount, onMounted, ref } from "vue";

const props = withDefaults(
  defineProps<{
    modelValue: ChecklistItem[];
    isDraggable?: boolean;
    disabled?: boolean;
  }>(),
  {
    isDraggable: true,
    disabled: false,
  }
);
const emit = defineEmits(["checked", "label-click", "update:modelValue"]);

const drag = ref(false);
const focusedIndex = ref<number | null>(null);
const draggedIndex = ref<number | null>(null);

const sortableOptions = computed(() => ({
  animation: 200,
  handle: props.isDraggable ? ".drag-handle" : undefined,
  ghostClass: "drag-ghost",
  disabled: !props.isDraggable,
}));

const dialogVisible = ref(false);
const dialogItem = ref<ChecklistItem | null>(null);

onMounted(() => {
  window.addEventListener("keydown", handleKeyDown);
});

onBeforeUnmount(() => {
  window.removeEventListener("keydown", handleKeyDown);
});

function onSelectChange(serviceID: string) {
  emit("checked", serviceID);
}

function openDialog(item: ChecklistItem) {
  dialogItem.value = item;
  dialogVisible.value = true;
  emit("label-click", item);
}

function closeDialog() {
  dialogVisible.value = false;
}

function handleKeyDown(event: KeyboardEvent) {
  if (!props.isDraggable || focusedIndex.value === null) return;

  const maxIndex = props.modelValue.length - 1;
  if (event.key === "Enter") {
    draggedIndex.value =
      draggedIndex.value === null ? focusedIndex.value : null;
    return;
  }
  if (draggedIndex.value === null) return;

  const move = (direction: number) => {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const newIndex = draggedIndex.value! + direction;
    if (newIndex < 0 || newIndex > maxIndex) return;

    event.preventDefault();

    const updatedList = swapItems(
      [...props.modelValue],
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      draggedIndex.value!,
      newIndex
    );
    emit("update:modelValue", updatedList);
    draggedIndex.value = newIndex;
    focusedIndex.value = newIndex;
  };

  if (event.key === "ArrowUp") move(-1);
  if (event.key === "ArrowDown") move(1);
}

function swapItems(
  array: DummyChecklistItem[],
  i: number,
  j: number
): DummyChecklistItem[] {
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const temp = array[i]!;
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  array[i] = array[j]!;
  array[j] = temp;
  return array;
}
</script>

<style scoped>
.drag-ghost {
  background-color: #e1f0fc !important;
  box-shadow: 0 2px 8px #007acc30;
}

.keyboard-dragging {
  outline: 2px solid var(--color-brand-main-blue);
  background-color: #d0e7ff;
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
  color: #617586;
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
