export const FIRSTNAME_DEFAULT = "World";

export const QUERY_PARAM_CHECKLIST_ID = "cl-id";

export function getChecklistIconByTitle(checklistTitle: string) {
  return {
    "Ich habe wenig Geld.":
      "https://stadt.muenchen.de/dam/Home/lhm_common/service-navigator/10482730.svg",
    "Ich will umziehen.":
      "https://stadt.muenchen.de/dam/Home/lhm_common/service-navigator/10482700.svg",
    "Ich manage eine Familie.":
      "https://stadt.muenchen.de/dam/Home/lhm_common/service-navigator/10483310.svg",
    "Einwanderung":
      "https://stadt.muenchen.de/dam/Home/lhm_common/service-navigator/10483311.svg",
  }[checklistTitle];
}
