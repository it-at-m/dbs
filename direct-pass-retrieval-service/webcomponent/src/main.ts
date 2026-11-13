/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

import { defineCustomElement } from "vue";
import PasswordResetElement from "@/PasswordResetElement.ce.vue";

const passwordResetElement = defineCustomElement(PasswordResetElement);

customElements.define("password-reset-element", passwordResetElement);
