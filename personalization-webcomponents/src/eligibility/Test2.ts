import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";





export class Test2 implements EligibilityCheckInterface {
  getName(): string {
    return "Test2";
  }

  evaluate(formData: FormData): EligibilityResult {
    // Start with empty missing fields and build it up
    const missingFields = new Set<FormDataField>();

    if (formData.nationality === undefined) {
      missingFields.add("nationality");
    } else if (formData.nationality === "German") {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason:
          "nicht für deutsche",
      };
    }

    // 1. Check financial difficulty (must be true)
    if (formData.householdSize === undefined) {
      missingFields.add("householdSize");
    } else if (formData.householdSize < 1000) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "arm assets",
      };
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
        "Sie erfüllen die Grundvoraussetzungen für Bürgergeld. Das Bürgergeld ist Teil der Grundsicherung für Arbeitsuchende und sichert Ihren Lebensunterhalt.",
      url: "https://www.arbeitsagentur.de/arbeitslosengeld-2/buergergeld",
    };
  }
}
