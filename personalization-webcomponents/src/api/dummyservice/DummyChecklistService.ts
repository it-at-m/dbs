import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";

export default class DummyChecklistService {
  dummyResponse: DummyChecklist[] = [
    {
      title: "Ich habe wenig Geld.",
      lhmExtId: "111",
      lastUpdated: new Date(),
      items: [
        {
          serviceID: "1",
          checked: null,
          title: "Schuldner- und Insolvenzberatung der Stadt München",
          note: "",
          required: true,
        },
        {
          serviceID: "2",
          checked: new Date(),
          title: "Befreiung vom Rundfunkbeitrag",
          note: "",
          required: true,
        },
        {
          serviceID: "3",
          checked: new Date(),
          title: "Städtische Ferienangebote",
          note: "",
          required: true,
        },
        {
          serviceID: "4",
          checked: new Date(),
          title: "another item 4",
          note: "",
          required: true,
        },
        {
          serviceID: "5",
          checked: new Date(),
          title: "another item 5",
          note: "",
          required: true,
        },
      ],
    },
    {
      title: "Ich will umziehen.",
      lhmExtId: "111",
      lastUpdated: new Date(),
      items: [
        {
          serviceID: "1",
          checked: null,
          title: "Wohnsitz anmelden oder ummelden",
          note: "",
          required: true,
        },
        {
          serviceID: "2",
          checked: null,
          title: "Änderung der Adresse in Fahrzeugpapieren",
          note: "",
          required: true,
        },
        {
          serviceID: "3",
          checked: null,
          title: "Anmeldung eines Hundes",
          note: "",
          required: true,
        },
        {
          serviceID: "4",
          checked: null,
          title: "another item 4",
          note: "",
          required: true,
        },
        {
          serviceID: "5",
          checked: null,
          title: "another item 5",
          note: "",
          required: true,
        },
        {
          serviceID: "6",
          checked: null,
          title: "another item 6",
          note: "",
          required: true,
        },
        {
          serviceID: "7",
          checked: null,
          title: "another item 7",
          note: "",
          required: true,
        },
        {
          serviceID: "8",
          checked: null,
          title: "another item 8",
          note: "",
          required: true,
        },
        {
          serviceID: "9",
          checked: new Date(),
          title: "another item 9",
          note: "",
          required: true,
        },
        {
          serviceID: "10",
          checked: new Date(),
          title: "another item 10",
          note: "",
          required: true,
        },
      ],
    },
    {
      title: "Ich manage eine Familie.",
      lhmExtId: "111",
      lastUpdated: new Date(),
      items: [
        {
          serviceID: "1",
          checked: new Date(),
          title: "Beratung zur Kindertagespflege",
          note: "",
          required: true,
        },
        {
          serviceID: "2",
          checked: new Date(),
          title: "Förderung von Kindern im Vorschulalter",
          note: "",
          required: true,
        },
        {
          serviceID: "3",
          checked: new Date(),
          title: "Leistungen aus dem Bildungspaket",
          note: "",
          required: true,
        },
        {
          serviceID: "4",
          checked: new Date(),
          title: "another item 4",
          note: "",
          required: true,
        },
        {
          serviceID: "5",
          checked: new Date(),
          title: "another item 5",
          note: "",
          required: true,
        },
        {
          serviceID: "6",
          checked: new Date(),
          title: "another item 6",
          note: "",
          required: true,
        },
        {
          serviceID: "7",
          checked: new Date(),
          title: "another item 7",
          note: "",
          required: true,
        },
        {
          serviceID: "8",
          checked: new Date(),
          title: "another item 8",
          note: "",
          required: true,
        },
        {
          serviceID: "9",
          checked: new Date(),
          title: "another item 9",
          note: "",
          required: true,
        },
        {
          serviceID: "10",
          checked: new Date(),
          title: "another item 10",
          note: "",
          required: true,
        },
      ],
    },
  ];

  getChecklists(): Promise<DummyChecklist[]> {
    return new Promise((resolve) =>
      setTimeout(() => resolve(this.dummyResponse), 1000)
    );
  }
}
