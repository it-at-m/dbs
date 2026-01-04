import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";

export class BaföGCheck implements EligibilityCheckInterface {
  getName(): string {
    return "BAföG";
  }

  evaluate(formData: FormData): EligibilityResult {
    // Start with empty missing fields and build it up
    const missingFields = new Set<FormDataField>();

    // 1. Check no financial difficulty (must be false)
    if (formData.hasFinancialHardship === undefined || formData.hasFinancialHardship === null) {
      missingFields.add('hasFinancialHardship');
    } else if (formData.hasFinancialHardship === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Bei akuter finanzieller Notlage sind andere Leistungen vorrangig.",
      };
    }

    // 2. Check health insurance (must have insurance)
    if (formData.healthInsurance === undefined || formData.healthInsurance === null) {
      missingFields.add('healthInsurance');
    } else if (formData.healthInsurance === 'keine') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Eine Krankenversicherung ist erforderlich.",
      };
    }

    // 3. Check age requirement (15 to 35)
    if (formData.age === undefined || formData.age === null) {
      missingFields.add('age');
    } else if (formData.age < 15 || formData.age >= 36) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Das Alter muss zwischen 15 und 35 Jahren liegen.",
      };
    }

    // 4. Check care insurance (must have Pflegeversicherung)
    if (formData.hasCareInsurance === undefined || formData.hasCareInsurance === null) {
      missingFields.add('hasCareInsurance');
    } else if (formData.hasCareInsurance !== true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Eine Pflegeversicherung ist erforderlich.",
      };
    }

    // 5. Check work ability (must NOT be none)
    if (formData.workAbility === undefined || formData.workAbility === null) {
      missingFields.add('workAbility');
    } else if (formData.workAbility === 'keine') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Eine gewisse Arbeitsfähigkeit ist erforderlich.",
      };
    }

    // 6. Check employment status (must be in education)
    if (formData.employmentStatus === undefined || formData.employmentStatus === null) {
      missingFields.add('employmentStatus');
    } else if (formData.employmentStatus !== 'student') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie müssen sich in einer Ausbildung befinden (Student/in).",
      };
    }

    // 7. Check NOT receiving pension
    if (formData.receivesPension === undefined || formData.receivesPension === null) {
      missingFields.add('receivesPension');
    } else if (formData.receivesPension === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Rentenbezieher sind nicht berechtigt.",
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
        "Sie erfüllen die Voraussetzungen für BAföG. Mit dem Bundesausbildungsförderungsgesetz (BAföG) werden junge Menschen finanziell bei der Schulausbildung und dem Studium unterstützt.",
      url: "https://www.bafög.de/",
    };
  }
}
