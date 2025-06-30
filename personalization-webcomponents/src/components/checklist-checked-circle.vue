<template>
  <div
    class="circle"
    :class="checked ? 'checked' : ''"
  ></div>
</template>

<script setup lang="ts">
defineProps<{
  checked: boolean;
}>();
</script>

<style scoped>
.circle {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid var(--color-brand-main-blue);
}

.circle.checked {
  background-color: var(--color-brand-main-blue);
}
</style>
