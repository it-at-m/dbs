import { defineCustomElement } from "vue";

import SolidDataManagerVueComponent from "@/SolidDataManager.ce.vue";

const SolidDataManagerWebcomponent = defineCustomElement(
  SolidDataManagerVueComponent
);

customElements.define("solid-data-manager", SolidDataManagerWebcomponent);


