import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";

export class GrundsicherungCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    return this.rules(formData, "Grundsicherung im Alter und bei Erwerbsminderung")
      .failIfField(
        "residenceInGermany",
        ({ residenceInGermany }) => !residenceInGermany,
        "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein."
      )
      .failIfField(
        "hasFinancialHardship",
        ({ hasFinancialHardship }) => !hasFinancialHardship,
        "Grundsicherung ist für Personen in finanzieller Notlage vorgesehen."
      )
      .failIfFields(
        ["workAbility", "employmentStatus"] as const,
        ({ workAbility, employmentStatus }) => {
          const isFullyDisabled = workAbility === 'none';
          const isRetired = employmentStatus === 'retired';
          return !isFullyDisabled && !isRetired;
        },
        "Sie müssen entweder das Rentenalter erreicht haben oder dauerhaft voll erwerbsgemindert sein."
      )
      .failIfField(
        "receivesUnemploymentBenefit1",
        ({ receivesUnemploymentBenefit1 }) => receivesUnemploymentBenefit1,
        "Bezieher von Arbeitslosengeld I sind nicht berechtigt."
      )
      .failIfField(
        "receivesStudentAid",
        ({ receivesStudentAid }) => receivesStudentAid,
        "BAföG-Bezieher sind nicht berechtigt."
      )
      .orElseSuccess(({ employmentStatus }) => {
        const isRetired = employmentStatus === 'retired';
        const reasonText = isRetired
          ? "Sie haben das Rentenalter erreicht und befinden sich in finanzieller Notlage."
          : "Sie sind dauerhaft voll erwerbsgemindert und befinden sich in finanzieller Notlage.";

        return {
          reason: `${reasonText} Sie könnten für Grundsicherung im Alter und bei Erwerbsminderung berechtigt sein.`,
          url: "https://www.deutsche-rentenversicherung.de/DRV/DE/Rente/Grundsicherung/grundsicherung_node.html",
        };
      });
  }
}
