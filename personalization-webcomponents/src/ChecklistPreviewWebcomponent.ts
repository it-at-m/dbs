import { defineCustomElement } from "vue";

import ChecklistPreviewVueComponent from "@/ChecklistPreview.ce.vue";

// convert into custom element constructor
const ChecklistPreviewWebcomponent = defineCustomElement(
  ChecklistPreviewVueComponent
);

// register
customElements.define("checklist-preview", ChecklistPreviewWebcomponent);
