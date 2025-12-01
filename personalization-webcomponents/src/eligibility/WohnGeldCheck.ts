import type {
  EligibilityCheckInterface,
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

export class WohnGeldCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Wohngeld";
  }

  evaluate(formData: FormData): EligibilityResult {
    const isEligible = formData.firstName.toLowerCase() === "luis";

    if (isEligible) {
      return {
        eligible: true,
        subsidyName: this.getName(),
        reason:
          "Da sie Luis heißen, sind sie für Wohngeld berechtigt.",
        url: "https://www.muenchen.de/rathaus/Stadtverwaltung/Sozialreferat/Wohnungsamt/Mietzuschuss.html",
      };
    }

    return {
      eligible: false,
      subsidyName: this.getName(),
      reason: "Da sie nicht Luis heißen, sind sie für Wohngeld nicht berechtigt.",
    };
  }
}

