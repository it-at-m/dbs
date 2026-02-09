import type { KEYCLOAK_AUTH_LEVEL } from "@/composables/idphint.ts";

export default class AuthorizationEventDetails {
  constructor(
    buergerName: string,
    buergerMail: string,
    loginProvider: string,
    trustLevel: KEYCLOAK_AUTH_LEVEL,
    accessToken: string
  ) {
    this.buergerName = buergerName;
    this.buergerMail = buergerMail;
    this.loginProvider = loginProvider;
    this.trustLevel = trustLevel;
    this.accessToken = accessToken;
  }

  buergerName: string;
  buergerMail: string;
  loginProvider: string;
  trustLevel: KEYCLOAK_AUTH_LEVEL;
  accessToken: string;
}
