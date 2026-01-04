import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";

export class ArbeitslosengeldCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Arbeitslosengeld";
  }

  evaluate(formData: FormData): EligibilityResult {
    const missingFields = new Set<FormDataField>();

    // 2. Check not pensionable (must NOT have reached pension age)
    if (formData.pensionEligible === undefined || formData.pensionEligible === null) {
      missingFields.add("pensionEligible");
    } else if (formData.pensionEligible) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Personen, die die Altersgrenze für ihre Rente erreicht haben, sind nicht berechtigt.",
      };
    }

    // 3. Check citizenship
    if (formData.nationality === undefined || formData.nationality === null) {
      missingFields.add("nationality");
    }

    // 4. Check residency status for non-German citizens
    if (formData.nationality !== 'German' && formData.nationality !== undefined && formData.nationality !== null) {
      if (formData.residenceStatus === undefined || formData.residenceStatus === null) {
        missingFields.add("residenceStatus");
      } else if (formData.residenceStatus === 'none') {
        return {
          eligible: false,
          subsidyName: this.getName(),
          reason: "Sie benötigen die deutsche Staatsbürgerschaft oder eine gültige Aufenthaltserlaubnis/Niederlassungserlaubnis.",
        };
      }
    }

    // 5. Check habitual residence in Germany
    if (formData.residenceInGermany === undefined || formData.residenceInGermany === null) {
      missingFields.add("residenceInGermany");
    } else if (!formData.residenceInGermany) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein.",
      };
    }

    // 6. Check work ability (must NOT be none - must be able to work at least 15h/week)
    if (formData.workAbility === undefined || formData.workAbility === null) {
      missingFields.add("workAbility");
    } else if (formData.workAbility === 'none') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie müssen in der Lage sein, mindestens 15 Stunden pro Woche zu arbeiten.",
      };
    }

    // 7. Check employment status (must be unemployed)
    if (formData.employmentStatus === undefined || formData.employmentStatus === null) {
      missingFields.add("employmentStatus");
    } else if (formData.employmentStatus !== 'unemployed') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Arbeitslosengeld ist für arbeitslose Personen vorgesehen.",
      };
    }

    // 8. Check NOT already receiving ALG1
    if (formData.receivesUnemploymentBenefit1 === undefined || formData.receivesUnemploymentBenefit1 === null) {
      missingFields.add("receivesUnemploymentBenefit1");
    } else if (formData.receivesUnemploymentBenefit1) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie beziehen bereits Arbeitslosengeld.",
      };
    }

    // 9. Check NOT receiving pension
    if (formData.receivesPension === undefined || formData.receivesPension === null) {
      missingFields.add("receivesPension");
    } else if (formData.receivesPension) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Rentenbezieher sind nicht für Arbeitslosengeld berechtigt.",
      };
    }

    // Return missing fields if any
    if (missingFields.size > 0) {
      return {
        missingFields,
        subsidyName: this.getName(),
        reason: "Bitte geben Sie alle erforderlichen Informationen an.",
      };
    }

    // Return eligible
    return {
      eligible: true,
      subsidyName: this.getName(),
      reason: "Sie erfüllen die Grundvoraussetzungen für Arbeitslosengeld. Das Arbeitslosengeld sichert Sie sozial ab und ersetzt einen Teil Ihres fehlenden Einkommens.",
      url: "https://www.arbeitsagentur.de/arbeitslos-arbeit-finden/arbeitslosengeld",
    };
  }
}

