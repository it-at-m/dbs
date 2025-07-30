<template>
  <div class="container">
    <button class="button" @click="sort">Zur Originalreihenfolge</button>

    <draggable
        v-model="list"
        item-key="order"
        tag="ul"
        class="list"
        :animation="200"
        @start="drag = true"
        @end="drag = false"
    >
      <template #item="{ element }">
        <li
            class="list-item"
            :class="{ muted: isSelected(element.order) }"
        >
          <input
              type="checkbox"
              :id="'cb-' + element.order"
              class="radio-look"
              :value="element.order"
              v-model="selected"
          />
          <label :for="'cb-' + element.order" class="label-text">
            {{ element.name }}
          </label>
        </li>
      </template>
    </draggable>

    <pre>Ausgewählt: {{ selected }}</pre>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import draggable from "vuedraggable";

const message = [
  "vue.draggable",
  "draggable",
  "component",
  "for",
  "vue.js 2.0",
  "based",
  "on",
  "Sortablejs",
];

const list = ref(
    message.map((name, index) => ({
      name,
      order: index + 1,
    }))
);

const selected = ref([]);
const drag = ref(false);

function sort() {
  list.value = [...list.value].sort((a, b) => a.order - b.order);
}

function isSelected(order) {
  return selected.value.includes(order);
}
</script>

<style scoped>
.container {
  max-width: 400px;
  margin: 1rem auto;
  font-family: Arial, sans-serif;
}

.button {
  padding: 0.5rem 1rem;
  margin-bottom: 1rem;
  background-color: #007acc;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}

.button:hover {
  background-color: #005fa3;
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

.list-item.muted {
  color: #999;
}

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
.label-text {
  flex-grow: 1;
  cursor: pointer;
}
</style>
