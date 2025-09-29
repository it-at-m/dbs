export default interface ChecklistItemServiceNavigator {
  serviceID: string;
  checked: Date | null;
  serviceName: string;
  publicUrl: string;
  summary: string;
  isExternal: boolean;
  mandatory: boolean;
  appointmentServiceUrl?: string;
  appointmentService?: boolean;
  onlineServices?: OnlineService[];
}

export interface OnlineService {
  uri: string;
  label: string;
}
