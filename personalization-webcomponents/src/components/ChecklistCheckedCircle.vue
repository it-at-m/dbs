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
  border: 2px solid var(--mde-color-neutral-grey);
  flex-shrink: 0;
}

.circle.checked {
  background-color: var(--mde-color-neutral-grey-x-light);
  border: 2px solid var(--mde-color-neutral-grey-x-light);
}
</style>
