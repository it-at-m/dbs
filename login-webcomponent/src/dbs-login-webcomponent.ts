import { defineCustomElement } from "vue";

import DbsLogin from "@/dbs-login.ce.vue";

// convert into custom element constructor
const DbsLoginElement = defineCustomElement(DbsLogin);
// register
customElements.define("dbs-login", DbsLoginElement);
