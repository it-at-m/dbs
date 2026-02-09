import type {
  KEYCLOAK_AUTH_LEVEL,
  KEYCLOAK_PROVIDER_ID,
} from "@/composables/idphint.ts";

export default interface AuthorizationRequestDetails {
  loginProvider: KEYCLOAK_PROVIDER_ID | undefined;
  authLevel: KEYCLOAK_AUTH_LEVEL | undefined;
}
