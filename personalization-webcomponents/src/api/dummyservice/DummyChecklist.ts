import type DummyChecklistItem from "@/api/dummyservice/DummyChecklistItem.ts";

export default interface DummyChecklist {
    title: string,
    lhmExtId: string,
    items: DummyChecklistItem[],
    lastUpdated: Date
}