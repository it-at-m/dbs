<template>
  <dialog
    ref="loginDialog"
    style="max-width: 37rem"
  >
    <div tabindex="1">
      <div class="modal-header">
        <h2
          id="exampleModalLabel"
          class="modal-title"
        >
          Anmeldung für Ihr Anliegen
        </h2>

        <button
          type="button"
          class="modal-button-close"
          aria-label="Dialog schliessen"
          @click="loginDialog?.close()"
        >
          <icon-x style="color: #005a9f" />
        </button>
      </div>
      <div class="modal-body">
        <p>
          Um Ihr Anliegen einzusehen, melden Sie sich bei dem Konto an, das Sie
          für die Erstellung Ihres Anliegens genutzt haben.
        </p>

        <idp-login-btn
          v-if="idpConfig"
          :idp="idpConfig.queryParameter"
          style="margin-bottom: 24px"
          @login="login"
        />
        <login-button
          v-else
          style="margin-bottom: 24px"
          :is-dropdown-open="false"
          :is-mobile="false"
          :is-logged-in="false"
          @login="login"
        />
      </div>
    </div>
  </dialog>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from "vue";

import IconX from "@/components/icons/icon-x.vue";
import IdpLoginBtn from "@/components/IdpLoginBtn.vue";
import LoginButton from "@/components/login-button.vue";
import { useIdpHint } from "@/composables/idphint";
import { getQueryParam } from "@/util/util";

const props = defineProps({
  loggedin: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["close", "login"]);

const loginDialog = ref<HTMLDialogElement>();

const { idpConfig } = useIdpHint();

watch(
  () => props.loggedin,
  (loggedin) => {
    if (loggedin) {
      loginDialog.value?.close();
    }
  }
);

onMounted(() => {
  if (getQueryParam("lg-forcelogin") && !props.loggedin) {
    loginDialog.value?.showModal();
  }
});

function login() {
  loginDialog.value?.close();
  emit("login");
}
</script>

<style scoped>
dialog {
  border: none;
  margin-top: 92px;
  padding: 0;
}

.modal-body {
  padding-top: 16px;
  padding-bottom: 32px;
}

dialog div:focus {
  outline: none;
}

dialog::backdrop {
  background-color: #3a5368;
  opacity: 70%;
}

@media all and (min-width: 768px) {
  dialog {
    margin-top: 204px;
  }
}
</style>
