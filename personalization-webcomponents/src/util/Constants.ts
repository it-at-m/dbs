export const FIRSTNAME_DEFAULT = "World";

export const LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT = "lhm.servicenavigator";

export const QUERY_PARAM_SN_RESULT_NAME = "p13n-name";
export const QUERY_PARAM_SN_RESULT_ID = "p13n-id";
export const QUERY_PARAM_SN_RESULT_SERVICES = "p13n-services";

export const QUERY_PARAM_CHECKLIST_ID = "cl-id";

let ACCESS_TOKEN: string | undefined = undefined;

export function getChecklistIconBySituationId(situationId: string) {
  return `https://stadt.muenchen.de/dam/Home/lhm_common/service-navigator/${situationId}.svg`;
}

export function getAPIBaseURL(): string {
  if (import.meta.env.VITE_VUE_APP_API_URL) {
    return import.meta.env.VITE_VUE_APP_API_URL;
  } else {
    return new URL(import.meta.url).origin;
  }
}

export function setAccessToken(newAccessToken: string): void {
  ACCESS_TOKEN = newAccessToken;
}

export function getAccessToken(): string | undefined {
  return ACCESS_TOKEN;
}

class Cookie {
  key: string;
  value: string;

  constructor(key: string, value: string) {
    this.key = key;
    this.value = value;
  }
}

/**
 * Get Cookie by its name . Undefined if cookie is not present.
 * @param cookieName Name of the Cookie
 */
export function getCookie(cookieName: string): Cookie | undefined {
  let cookie: Cookie | undefined = undefined;

  document.cookie.split(";").forEach(function (el) {
    const [key, value] = el.split("=");
    if (key && value && key.trim() == cookieName) {
      cookie = new Cookie(key.trim(), value);
    }
  });
  return cookie;
}

export function getXSRFToken() {
  const XSRFToken = getCookie("XSRF-TOKEN");
  if (XSRFToken == undefined) {
    console.debug("XRSF-Token Konnte nicht aus Cookie geholt werden");
    return "";
  }
  return XSRFToken.value;
}
