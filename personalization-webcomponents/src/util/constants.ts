export const FIRSTNAME_DEFAULT = "World";

export function getChecklistIconByTitle(checklistTitle: string) {
    return {
        "Ich habe wenig Geld.": "./assets/img/placeholder_checklist_icon.png",
        "Ich will umziehen.": "./assets/img/placeholder_checklist_icon.png",
        "Ich manage eine Familie.": "./assets/img/placeholder_checklist_icon.png"
    }[checklistTitle];
}