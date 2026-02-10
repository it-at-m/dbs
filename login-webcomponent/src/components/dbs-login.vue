<template>
  <login-dialog
    :loggedin="isLoggedIn"
    @login="login"
  />
  <div class="m-page-header">
    <login-button
      ref="loginBtn"
      :is-dropdown-open="isDropdownOpen"
      :is-mobile="isMobile"
      :is-logged-in="isLoggedIn"
      @login="login"
      @toggle-dropdown="toggleDropdown"
    />
  </div>
  <div ref="dropdownDiv">
    <user-account-dropdown
      :open="isDropdownOpen"
      :is-mobile="isMobile"
      :is-logged-in="isLoggedIn"
      :user-properties="userProperties"
      :links="links"
      :position="bcRect"
      @login="login"
      @logout="logout"
      @close="isDropdownOpen = false"
    />
  </div>
  <inactivity-tracker
    v-model:last-active="lastActive"
    :loggedin="isLoggedIn"
    @inactive="logout"
    @refresh="refresh"
  />
</template>

<script setup lang="ts">
import type AuthorizationRequestDetails from "@/types/AuthorizationRequestDetails.ts";
import type LinkProperties from "@/types/LinkProperties.ts";
import type UserProperties from "@/types/UserProperties.ts";
import type {
  KeycloakConfig,
  KeycloakLoginOptions,
  KeycloakUserInfo,
} from "keycloak-js";
import type { Ref } from "vue";

import { onClickOutside, useElementBounding } from "@vueuse/core";
import Keycloak from "keycloak-js";
import { onBeforeUnmount, onMounted, ref, useTemplateRef } from "vue";
import { useCookies } from "vue3-cookies";

import InactivityTracker from "@/components/inactivity-tracker.vue";
import LoginButton from "@/components/login-button.vue";
import LoginDialog from "@/components/login-dialog.vue";
import UserAccountDropdown from "@/components/user-account-dropdown.vue";
import { useIdpHint } from "@/composables/idphint";
import AuthorizationEventDetails from "@/types/AuthorizationEventDetails.ts";
import { parseTime } from "@/util/util";

const {
  kcUrl,
  kcRealm,
  kcClientId,
  cookieDomain = "",
  links,
} = defineProps<{
  kcUrl: string;
  kcRealm: string;
  kcClientId: string;
  cookieDomain?: string;
  links: LinkProperties;
}>();

const keycloak = new Keycloak({
  url: kcUrl,
  realm: kcRealm,
  clientId: kcClientId,
} as KeycloakConfig);

const { cookies } = useCookies();

const COOKIE_NAME_SSOTOKEN = "ssotoken";
const LS_KEY_REFRESHTOKEN = "refreshtoken";
const LS_KEY_IDTOKEN = "idtoken";
const USERINFO_ACCOUNTSOURCE_ORGANIZATION = "ELSTER_NEZO";

const ssotoken = cookies.get(COOKIE_NAME_SSOTOKEN);
const refreshtoken = localStorage.getItem(LS_KEY_REFRESHTOKEN);
const idtoken = localStorage.getItem(LS_KEY_IDTOKEN);

// Use secure cookies only in prod - doesn't work locally
const cookieSettings = {
  cookiesSecure: import.meta.env.VITE_COOKIES_SECURE,
  expireTime: "5min",
  path: "/",
  sameSite: "Strict",
};

const authLevels = {
  level1: "Benutzername & Passwort",
  level3: "ELSTER Zertifikat",
  level4: "Online-Ausweis / EU-ID",
};

const { idpConfig } = useIdpHint();
const isLoggedIn = ref(false);
const userProperties = ref<UserProperties>({} as UserProperties);

const lastActive = ref(new Date());
const isMobile = ref(false);
//@ts-expect-error useTemplateRef doesn't allow different handling
const loginBtn: Ref<HTMLElement | undefined> = useTemplateRef("loginBtn");
const dropdownDiv: Ref<HTMLElement | undefined> = ref();

const isDropdownOpen = ref(false);

const AUTHORIZATION_EVENT_NAME = "authorization-event";
const AUTHORIZATION_REQUEST_NAME = "authorization-request";

// store interval id so we can clear it later
let authResendInterval: number | undefined;

onClickOutside(dropdownDiv, () => (isDropdownOpen.value = false), {
  ignore: [loginBtn],
});

const bcRect = useElementBounding(loginBtn);

function createAuthorizationEvent(
  authEventDetails: AuthorizationEventDetails | undefined
): CustomEvent {
  return new CustomEvent(AUTHORIZATION_EVENT_NAME, {
    detail: authEventDetails,
  });
}

const afterAuthentication = (authenticated: boolean) => {
  console.debug(authenticated ? "Angemeldet." : "Nicht angemeldet.");

  if (keycloak.token != null) {
    isLoggedIn.value = true;

    keycloak
      .loadUserInfo()
      .then((data) => {
        const userInfo = data as KeycloakUserInfo;
        const newUserProperties = {} as UserProperties;
        if (!userInfo.accountSource || !userInfo.displayName) {
          newUserProperties.personname = "Bürger*in";
        } else {
          if (userInfo.accountSource === USERINFO_ACCOUNTSOURCE_ORGANIZATION) {
            // Zusatzinfo bei Unternehmenskonto
            if (
              userInfo.handelndePersonVorname &&
              userInfo.handelndePersonNachname
            ) {
              newUserProperties.additionalInfoOrganization = `${userInfo.handelndePersonVorname} ${userInfo.handelndePersonNachname}`;
            } else {
              newUserProperties.additionalInfoOrganization = userInfo.email;
            }
            newUserProperties.accountSource = "ELSTER";
            newUserProperties.authLevel = "Mein Unternehmenskonto";
          } else {
            newUserProperties.accountSource = userInfo.accountSource;
            if (userInfo.authlevel) {
              newUserProperties.authLevel =
                //@ts-expect-error won't fix this ts error
                authLevels[userInfo.authlevel] || undefined;
            }
          }
          newUserProperties.personname = userInfo.displayName;
        }

        userProperties.value = newUserProperties;
      })
      .catch((reason) => {
        console.error("error loading userinfo", reason);
        userProperties.value = { personname: "Bürger*in" } as UserProperties;
      });

    _setCookieWithSettings(COOKIE_NAME_SSOTOKEN, keycloak.token);
    localStorage.setItem(LS_KEY_REFRESHTOKEN, keycloak.refreshToken as string);
    localStorage.setItem(LS_KEY_IDTOKEN, keycloak.idToken as string);

    document.dispatchEvent(createAuthorizationEvent(getAuthEventDetails()));

    // Resend event every 100ms for the first 4 seconds
    const interval = setInterval(() => {
      document.dispatchEvent(createAuthorizationEvent(getAuthEventDetails()));
    }, 100);
    setTimeout(() => {
      clearInterval(interval);
    }, 4000);

    // Resend event every 5 seconds for webcomponents that came late to the party
    authResendInterval = window.setInterval(() => {
      document.dispatchEvent(createAuthorizationEvent(getAuthEventDetails()));
    }, 5000);
  }
};

function getAuthEventDetails(): AuthorizationEventDetails | undefined {
  if (keycloak && keycloak.userInfo) {
    const uI = keycloak.userInfo as KeycloakUserInfo;
    return new AuthorizationEventDetails(
      uI.displayName,
      uI.email,
      uI.accountSource,
      uI.authlevel,
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      keycloak.token!
    );
  } else {
    return undefined;
  }
}

const login = () => {
  if (idpConfig.value) {
    keycloak.login({
      idpHint: idpConfig.value.keycloakIdentifier,
    });
  } else {
    keycloak.login();
  }
};

function loginWithAuthorizationRequest(event: Event) {
  const eventDetail = (event as CustomEvent)
    .detail as AuthorizationRequestDetails;

  const loginOptions: KeycloakLoginOptions = {};

  if (eventDetail.loginProvider) {
    loginOptions.idpHint = eventDetail.loginProvider;
  }

  if (eventDetail.authLevel) {
    loginOptions.scope = eventDetail.authLevel;
  }

  keycloak.login(loginOptions);
}

const logout = () => {
  isLoggedIn.value = false;
  _deleteCookieWithSettings(COOKIE_NAME_SSOTOKEN);
  localStorage.removeItem(LS_KEY_REFRESHTOKEN);
  localStorage.removeItem(LS_KEY_IDTOKEN);
  document.dispatchEvent(createAuthorizationEvent(undefined));
  keycloak.logout({ redirectUri: document.URL });
  isDropdownOpen.value = false;
};

const refresh = () => {
  keycloak
    .updateToken(5) //Token updaten wenn es in <= 5 Sekunden abläuft
    .then(function (refreshed: boolean) {
      if (refreshed) {
        _setCookieWithSettings(COOKIE_NAME_SSOTOKEN, keycloak.token);
        localStorage.setItem(
          LS_KEY_REFRESHTOKEN,
          keycloak.refreshToken as string
        );
        localStorage.setItem(LS_KEY_IDTOKEN, keycloak.idToken as string);
      }
    })
    .catch(function () {
      logout();
    });
};

const _setCookieWithSettings = (key: string, value: string | undefined) => {
  cookies.set(
    key,
    value as string,
    cookieSettings.expireTime,
    cookieSettings.path,
    cookieDomain,
    cookieSettings.cookiesSecure,
    cookieSettings.sameSite
  );
};

const _deleteCookieWithSettings = (key: string) => {
  cookies.remove(key, cookieSettings.path, cookieDomain);
};

const toggleDropdown = (): void => {
  isDropdownOpen.value = !isDropdownOpen.value;
};

const checksMobile = () => {
  isMobile.value = window.matchMedia("(max-width: 1199px)").matches;
  // always close dropdown on resize due to offcanvas-backdrop problems
  isDropdownOpen.value = false;
};

onMounted(() => {
  checksMobile();
  window.addEventListener("resize", checksMobile);
  document.addEventListener(
    AUTHORIZATION_REQUEST_NAME,
    loginWithAuthorizationRequest
  );

  let enableSilentSSOFallback;
  try {
    const currentDomain = _getDomain(new URL(window.location.href));
    const ssoDomain = _getDomain(new URL(kcUrl));
    enableSilentSSOFallback = currentDomain === ssoDomain;
    console.debug("Comparing current and sso domain: ", {
      currentDomain,
      ssoDomain,
      enableSilentSSOFallback,
    });
  } catch (err) {
    enableSilentSSOFallback = true;
    console.debug("Enabling silent sso fallback due to error", err);
  }

  //Keycloak-js initialisieren
  keycloak
    // @ts-expect-error we dont need the full object here
    .init({
      onLoad: "check-sso",
      silentCheckSsoRedirectUri: `${location.origin}/buergerservice/anliegen/login.html`,
      enableLogging: true,
      pkceMethod: "S256",
      token: ssotoken,
      refreshToken: refreshtoken ? refreshtoken : undefined,
      idToken: idtoken ? idtoken : undefined,
      timeSkew: 0,
      /**
       * The following feature is disabled when we are not on the same subdomain as our SSO.
       * Because in this case a silent check will force a redirect which will destroy the state
       * of some webcomponents (e.g. Terminvereinbarung).
       */
      silentCheckSsoFallback: enableSilentSSOFallback,
    })
    .then(afterAuthentication)
    .catch((error) => {
      console.error("keycloak init error", error);
    });

  if (ssotoken) {
    if (keycloak.token) {
      console.debug("ssotoken found in cookies");
      if (keycloak.tokenParsed && keycloak.tokenParsed.exp) {
        const tokenExpiration = keycloak.tokenParsed.exp;
        console.debug(parseTime(tokenExpiration, false));
      }
    }
  }
});

onBeforeUnmount(() => {
  // cleanup listeners added in onMounted
  window.removeEventListener("resize", checksMobile);
  document.removeEventListener(
    AUTHORIZATION_REQUEST_NAME,
    loginWithAuthorizationRequest
  );

  // clear the interval if still active
  if (authResendInterval != null) {
    clearInterval(authResendInterval);
    authResendInterval = undefined;
  }
});

function _getDomain(url: URL) {
  const split = url.host.split(".");
  const tld = split.pop();
  const sld = split.pop();
  return [sld, tld].join(".");
}
</script>
