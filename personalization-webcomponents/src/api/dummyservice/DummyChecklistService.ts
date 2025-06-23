import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";

export default class DummyChecklistService {

    getChecklists(): DummyChecklist[] {
        return [
            {
                title: "Ich habe wenig Geld.",
                lhmExtId: "111",
                lastUpdated: new Date(),
                items: [
                    { serviceID: "1", checked: false, title: "Schuldner- und Insolvenzberatung der Stadt München", note: "", required: true },
                    { serviceID: "2", checked: true, title: "Befreiung vom Rundfunkbeitrag", note: "", required: true },
                    { serviceID: "3", checked: true, title: "Städtische Ferienangebote", note: "", required: true },
                    { serviceID: "4", checked: true, title: "another item 4", note: "", required: true },
                    { serviceID: "5", checked: true, title: "another item 5", note: "", required: true },
                ]
            },
            {
                title: "Ich will umziehen.",
                lhmExtId: "111",
                lastUpdated: new Date(),
                items: [
                    { serviceID: "1", checked: false, title: "Wohnsitz anmelden oder ummelden", note: "", required: true },
                    { serviceID: "2", checked: false, title: "Änderung der Adresse in Fahrzeugpapieren", note: "", required: true },
                    { serviceID: "3", checked: false, title: "Anmeldung eines Hundes", note: "", required: true },
                    { serviceID: "4", checked: false, title: "another item 4", note: "", required: true },
                    { serviceID: "5", checked: false, title: "another item 5", note: "", required: true },
                    { serviceID: "6", checked: false, title: "another item 6", note: "", required: true },
                    { serviceID: "7", checked: false, title: "another item 7", note: "", required: true },
                    { serviceID: "8", checked: false, title: "another item 8", note: "", required: true },
                    { serviceID: "9", checked: true, title: "another item 9", note: "", required: true },
                    { serviceID: "10", checked: true, title: "another item 10", note: "", required: true },
                ]
            },
            {
                title: "Ich manage eine Familie.",
                lhmExtId: "111",
                lastUpdated: new Date(),
                items: [
                    { serviceID: "1", checked: true, title: "Beratung zur Kindertagespflege", note: "", required: true },
                    { serviceID: "2", checked: true, title: "Förderung von Kindern im Vorschulalter", note: "", required: true },
                    { serviceID: "3", checked: true, title: "Leistungen aus dem Bildungspaket", note: "", required: true },
                    { serviceID: "4", checked: true, title: "another item 4", note: "", required: true },
                    { serviceID: "5", checked: true, title: "another item 5", note: "", required: true },
                    { serviceID: "6", checked: true, title: "another item 6", note: "", required: true },
                    { serviceID: "7", checked: true, title: "another item 7", note: "", required: true },
                    { serviceID: "8", checked: true, title: "another item 8", note: "", required: true },
                    { serviceID: "9", checked: true, title: "another item 9", note: "", required: true },
                    { serviceID: "10", checked: true, title: "another item 10", note: "", required: true },
                ]
            },
        ]
    }
}