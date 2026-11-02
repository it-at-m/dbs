import { defineCustomElement } from "vue";

import ChecklistDetailVueComponent from "@/ChecklistDetail.ce.vue";
import I18nHost from "@/i18n-host.ce.vue";

// convert into custom element constructor
const I18nHostElement = defineCustomElement(I18nHost);

const ChecklistDetailWebcomponent = defineCustomElement(
  ChecklistDetailVueComponent
);

// register
customElements.define("checklist-detail-i18n-host", I18nHostElement);

customElements.define("checklist-detail-wrapped", ChecklistDetailWebcomponent);
