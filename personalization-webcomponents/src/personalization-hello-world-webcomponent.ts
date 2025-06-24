import { defineCustomElement } from "vue";

import PersonalizationHelloWorldVueComponent from "@/personalization-hello-world-webcomponent.ce.vue";

// convert into custom element constructor
const PersonalizationHelloWorldWebComponent = defineCustomElement(
  PersonalizationHelloWorldVueComponent
);

// register
customElements.define(
  "personalization-hello-world-webcomponent",
  PersonalizationHelloWorldWebComponent
);
