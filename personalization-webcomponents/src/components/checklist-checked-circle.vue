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
  border: 2px solid var(--color-neutrals-grey);
}

.circle.checked {
  background-color: #9CA8B3;
  border: 2px solid #9CA8B3;
}
</style>
