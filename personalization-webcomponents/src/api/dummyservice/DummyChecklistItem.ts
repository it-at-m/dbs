export default interface DummyChecklistItem {
    serviceID: string,
    checked: Date | null,
    title: string,
    note: string,
    required: boolean
}