import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";

export class HilfeZumLebensunterhaltCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Hilfe zum Lebensunterhalt";
  }

  evaluate(formData: FormData): EligibilityResult {
    const missingFields = new Set<FormDataField>();

    // 1. Check habitual residence in Germany
    if (formData.residenceInGermany === undefined || formData.residenceInGermany === null) {
      missingFields.add("residenceInGermany");
    } else if (!formData.residenceInGermany) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein.",
      };
    }

    // 2. Check age (birth date required)
    if (formData.age === undefined || formData.age === null) {
      missingFields.add("age");
    }

    // 3. Check not pensionable (must NOT have reached pension age)
    if (formData.pensionEligible === undefined || formData.pensionEligible === null) {
      missingFields.add("pensionEligible");
    } else if (formData.pensionEligible) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Personen, die die Altersgrenze erreicht haben, sollten Grundsicherung im Alter beantragen.",
      };
    }

    // 4. Check work ability (must be none - fully incapacitated)
    if (formData.workAbility === undefined || formData.workAbility === null) {
      missingFields.add("workAbility");
    } else if (formData.workAbility !== 'none') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Hilfe zum Lebensunterhalt ist für Personen vorgesehen, die vorübergehend nicht arbeiten können.",
      };
    }

    // 5. Check NOT receiving unemployment benefit or citizen's income
    if (formData.receivesUnemploymentBenefit1 === undefined || formData.receivesUnemploymentBenefit1 === null) {
      missingFields.add("receivesUnemploymentBenefit1");
    } else if (formData.receivesUnemploymentBenefit1) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Bezieher von Arbeitslosengeld sind nicht für Hilfe zum Lebensunterhalt berechtigt.",
      };
    }

    if (formData.receivesUnemploymentBenefit2 === undefined || formData.receivesUnemploymentBenefit2 === null) {
      missingFields.add("receivesUnemploymentBenefit2");
    } else if (formData.receivesUnemploymentBenefit2) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Bezieher von Bürgergeld sind nicht für Hilfe zum Lebensunterhalt berechtigt.",
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
      reason: "Sie erfüllen die Grundvoraussetzungen für Hilfe zum Lebensunterhalt. Diese Leistung richtet sich an Menschen, die vorübergehend nicht arbeiten können.",
      url: "https://www.bmas.de/DE/Soziales/Sozialhilfe/Leistungen-der-Sozialhilfe/hilfe-zum-lebensunterhalt.html",
    };
  }
}

