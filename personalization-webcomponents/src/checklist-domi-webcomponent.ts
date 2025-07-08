import { defineCustomElement } from "vue";

import ChecklistDomiVueComponent from "@/checklist-domi.vue";

// convert into custom element constructor
const ChecklistDetailWebcomponent = defineCustomElement(
    ChecklistDomiVueComponent
);

// register
customElements.define(
    "checklist-domi",
    ChecklistDetailWebcomponent
);
