import { defineCustomElement } from "vue";

import ChecklistOverviewVueComponent from "@/checklist-overview.ce.vue";

// convert into custom element constructor
const ChecklistOverviewWebcomponent = defineCustomElement(
    ChecklistOverviewVueComponent
);

// register
customElements.define(
  "checklist-overview",
    ChecklistOverviewWebcomponent
);
