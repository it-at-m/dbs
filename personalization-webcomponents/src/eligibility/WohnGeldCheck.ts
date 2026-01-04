import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";





export class WohnGeldCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Wohngeld";
  }

  evaluate(formData: FormData): EligibilityResult {
    // Start with empty missing fields and build it up
    const missingFields = new Set<FormDataField>();

    // Check all attributes systematically
    
    // 1. Check receivesUnemploymentBenefit2
    if (formData.receivesUnemploymentBenefit2 === undefined || formData.receivesUnemploymentBenefit2 === null) {
      missingFields.add('receivesUnemploymentBenefit2');
    } else if (formData.receivesUnemploymentBenefit2 === true) {
      // Immediately disqualified - can return false in between
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Bezieher von Arbeitslosengeld II sind nicht berechtigt für Wohngeld.",
      };
    }

    // 2. Check monthlyRent
    if (formData.monthlyRent === undefined || formData.monthlyRent === null) {
      missingFields.add('monthlyRent');
    } else if (formData.monthlyRent <= 400) {
      // Immediately disqualified - can return false in between
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Die Miete ist zu niedrig für Wohngeld.",
      };
    }

    // 3. Check householdSize
    if (formData.householdSize === undefined || formData.householdSize === null) {
      missingFields.add('householdSize');
    }

    // 4. Check netMonthlyIncome
    if (formData.netMonthlyIncome === undefined || formData.netMonthlyIncome === null) {
      missingFields.add('netMonthlyIncome');
    } else if (formData.householdSize !== undefined && formData.householdSize !== null) {
      // Can only check income threshold if household size is known
      if (formData.netMonthlyIncome >= 1600 * formData.householdSize) {
        // Immediately disqualified - can return false in between
        return {
          eligible: false,
          subsidyName: this.getName(),
          reason: "Das Einkommen liegt über der Grenze.",
        };
      }
    }

    // Only return missingFields at the end
    if (missingFields.size > 0) {
      return {
        missingFields,
        subsidyName: this.getName(),
        reason: "Bitte geben Sie alle erforderlichen Informationen an.",
      };
    }

    // Only return eligible at the end
    return {
      eligible: true,
      subsidyName: this.getName(),
      reason:
        "Basierend auf Ihren Angaben könnten Sie für Wohngeld berechtigt sein. Das Einkommen liegt unter der Grenze und Sie zahlen Miete.",
      url: "https://www.muenchen.de/rathaus/Stadtverwaltung/Sozialreferat/Wohnungsamt/Mietzuschuss.html",
    };
  }
}
