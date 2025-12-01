import type {
  EligibilityCheckInterface,
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

export class BürgergeldCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Bürgergeld";
  }

  evaluate(formData: FormData): EligibilityResult {
    // 1. Check financial difficulty (must be true)
    if (formData.hatFinanzielleNotlage !== true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Bürgergeld ist für Personen in finanzieller Notlage vorgesehen.",
      };
    }

    // 2. Check citizenship and residency status
    const hasValidCitizenship = formData.staatsangehoerigkeit === 'Deutsch';
    const hasValidResidency = 
      formData.aufenthaltsstatus === 'Aufenthaltserlaubnis' || 
      formData.aufenthaltsstatus === 'Niederlassungserlaubnis';
    
    if (!hasValidCitizenship && !hasValidResidency) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie benötigen die deutsche Staatsbürgerschaft oder eine gültige Aufenthaltserlaubnis/Niederlassungserlaubnis.",
      };
    }

    // 3. Check habitual residence in Germany
    if (formData.wohnsitzInDeutschland !== true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein.",
      };
    }

    // 4. Check age requirement (must be >= 15)
    if (!formData.alter || formData.alter < 15) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie müssen mindestens 15 Jahre alt sein.",
      };
    }

    // 5. Check not pensionable (not reached pension age)
    if (formData.rentenberechtigt === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Personen, die die Altersgrenze für ihre Rente erreicht haben, sind nicht berechtigt.",
      };
    }

    // 6. Check work ability (must NOT be none)
    if (formData.arbeitsfahigkeit === 'keine') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie müssen in der Lage sein, mindestens 3 Stunden täglich zu arbeiten.",
      };
    }

    // 7. Check employment status (must NOT be in education, self-employed, or pension)
    if (
      formData.beschaeftigungsstatus === 'student' ||
      formData.beschaeftigungsstatus === 'selbststaendig' ||
      formData.beschaeftigungsstatus === 'rentner'
    ) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Studierende, Selbständige und Rentner sind nicht berechtigt.",
      };
    }

    // 8. Check NOT already receiving conflicting benefits
    if (formData.beziehtAlg2 === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie beziehen bereits Bürgergeld.",
      };
    }

    if (formData.beziehtRente === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Rentenbezieher sind nicht berechtigt.",
      };
    }

    if (formData.beziehtWohngeld === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Wohngeldbezieher sind in der Regel nicht zusätzlich für Bürgergeld berechtigt.",
      };
    }

    // 9. Check household members (must be >= 1)
    if (!formData.anzahlPersonenHaushalt || formData.anzahlPersonenHaushalt < 1) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Haushaltsangaben sind erforderlich.",
      };
    }

    // 10. Check assets limit (simplified - actual limits are complex and depend on household)
    // Basic limit is around 15,000 EUR for single person, plus 500 EUR per year of age
    const assetLimit = 15000 + ((formData.alter || 0) * 500);
    if (formData.vermoegen && formData.vermoegen > assetLimit) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: `Ihr Vermögen (${formData.vermoegen}€) überschreitet die Freigrenze (ca. ${assetLimit.toFixed(0)}€).`,
      };
    }

    // All conditions met
    return {
      eligible: true,
      subsidyName: this.getName(),
      reason:
        "Sie erfüllen die Grundvoraussetzungen für Bürgergeld. Das Bürgergeld ist Teil der Grundsicherung für Arbeitsuchende und sichert Ihren Lebensunterhalt.",
      url: "https://www.arbeitsagentur.de/arbeitslosengeld-2/buergergeld",
    };
  }
}

