import { defineCustomElement } from "vue";

import ChecklistDetailVueComponent from "@/ChecklistDetail.ce.vue";

// convert into custom element constructor
const ChecklistDetailWebcomponent = defineCustomElement(
  ChecklistDetailVueComponent
);

// register
customElements.define("checklist-detail", ChecklistDetailWebcomponent);
