<template>
  <div class="container">
    <draggable
        v-model="list"
        tag="ul"
        class="list"
        :animation="200"
        :handle="isDraggable ? '.drag-handle' : undefined"
        :disabled="!isDraggable"
        @start="drag = true"
        @end="drag = false"
        item-key="serviceID"
    >
      <template #item="{ element}">
        <li class="list-item"
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
          <label
              class="label-text"
              @click.prevent="openDialog(element)"
              style="cursor: pointer;"
          >
            {{ element.title }}
          </label>

          <!-- Drag-Handle Icon -->
          <span v-if="isDraggable" class="drag-handle" title="Element verschieben">
            <muc-icon icon="drag-vertical" />
          </span>
        </li>
      </template>
    </draggable>

    <!--todo-->
    <!-- Platzhalter Einfaches Dialogfenster (Modal)-->
    <div v-if="dialogVisible" class="modal-overlay" @click.self="closeDialog">
      <div class="modal-content">
        <h3>Information zu "{{ dialogItem?.title }}"</h3>
        <p>Hier kannst du beliebige Inhalte anzeigen.</p>
        <button @click="closeDialog">Schließen</button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits,watch } from "vue";
import draggable from "vuedraggable";
import {MucIcon} from "@muenchen/muc-patternlab-vue";
import type DummyChecklistItem from "@/api/dummyservice/DummyChecklistItem.ts";


const props = withDefaults(defineProps<{
  modelValue: DummyChecklistItem[];
  isDraggable?: boolean;
  disabled?: boolean; // Optional, damit Default greifen kann
}>(), {
  isDraggable: true,
  disabled: false
});
const emit = defineEmits(["checked", "update:modelValue", "label-click"]);
const list = ref<DummyChecklistItem[]>([...props.modelValue]);
const drag = ref(false);

const dialogVisible = ref(false);
const dialogItem = ref<DummyChecklistItem | null>(null);


watch(() => props.modelValue, (newVal) => {
  list.value = [...newVal];
});

watch(list, (newVal) => {
  emit('update:modelValue', [...newVal]);
}, { deep: true });


function onSelectChange(serviceID: string) {
  emit('checked', serviceID);
}

function openDialog(item: DummyChecklistItem) {
  dialogItem.value = item;
  dialogVisible.value = true;
  emit('label-click', item);
}

function closeDialog() {
  dialogVisible.value = false;
}
</script>

<style scoped>
.container {
  max-width: 600px;
  margin: 1rem auto;
  font-family: Arial, sans-serif;
}

.list {
  list-style: none;
  padding: 0;
  margin: 0;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.list-item {
  display: flex;
  align-items: center;
  padding: 0.5rem 1rem;
  border-bottom: 1px solid #ddd;
  user-select: none;
  cursor: grab;
}

.list-item:last-child {
  border-bottom: none;
}

/* Text ausgegraut, wenn ausgewählt */
.muted {
  color: #999;
}

/* Checkbox im Radio-Look mit weißem Haken auf blauem Kreis */
.radio-look {
  appearance: none;
  -webkit-appearance: none;
  width: 16px;
  height: 16px;
  border: 2px solid #007acc;
  border-radius: 50%;
  background: white;
  box-sizing: border-box;
  margin-right: 0.8rem;
  position: relative;
  cursor: pointer;
  outline-offset: 2px;
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.radio-look:hover {
  border-color: #005fa3;
  background-color: #cce4ff;
}

/* Blauer Kreis im Innern beim Hover (leicht transparent) */
.radio-look:hover::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 10px;
  height: 10px;
  background-color: #007acc;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  opacity: 0.3;
  pointer-events: none;
  transition: opacity 0.2s ease;
}

/* Blauer Kreis mit weißem Haken beim Ausgewählt */
.radio-look:checked {
  border-color: #007acc;
  background-color: #007acc;
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
  transform: translate(-50%, -55%);
  pointer-events: none;
  user-select: none;
  transition: color 0.2s ease;
}

/* Optional Haken leicht transparent beim Hover des Ausgewählten */
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
  color: #666;
  display: flex;
  align-items: center;
}

.drag-handle:active {
  cursor: grabbing;
  color: #007acc;
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