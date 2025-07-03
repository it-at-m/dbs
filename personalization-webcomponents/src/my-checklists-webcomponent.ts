import { defineCustomElement } from "vue";

import MyChecklistsVueComponent from "@/my-checklists.vue";

// convert into custom element constructor
const MyChecklistsWebcomponent = defineCustomElement(MyChecklistsVueComponent);

// register
customElements.define("my-checklists", MyChecklistsWebcomponent);
