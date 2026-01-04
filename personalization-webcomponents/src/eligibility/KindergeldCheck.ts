import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";





export class KindergeldCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Kindergeld";
  }

  evaluate(formData: FormData): EligibilityResult {
    // Start with empty missing fields and build it up
    const missingFields = new Set<FormDataField>();

    // 1. Check if they have children
    if (formData.numberOfChildren === undefined || formData.numberOfChildren === null) {
      missingFields.add("numberOfChildren");
      missingFields.add( "householdSize")
    } else if (formData.numberOfChildren <= 0) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie haben keine Kinder angegeben.",
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
    const childCount = formData.numberOfChildren || 0;
    return {
      eligible: true,
      subsidyName: this.getName(),
      reason:
        `Sie haben ${childCount} ${childCount === 1 ? 'Kind' : 'Kinder'} angegeben. Sie könnten für Kindergeld berechtigt sein.`,
      url: "https://www.arbeitsagentur.de/familie-und-kinder/kindergeld",
    };
  }
}
