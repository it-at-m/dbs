export interface SNService {
  serviceName: string
  publicUrl: string
  summary: string
  onlineServices?: OnlineService[]
  id: string
  hasResponsibilities?: boolean
  isExternal: boolean
  appointmentServiceUrl?: string
  appointmentService?: boolean
}

export interface OnlineService {
  uri: string
  label: string
}
