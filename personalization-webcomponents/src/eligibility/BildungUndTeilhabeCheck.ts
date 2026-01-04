import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";

export class BildungUndTeilhabeCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Bildung und Teilhabe";
  }

  evaluate(formData: FormData): EligibilityResult {
    const missingFields = new Set<FormDataField>();

    // 1. Check if they have children
    if (formData.numberOfChildren === undefined || formData.numberOfChildren === null) {
      missingFields.add("numberOfChildren");
    } else if (formData.numberOfChildren <= 0) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie haben keine Kinder angegeben.",
      };
    }

    // 2. Check children ages (must be under 25)
    if (formData.childrenAges === undefined || formData.childrenAges === null || formData.childrenAges.length === 0) {
      missingFields.add("childrenAges");
    } else {
      const hasEligibleChild = formData.childrenAges.some(age => age < 25);
      if (!hasEligibleChild) {
        return {
          eligible: false,
          subsidyName: this.getName(),
          reason: "Bildung und Teilhabe wird nur für Kinder unter 25 Jahren gewährt.",
        };
      }
    }

    // 3. Check parenting situation (must have children in household)
    if (formData.isSingleParent === undefined || formData.isSingleParent === null) {
      missingFields.add("isSingleParent");
    }
    // Note: isSingleParent being defined means they have children in household

    // 4. Check habitual residence in Germany
    if (formData.residenceInGermany === undefined || formData.residenceInGermany === null) {
      missingFields.add("residenceInGermany");
    } else if (!formData.residenceInGermany) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein.",
      };
    }

    // 5. Check receiving citizen's income OR housing allowance
    if (formData.receivesUnemploymentBenefit2 === undefined || formData.receivesUnemploymentBenefit2 === null) {
      missingFields.add("receivesUnemploymentBenefit2");
    }
    if (formData.receivesHousingBenefit === undefined || formData.receivesHousingBenefit === null) {
      missingFields.add("receivesHousingBenefit");
    }

    // Check if receiving at least one of the required benefits
    if (
      formData.receivesUnemploymentBenefit2 !== undefined && formData.receivesUnemploymentBenefit2 !== null &&
      formData.receivesHousingBenefit !== undefined && formData.receivesHousingBenefit !== null
    ) {
      const receivesEligibleBenefit = formData.receivesUnemploymentBenefit2 || formData.receivesHousingBenefit;
      if (!receivesEligibleBenefit) {
        return {
          eligible: false,
          subsidyName: this.getName(),
          reason: "Sie müssen Bürgergeld oder Wohngeld beziehen, um Leistungen für Bildung und Teilhabe zu erhalten.",
        };
      }
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
    const childCount = formData.numberOfChildren || 0;
    return {
      eligible: true,
      subsidyName: this.getName(),
      reason: `Sie haben ${childCount} ${childCount === 1 ? 'Kind' : 'Kinder'} und beziehen eine berechtigende Leistung. Sie könnten für Leistungen zur Bildung und Teilhabe berechtigt sein.`,
      url: "https://www.bmas.de/DE/Arbeit/Grundsicherung-Buergergeld/Bildungspaket/bildungspaket.html",
    };
  }
}

