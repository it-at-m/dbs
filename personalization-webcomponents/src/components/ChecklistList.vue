<template>
  <div class="container">
    <draggable
        v-model="list"
        tag="ul"
        class="list"
        :animation="200"
        handle=".drag-handle"
        @start="drag = true"
        @end="drag = false"
    >
      <template #item="{ element}">
        <li class="list-item"
            :class="{ muted: isSelected(element.serviceID) }"
            :key="element.serviceID"
        >
          <input
              type="checkbox"
              :id="'cb-' + element.serviceID"
              class="radio-look"
              :value="element.serviceID"
              v-model="selected"
              @change="onSelectChange"
          />
          <label
              class="label-text"
              @click.prevent="openDialog(element)"
              style="cursor: pointer;"
          >
            {{ element.title }}
          </label>

          <!-- Drag-Handle Icon -->
          <span class="drag-handle" title="Element verschieben">
            <muc-icon icon="drag-vertical" />
          </span>
        </li>
      </template>
    </draggable>

    <pre>Ausgewählt: {{ selected }}</pre>

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


const props = defineProps<{
  modelValue: DummyChecklistItem[]
}>();

const emit = defineEmits(["checked", "update:modelValue", "label-click"]);
const list = ref<DummyChecklistItem[]>([...props.modelValue]);
const selected = ref<string[]>([]);
const drag = ref(false);

const dialogVisible = ref(false);
const dialogItem = ref<DummyChecklistItem | null>(null);


watch(() => props.modelValue, (newVal) => {
  list.value = [...newVal];
});

watch(list, (newVal) => {
  emit('update:modelValue', [...newVal]);
}, { deep: true });

function isSelected(serviceID: string) {
  return selected.value.includes(serviceID);
}

function onSelectChange() {
  emit('checked', [...selected.value]);
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

/* Checkbox im Radio-Look */
.radio-look {
  appearance: none;
  -webkit-appearance: none;
  width: 16px;
  height: 16px;
  border: 2px solid #007acc;
  border-radius: 50%;
  margin-right: 0.8rem;
  position: relative;
  cursor: pointer;
  outline-offset: 2px;
  transition: border-color 0.2s ease;
}

.radio-look:hover {
  border-color: #005fa3;
}

.radio-look:checked::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 8px;
  height: 8px;
  background-color: #007acc;
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.radio-look:hover::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 8px;
  height: 8px;
  background-color: #007acc;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  opacity: 0.5;
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