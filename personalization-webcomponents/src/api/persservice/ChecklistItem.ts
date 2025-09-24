export default interface ChecklistItem {
  serviceID: string;
  checked: Date | null;
  title: string;
  note: string;
  required: boolean;
}
