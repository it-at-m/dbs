<template>

  <div
      tabindex="0"
      class="radio-look"
      :disabled="disabled"
      :checked="checked"
      @click="emit('check')"
      @keydown="(evt) => evt.keyCode == 32 ? emit('check') : null"
  >
    <muc-icon
        class="icon check-icon"
        icon="check"
        :color="checked ? 'white' : 'var(--color-neutrals-blue)'"
    />
  </div>
</template>

<script lang="ts" setup>
import {MucIcon} from "@muenchen/muc-patternlab-vue";

const props = withDefaults(
    defineProps<{
      checked?: boolean;
      disabled?: boolean;
    }>(),
    {
      checked: false,
      disabled: false,
    }
);

const emit = defineEmits(["check"])
</script>

<style scoped>
.radio-look {
  --radio-button-size: 24px;
  --radio-button-border-width: 2px;

  display: inline-block;
  appearance: none;
  -webkit-appearance: none;
  flex: 0 0 auto;
  width: var(--radio-button-size);
  height: var(--radio-button-size);
  border: var(--radio-button-border-width) solid var(--color-neutrals-grey);
  border-radius: var(--radio-button-size);
  background: white;
  box-sizing: border-box;
  position: relative;
  cursor: pointer;
  outline-offset: var(--radio-button-border-width);
  transition: border-color 0.3s ease,
  background-color 0.3s ease;
}

.check-icon {
  display: block;
  margin: auto;
  width: calc(var(--radio-button-size) - (2*var(--radio-button-border-width)));
  height: calc(var(--radio-button-size) - (2*var(--radio-button-border-width)));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.radio-look[checked]:hover, .radio-look[checked]:focus {
  border-color: var(--color-brand-main-blue);
  background-color: var(--color-neutrals-blue);
}

.radio-look[checked=false]:hover .check-icon, .radio-look[checked=false]:focus .check-icon {
  opacity: 1;
}
.radio-look[checked=true]:hover .check-icon, .radio-look[checked=true]:focus .check-icon {
  opacity: 0;
}

.radio-look[checked=true] {
  border-color: var(--color-brand-main-blue-dark, #004376);
  background-color: var(--color-brand-main-blue)
}

.radio-look[checked=true] .check-icon {
  opacity: 1;
}


</style>
<script setup lang="ts">
</script>