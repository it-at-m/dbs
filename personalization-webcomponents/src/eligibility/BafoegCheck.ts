import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";
import { calculateAge } from "@/eligibility/util.ts";

export class BafoegCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    return this.rules(formData, "BAföG")
      .failIfField(
        "hasFinancialHardship",
        ({ hasFinancialHardship }) => hasFinancialHardship,
        "Bei akuter finanzieller Notlage sind andere Leistungen vorrangig."
      )
      .failIfField(
        "dateOfBirth",
        ({ dateOfBirth }) => {
          const age = calculateAge(dateOfBirth);
          return age < 15 || age >= 36;
        },
        "Das Alter muss zwischen 15 und 35 Jahren liegen."
      )
      .failIfField(
        "healthInsurance",
        ({ healthInsurance }) => healthInsurance === 'none',
        "Eine Krankenversicherung ist erforderlich."
      )
      .failIfField(
        "hasCareInsurance",
        ({ hasCareInsurance }) => !hasCareInsurance,
        "Eine Pflegeversicherung ist erforderlich."
      )
      .failIfField(
        "workAbility",
        ({ workAbility }) => workAbility === 'none',
        "Eine gewisse Arbeitsfähigkeit ist erforderlich."
      )
      .failIfField(
        "employmentStatus",
        ({ employmentStatus }) => employmentStatus !== 'student',
        "Sie müssen sich in einer Ausbildung befinden (Student/in)."
      )
      .failIfField(
        "receivesPension",
        ({ receivesPension }) => receivesPension,
        "Rentenbezieher sind nicht berechtigt."
      )
      .orElseSuccess(() => ({
        reason: "Sie erfüllen die Voraussetzungen für BAföG. Mit dem Bundesausbildungsförderungsgesetz (BAföG) werden junge Menschen finanziell bei der Schulausbildung und dem Studium unterstützt.",
        url: "https://www.bafög.de/",
      }));
  }
}
