import type ChecklistItemServiceNavigator from "@/api/persservice/ChecklistItemServiceNavigator.ts";

export default interface ChecklistServiceNavigator {
  id: string;
  title: string;
  lhmExtId: string;
  situationId: string;
  checklistItemServiceNavigatorDtos: ChecklistItemServiceNavigator[];
  lastUpdate: Date;
}
