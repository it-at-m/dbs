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
    // POC: Trigger if last name is söder
    const isEligible = formData.lastName.toLowerCase() === "söder";

    if (isEligible) {
      return {
        eligible: true,
        subsidyName: this.getName(),
        reason:
          "Da sie Söder heißen, sind sie für Kindergeld berechtigt.",
        url: "https://www.muenchen.de/rathaus/Stadtverwaltung/Sozialreferat/Jugendamt/Leistungen/Kindergeld.html",
      };
    }

    return {
      eligible: false,
      reason: "Da sie nicht Söder heißen, sind sie für Kindergeld nicht berechtigt.",
      subsidyName: this.getName(),
    };
  }
}

