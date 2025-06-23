<script setup lang="ts">
defineProps({
  backgroundColor: {
    type: String,
    default: "#fff"
  }
})

</script>

<template>
  <div
      class="chip"
      :style="'background-color: ' + backgroundColor"
  >
    <div class="chip-content">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.chip {
  display: inline-flex;
  flex-direction: row;
  border: none;
  cursor: default;
  height: 32px;
  line-height: 32px;
  outline: none;
  padding: 0;
  font-size: 0.875rem;
  white-space: nowrap;
  align-items: center;
  border-radius: 16px;
  vertical-align: middle;
  text-decoration: none;
  justify-content: center;
}

.chip-content {
  cursor: inherit;
  display: flex;
  align-items: center;
  user-select: none;
  white-space: nowrap;
  padding-left: 16px;
  padding-right: 16px;
  opacity: 0.95;
}
</style>
