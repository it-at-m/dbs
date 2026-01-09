import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";

export class KindergeldCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    const builder = this.rules(formData, "Kindergeld")
      .failIfField(
        "householdSize",
        ({ householdSize }) => householdSize === 1,
        "Kindergeld wird für Kinder gezahlt."
      );

    if (formData.householdSize !== 1) {
      builder.failIfField(
        "numberOfChildren",
        ({ numberOfChildren }) => numberOfChildren <= 0,
        "Sie haben keine Kinder angegeben."
      );
    }

    return builder.orElseSuccess(() => {
      const count = formData.numberOfChildren || 0;
      return {
        reason: `Sie haben ${count} ${count === 1 ? 'Kind' : 'Kinder'} angegeben. Sie könnten für Kindergeld berechtigt sein.`,
        url: "https://www.arbeitsagentur.de/familie-und-kinder/kindergeld",
      };
    });
  }
}
