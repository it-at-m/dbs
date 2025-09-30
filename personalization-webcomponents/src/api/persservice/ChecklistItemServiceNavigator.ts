export default interface ChecklistItemServiceNavigator {
  serviceID: string;
  checked: Date | null;
  title: string;
  publicUrl: string;
  note: string;
  isExternal: boolean;
  required: boolean;
  appointmentServiceUrl?: string;
  appointmentService?: boolean;
  onlineServices?: OnlineService[];
}

export interface OnlineService {
  uri: string;
  label: string;
}
