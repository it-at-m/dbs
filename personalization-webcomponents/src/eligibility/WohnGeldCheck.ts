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
    // Check if we have the required fields for Wohngeld eligibility
    if (!formData.nettoEinkommenMonatlich || !formData.mieteMietzinsMonatlich) {
      return {
        eligible: false,
        subsidyName: this.getName(),
      };
    }

    // Check if already receiving ALG2 (not eligible for Wohngeld)
    if (formData.beziehtAlg2) {
      return {
        eligible: false,
        subsidyName: this.getName(),
      };
    }

    // Simplified eligibility: low income and paying rent
    const householdSize = formData.anzahlPersonenHaushalt || 1;
    const isEligible = formData.nettoEinkommenMonatlich < 1600 * householdSize && 
                       formData.mieteMietzinsMonatlich > 400;

    if (isEligible) {
      return {
        eligible: true,
        subsidyName: this.getName(),
        reason:
          "Basierend auf Ihren Angaben könnten Sie für Wohngeld berechtigt sein. Das Einkommen liegt unter der Grenze und Sie zahlen Miete.",
        url: "https://www.muenchen.de/rathaus/Stadtverwaltung/Sozialreferat/Wohnungsamt/Mietzuschuss.html",
      };
    }

    return {
      eligible: false,
      subsidyName: this.getName(),
    };
  }
}

