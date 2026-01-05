import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";

export class WohnGeldCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    return this.rules(formData, "Wohngeld")
      .failIfField(
        "receivesUnemploymentBenefit2",
        ({ receivesUnemploymentBenefit2 }) => receivesUnemploymentBenefit2,
        "Bezieher von Arbeitslosengeld II sind nicht berechtigt für Wohngeld."
      )
      .failIfField(
        "monthlyRent",
        ({ monthlyRent }) => monthlyRent <= 400,
        "Die Miete ist zu niedrig für Wohngeld."
      )
      .failIfFields(
        ["householdSize", "netMonthlyIncome"] as const,
        ({ householdSize, netMonthlyIncome }) =>
          netMonthlyIncome >= 1600 * householdSize,
        "Das Einkommen liegt über der Grenze."
      )
      .orElseSuccess(() => ({
        reason:
          "Basierend auf Ihren Angaben könnten Sie für Wohngeld berechtigt sein.",
        url: "https://www.muenchen.de/rathaus/Stadtverwaltung/Sozialreferat/Wohnungsamt/Mietzuschuss.html",
      }));
  }
}
