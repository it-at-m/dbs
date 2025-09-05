import { defineCustomElement } from "vue";

import MyChecklistsVueComponent from "@/MyChecklists.ce.vue";

// convert into custom element constructor
const MyChecklistsWebcomponent = defineCustomElement(MyChecklistsVueComponent);

// register
customElements.define("my-checklists", MyChecklistsWebcomponent);
