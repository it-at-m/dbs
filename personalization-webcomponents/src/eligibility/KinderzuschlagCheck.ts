import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";

export class KinderzuschlagCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Kinderzuschlag";
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
          reason: "Kinderzuschlag wird nur für Kinder unter 25 Jahren gewährt.",
        };
      }
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

    // 4. Check citizenship
    if (formData.nationality === undefined || formData.nationality === null) {
      missingFields.add("nationality");
    }

    // 5. Check residency status for non-German citizens
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

    // 6. Check parenting situation
    if (formData.isSingleParent === undefined || formData.isSingleParent === null) {
      missingFields.add("isSingleParent");
    }

    // 7. Check household income based on parenting situation
    if (formData.grossMonthlyIncome === undefined || formData.grossMonthlyIncome === null) {
      missingFields.add("grossMonthlyIncome");
    } else if (formData.isSingleParent !== undefined && formData.isSingleParent !== null) {
      if (formData.isSingleParent) {
        // Single parent: must have at least 600€ income
        if (formData.grossMonthlyIncome < 600) {
          return {
            eligible: false,
            subsidyName: this.getName(),
            reason: "Als Alleinerziehende/r müssen Sie mindestens 600€ Einkommen haben.",
          };
        }
      } else {
        // Co-parenting: must have at least 900€ income
        if (formData.grossMonthlyIncome < 900) {
          return {
            eligible: false,
            subsidyName: this.getName(),
            reason: "Paare müssen mindestens 900€ Einkommen haben.",
          };
        }
      }
    }

    // 8. Check if receiving child benefit (must receive Kindergeld)
    if (formData.receivesChildBenefit === undefined || formData.receivesChildBenefit === null) {
      missingFields.add("receivesChildBenefit");
    } else if (!formData.receivesChildBenefit) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie müssen Kindergeld beziehen, um Kinderzuschlag zu erhalten.",
      };
    }

    // 9. Check NOT receiving Bürgergeld (citizen's income)
    if (formData.receivesUnemploymentBenefit2 === undefined || formData.receivesUnemploymentBenefit2 === null) {
      missingFields.add("receivesUnemploymentBenefit2");
    } else if (formData.receivesUnemploymentBenefit2) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Bezieher von Bürgergeld sind nicht für Kinderzuschlag berechtigt.",
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
    const childCount = formData.numberOfChildren || 0;
    return {
      eligible: true,
      subsidyName: this.getName(),
      reason: `Sie haben ${childCount} ${childCount === 1 ? 'Kind' : 'Kinder'} und beziehen Kindergeld. Sie könnten für Kinderzuschlag berechtigt sein.`,
      url: "https://www.arbeitsagentur.de/familie-und-kinder/kinderzuschlag",
    };
  }
}

