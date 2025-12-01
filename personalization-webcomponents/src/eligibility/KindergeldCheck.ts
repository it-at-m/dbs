import type {
  EligibilityCheckInterface,
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

export class KindergeldCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Kindergeld";
  }

  evaluate(formData: FormData): EligibilityResult {
    // POC: Trigger if last name is mueller
    const isEligible = formData.lastName.toLowerCase() === "mueller";

    if (isEligible) {
      return {
        eligible: true,
        subsidyName: this.getName(),
        reason:
          "Kindergeld steht Eltern oder Erziehungsberechtigten zu. Sie könnten anspruchsberechtigt sein.",
        url: "https://www.muenchen.de/rathaus/Stadtverwaltung/Sozialreferat/Jugendamt/Leistungen/Kindergeld.html",
      };
    }

    return {
      eligible: false,
      subsidyName: this.getName(),
    };
  }
}

