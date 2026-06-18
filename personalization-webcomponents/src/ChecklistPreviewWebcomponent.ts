import { defineCustomElement } from "vue";

import ChecklistPreviewVueComponent from "@/ChecklistPreview.ce.vue";
import I18nHost from "@/i18n-host.ce.vue";

// convert into custom element constructor
const I18nHostElement = defineCustomElement(I18nHost);

const ChecklistPreviewWebcomponent = defineCustomElement(
  ChecklistPreviewVueComponent
);

// register
customElements.define("checklist-preview-i18n-host", I18nHostElement);

customElements.define(
  "checklist-preview-wrapped",
  ChecklistPreviewWebcomponent
);
