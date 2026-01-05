import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";

export class KindergeldCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    return this.rules(formData, "Kindergeld")
      .failIfField(
        "numberOfChildren",
        ({ numberOfChildren }) => numberOfChildren <= 0,
        "Sie haben keine Kinder angegeben."
      )
      .orElseSuccess(({ numberOfChildren }) => ({
        reason: `Sie haben ${numberOfChildren} ${numberOfChildren === 1 ? 'Kind' : 'Kinder'} angegeben. Sie könnten für Kindergeld berechtigt sein.`,
        url: "https://www.arbeitsagentur.de/familie-und-kinder/kindergeld",
      }));
  }
}
