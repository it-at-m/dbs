import type ChecklistItem from "@/api/persservice/ChecklistItem.ts";

export default interface Checklist {
  id: string;
  title: string;
  lhmExtId: string;
  situationId: string;
  checklistItems: ChecklistItem[];
  lastUpdate: Date;
}
