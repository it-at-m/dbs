import type { Ref } from "vue";

import { onMounted, ref } from "vue";

import { getQueryParam } from "@/util/util";

export interface IDPConfig {
  queryParameter: IDP;
  displayName: string;
  keycloakIdentifier: KEYCLOAK_PROVIDER_ID;
}

export const IDP_BAYERN_ID = "BayernID";
export const IDP_ELSTER = "ELSTER_NEZO";
export const IDP_BUND_ID = "BundID";

export type IDP = typeof IDP_BAYERN_ID | typeof IDP_ELSTER | typeof IDP_BUND_ID;

export const KEYCLOAK_ID_BAYERN_ID = "buergerkonto";
export const KEYCLOAK_ID_ELSTER = "nezo";
export const KEYCLOAK_ID_BUND_ID = "bundid";

export type KEYCLOAK_PROVIDER_ID =
  | typeof KEYCLOAK_ID_BAYERN_ID
  | typeof KEYCLOAK_ID_ELSTER
  | typeof KEYCLOAK_ID_BUND_ID;

export const KEYCLOAK_AUTH_LEVEL1 = "level1";
export const KEYCLOAK_AUTH_LEVEL3 = "level3";
export const KEYCLOAK_AUTH_LEVEL4 = "level4";

export type KEYCLOAK_AUTH_LEVEL =
  | typeof KEYCLOAK_AUTH_LEVEL1
  | typeof KEYCLOAK_AUTH_LEVEL3
  | typeof KEYCLOAK_AUTH_LEVEL4;

export const idphintMapping = new Map<string, IDPConfig>([
  [
    IDP_BAYERN_ID,
    {
      queryParameter: "BayernID",
      displayName: "Bayern-ID",
      keycloakIdentifier: KEYCLOAK_ID_BAYERN_ID,
    },
  ],
  [
    IDP_ELSTER,
    {
      queryParameter: "ELSTER_NEZO",
      displayName: "ELSTER",
      keycloakIdentifier: KEYCLOAK_ID_ELSTER,
    },
  ],
  [
    IDP_BUND_ID,
    {
      queryParameter: "BundID",
      displayName: "BundID",
      keycloakIdentifier: KEYCLOAK_ID_BUND_ID,
    },
  ],
]);

// by convention, composable function names start with "use"
export function useIdpHint() {
  const idpConfig: Ref<IDPConfig | null> = ref(null);

  onMounted(() => {
    const queryParam = getQueryParam("lg-idphint");
    if (queryParam && idphintMapping.has(queryParam)) {
      idpConfig.value = idphintMapping.get(queryParam) as IDPConfig;
    } else {
      idpConfig.value = null;
    }
  });

  // expose managed state as return value
  return { idpConfig };
}
