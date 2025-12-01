import type {
  EligibilityCheckInterface,
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

export class BaföGCheck implements EligibilityCheckInterface {
  getName(): string {
    return "BAföG";
  }

  evaluate(formData: FormData): EligibilityResult {
    // 1. Check age requirement (15 to 35)
    if (!formData.alter || formData.alter < 15 || formData.alter >= 36) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Das Alter muss zwischen 15 und 35 Jahren liegen.",
      };
    }

    // 2. Check no financial difficulty (must be false)
    if (formData.hatFinanzielleNotlage === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Bei akuter finanzieller Notlage sind andere Leistungen vorrangig.",
      };
    }

    // 3. Check health insurance (must have insurance)
    if (formData.krankenversicherung === 'keine') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Eine Krankenversicherung ist erforderlich.",
      };
    }

    // 4. Check care insurance (must have Pflegeversicherung)
    if (formData.hatPflegeversicherung !== true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Eine Pflegeversicherung ist erforderlich.",
      };
    }

    // 5. Check work ability (must NOT be none)
    if (formData.arbeitsfahigkeit === 'keine') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Eine gewisse Arbeitsfähigkeit ist erforderlich.",
      };
    }

    // 6. Check employment status (must be in education)
    if (formData.beschaeftigungsstatus !== 'student') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie müssen sich in einer Ausbildung befinden (Student/in).",
      };
    }

    // 7. Check NOT receiving pension
    if (formData.beziehtRente === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Rentenbezieher sind nicht berechtigt.",
      };
    }

    // All conditions met
    return {
      eligible: true,
      subsidyName: this.getName(),
      reason:
        "Sie erfüllen die Voraussetzungen für BAföG. Mit dem Bundesausbildungsförderungsgesetz (BAföG) werden junge Menschen finanziell bei der Schulausbildung und dem Studium unterstützt.",
      url: "https://www.bafög.de/",
    };
  }
}

