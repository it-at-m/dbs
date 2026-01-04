import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";





export class Test1 implements EligibilityCheckInterface {
  getName(): string {
    return "Test1";
  }

  evaluate(formData: FormData): EligibilityResult {
    // Start with empty missing fields and build it up
    const missingFields = new Set<FormDataField>();

    if (formData.nationality === undefined) {
      missingFields.add("nationality");
    } else if (formData.nationality !== "Deutsch") {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason:
          "nicht deutsch",
      };
    }
    
    // 1. Check financial difficulty (must be true)
    if (formData.netMonthlyIncome === undefined) {
      missingFields.add("netMonthlyIncome");
    } else if (formData.netMonthlyIncome < 1000) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason:
          "arm",
      };
    }
    if (formData.numberOfChildren === undefined){
      missingFields.add("numberOfChildren");
    } else if (formData.numberOfChildren < 3){
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "wenig kinder",
      };
    }

    if (missingFields.size > 0) {
        // Only return missingFields at the end
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
        "yes du bist reich",
      url: "https://www.arbeitsagentur.de/arbeitslosengeld-2/buergergeld",
    };
  }
}
