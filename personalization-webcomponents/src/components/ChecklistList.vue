<template>
  <div
    class="container"
    tabindex="0"
    role="list"
  >
    <sortable
      :list="checklistItems"
      tag="ul"
      class="list"
      :options="sortableOptions"
      item-key="serviceID"
      @end="onSortEnd"
    >
      <template #item="{ element, index }">
        <li
          class="list-item"
          role="listitem"
          aria-roledescription="sortierbares Listenelement"
          :class="{
            'keyboard-dragging': draggedIndex === index,
          }"
          :aria-grabbed="draggedIndex === index ? 'true' : 'false'"
          :aria-label="`${element.title}, Position ${index + 1} von ${checklistItems.length}`"
          tabindex="0"
          @focus="focusedIndex = index"
          :key="element.serviceID"
          @keydown="handleEnterKeyDown"
        >
          <p13n-checkbox
            :id="'cb-' + element.serviceID"
            :aria-label="!!element.checked ? element.title + ' als nicht erledigt markieren.' : element.title + ' als erledigt markieren.'"
            :checked="!!element.checked"
            :disabled="disabled"
            style="margin-left: 8px;"
            @check="() => onSelectChange(element.serviceID)"
          />
          <span
            tabindex="0"
            class="label-text"
            :class="{
              muted: element.checked !== null,
            }"
            @click="(evt) => openDialog(element, evt)"
            @keydown="(evt) => evt.keyCode == 32 ? openDialog(element, evt) : null"
          >
            <b>{{ element.title }}</b>
            <span
                class="required-label"
                v-if="element.required"
            >
              - verpflichtend
            </span>
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
import P13nCheckbox from "@/components/P13nCheckbox.vue";

const props = withDefaults(
  defineProps<{
    checklistItems: ChecklistItem[];
    isDraggable?: boolean;
    disabled?: boolean;
  }>(),
  {
    isDraggable: true,
    disabled: false,
  }
);
const emit = defineEmits(["checked", "label-click", "sort"]);

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
  window.addEventListener("keydown", handleArrowKeyDown);
});

onBeforeUnmount(() => {
  window.removeEventListener("keydown", handleArrowKeyDown);
});

function onSelectChange(serviceID: string) {
  emit("checked", serviceID);
}

function openDialog(item: ChecklistItem, evt: Event) {
  evt.preventDefault();
  dialogItem.value = item;
  dialogVisible.value = true;
  emit("label-click", item);
}

function closeDialog() {
  dialogVisible.value = false;
}

function onSortEnd(evt: { oldIndex: number; newIndex: number }) {
  const oldIndex = evt.oldIndex;
  const newIndex = evt.newIndex;
  if (oldIndex !== newIndex) {
    emit("sort", { oldIndex, newIndex });
  }
}

function handleEnterKeyDown(event: KeyboardEvent) {
  if (!props.isDraggable || focusedIndex.value === null) return;

  if (event.key === "Enter") {
    draggedIndex.value =
      draggedIndex.value === null ? focusedIndex.value : null;
  }
}

function handleArrowKeyDown(event: KeyboardEvent) {
  if (!props.isDraggable || focusedIndex.value === null) return;
  if (draggedIndex.value === null) return;

  const maxIndex = props.checklistItems.length - 1;
  const move = (direction: number) => {
    if (draggedIndex.value) {
      const newIndex = draggedIndex.value + direction;
      if (newIndex < 0 || newIndex > maxIndex) return;

      event.preventDefault();

      emit("sort", { oldIndex: draggedIndex.value, newIndex });

      draggedIndex.value = newIndex;
      focusedIndex.value = newIndex;
    }
  };

  if (event.key === "ArrowUp") move(-1);
  if (event.key === "ArrowDown") move(1);
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

.list {
  list-style: none;
  padding: 0;
  margin: 0;
  border-top: 1px solid var(--color-neutrals-beau-blue-light, #E5EEF5);
  border-bottom: 1px solid var(--color-neutrals-beau-blue-light, #E5EEF5);
}

.container {
  padding-left: 0;
  padding-right: 0;
  padding-bottom: 56px;
}

.list-item {
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--color-neutrals-beau-blue-light, #E5EEF5);
  user-select: none;
  cursor: grab;
  color: var(--color-brand-main-blue);
}

.list-item:last-child {
  border-bottom: none;
}

/* text grayed out when selected */
.muted {
  color: #7a8d9f !important;
}

.label-text {
  cursor: pointer;
  color: var(--color-brand-main-blue);
  /* Body/Body 1 Bold */
  font-family: "Open Sans", sans-serif;
  font-size: 18px;
  font-style: normal;
  font-weight: 700;
  line-height: 150%; /* 27px */

  padding: 16px 8px;
  flex-grow: 1;
  user-select: none;
}

.label-text:hover {
  text-decoration: underline;
}

@media (max-width: 576px) {
  .label-text {
    padding: 12px 8px;
  }
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

.required-label {
  color: var(--neutrals-grey, #3A5368);
  /* Body/Body 2 */
  font-family: "Open Sans", sans-serif;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: 150%;
}
</style>
