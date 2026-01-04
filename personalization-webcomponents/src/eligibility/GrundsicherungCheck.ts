import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";

export class GrundsicherungCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Grundsicherung im Alter und bei Erwerbsminderung";
  }

  evaluate(formData: FormData): EligibilityResult {
    const missingFields = new Set<FormDataField>();

    // 1. Check age (birth date required)
    if (formData.age === undefined || formData.age === null) {
      missingFields.add("age");
    }

    // 2. Check habitual residence in Germany
    if (formData.residenceInGermany === undefined || formData.residenceInGermany === null) {
      missingFields.add("residenceInGermany");
    } else if (!formData.residenceInGermany) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein.",
      };
    }

    // 3. Check financial difficulty (must be true)
    if (formData.hasFinancialHardship === undefined || formData.hasFinancialHardship === null) {
      missingFields.add("hasFinancialHardship");
    } else if (!formData.hasFinancialHardship) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Grundsicherung ist für Personen in finanzieller Notlage vorgesehen.",
      };
    }

    // 4. Check work ability
    if (formData.workAbility === undefined || formData.workAbility === null) {
      missingFields.add("workAbility");
    }

    // 5. Check employment status
    if (formData.employmentStatus === undefined || formData.employmentStatus === null) {
      missingFields.add("employmentStatus");
    }

    // 6. Check eligibility condition: either fully disabled (workAbility = none) OR retired
    if (
      formData.workAbility !== undefined && formData.workAbility !== null &&
      formData.employmentStatus !== undefined && formData.employmentStatus !== null
    ) {
      const isFullyDisabled = formData.workAbility === 'none';
      const isRetired = formData.employmentStatus === 'retired';

      if (!isFullyDisabled && !isRetired) {
        return {
          eligible: false,
          subsidyName: this.getName(),
          reason: "Sie müssen entweder das Rentenalter erreicht haben oder dauerhaft voll erwerbsgemindert sein.",
        };
      }
    }

    // 7. Check NOT receiving unemployment benefit (ALG1)
    if (formData.receivesUnemploymentBenefit1 === undefined || formData.receivesUnemploymentBenefit1 === null) {
      missingFields.add("receivesUnemploymentBenefit1");
    } else if (formData.receivesUnemploymentBenefit1) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Bezieher von Arbeitslosengeld I sind nicht berechtigt.",
      };
    }

    // 8. Check NOT receiving BAföG
    if (formData.receivesStudentAid === undefined || formData.receivesStudentAid === null) {
      missingFields.add("receivesStudentAid");
    } else if (formData.receivesStudentAid) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "BAföG-Bezieher sind nicht berechtigt.",
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

    // Determine reason based on eligibility path
    const isRetired = formData.employmentStatus === 'retired';
    const reasonText = isRetired
      ? "Sie haben das Rentenalter erreicht und befinden sich in finanzieller Notlage."
      : "Sie sind dauerhaft voll erwerbsgemindert und befinden sich in finanzieller Notlage.";

    // Return eligible
    return {
      eligible: true,
      subsidyName: this.getName(),
      reason: `${reasonText} Sie könnten für Grundsicherung im Alter und bei Erwerbsminderung berechtigt sein.`,
      url: "https://www.deutsche-rentenversicherung.de/DRV/DE/Rente/Grundsicherung/grundsicherung_node.html",
    };
  }
}

