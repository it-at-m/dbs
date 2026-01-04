import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";

export class BerufsausbildungsbeihilfeCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Berufsausbildungsbeihilfe (BAB)";
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
        reason: "Personen im Rentenalter sind nicht für BAB berechtigt.",
      };
    }

    // 3. Check habitual residence in Germany
    if (formData.residenceInGermany === undefined || formData.residenceInGermany === null) {
      missingFields.add("residenceInGermany");
    } else if (!formData.residenceInGermany) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein.",
      };
    }

    // 4. Check employment status (must be in education/training)
    if (formData.employmentStatus === undefined || formData.employmentStatus === null) {
      missingFields.add("employmentStatus");
    } else if (formData.employmentStatus !== 'student') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "BAB ist für Personen in einer Berufsausbildung vorgesehen.",
      };
    }

    // 5. Check NOT receiving unemployment benefit, BAföG, or pension
    if (formData.receivesUnemploymentBenefit1 === undefined || formData.receivesUnemploymentBenefit1 === null) {
      missingFields.add("receivesUnemploymentBenefit1");
    } else if (formData.receivesUnemploymentBenefit1) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Bezieher von Arbeitslosengeld sind nicht für BAB berechtigt.",
      };
    }

    if (formData.receivesStudentAid === undefined || formData.receivesStudentAid === null) {
      missingFields.add("receivesStudentAid");
    } else if (formData.receivesStudentAid) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "BAföG-Bezieher sind nicht für BAB berechtigt.",
      };
    }

    if (formData.receivesPension === undefined || formData.receivesPension === null) {
      missingFields.add("receivesPension");
    } else if (formData.receivesPension) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Rentenbezieher sind nicht für BAB berechtigt.",
      };
    }

    // 6. Check does NOT live with parents
    if (formData.livesWithParents === undefined || formData.livesWithParents === null) {
      missingFields.add("livesWithParents");
    } else if (formData.livesWithParents) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "BAB wird in der Regel nur gewährt, wenn Sie nicht bei Ihren Eltern wohnen.",
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
      reason: "Sie erfüllen die Grundvoraussetzungen für BAB. Diese Leistung hilft jungen Menschen in ihrer ersten Berufsausbildung.",
      url: "https://www.arbeitsagentur.de/bildung/ausbildung/berufsausbildungsbeihilfe-bab",
    };
  }
}

