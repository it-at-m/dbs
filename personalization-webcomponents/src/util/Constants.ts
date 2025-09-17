export const FIRSTNAME_DEFAULT = "World";

export const LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT = "lhm.servicenavigator";

export const QUERY_PARAM_SN_RESULT_NAME = "p13n-name";
export const QUERY_PARAM_SN_RESULT_ID = "p13n-id";
export const QUERY_PARAM_SN_RESULT_SERVICES = "p13n-services";

export const QUERY_PARAM_CHECKLIST_ID = "cl-id";

let ACCESS_TOKEN: string | undefined = undefined;

export function getChecklistIconByTitle(checklistTitle: string) {
  return {
    "Ich habe wenig Geld.":
      "https://stadt.muenchen.de/dam/Home/lhm_common/service-navigator/10482730.svg",
    "Ich will umziehen.":
      "https://stadt.muenchen.de/dam/Home/lhm_common/service-navigator/10482700.svg",
    "Ich manage eine Familie.":
      "https://stadt.muenchen.de/dam/Home/lhm_common/service-navigator/10483310.svg",
    Einwanderung:
      "https://stadt.muenchen.de/dam/Home/lhm_common/service-navigator/10483311.svg",
  }[checklistTitle];
}

export function getAPIBaseURL(): string {
  if (import.meta.env.VITE_VUE_APP_API_URL) {
    return import.meta.env.VITE_VUE_APP_API_URL;
  } else {
    return new URL(import.meta.url).origin;
  }
}

export function setAccessToken(newAccessToken: string): void {
  console.debug("#setAccessToken with token ", newAccessToken);
  ACCESS_TOKEN = newAccessToken;
}

export function getAccessToken(): string | undefined {
  console.debug("#getAccessToken getting ", ACCESS_TOKEN);
  return ACCESS_TOKEN;
}
