import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";

export class BerufsausbildungsbeihilfeCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    return this.rules(formData, "Berufsausbildungsbeihilfe (BAB)")
      .failIfField(
        "pensionEligible",
        ({ pensionEligible }) => pensionEligible,
        "Personen im Rentenalter sind nicht für BAB berechtigt."
      )
      .failIfField(
        "residenceInGermany",
        ({ residenceInGermany }) => !residenceInGermany,
        "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein."
      )
      .failIfField(
        "employmentStatus",
        ({ employmentStatus }) => employmentStatus !== 'student',
        "BAB ist für Personen in einer Berufsausbildung vorgesehen."
      )
      .failIfField(
        "receivesUnemploymentBenefit1",
        ({ receivesUnemploymentBenefit1 }) => receivesUnemploymentBenefit1,
        "Bezieher von Arbeitslosengeld sind nicht für BAB berechtigt."
      )
      .failIfField(
        "receivesStudentAid",
        ({ receivesStudentAid }) => receivesStudentAid,
        "BAföG-Bezieher sind nicht für BAB berechtigt."
      )
      .failIfField(
        "receivesPension",
        ({ receivesPension }) => receivesPension,
        "Rentenbezieher sind nicht für BAB berechtigt."
      )
      .failIfField(
        "livesWithParents",
        ({ livesWithParents }) => livesWithParents,
        "BAB wird in der Regel nur gewährt, wenn Sie nicht bei Ihren Eltern wohnen."
      )
      .orElseSuccess(() => ({
        reason: "Sie erfüllen die Grundvoraussetzungen für BAB. Diese Leistung hilft jungen Menschen in ihrer ersten Berufsausbildung.",
        url: "https://www.arbeitsagentur.de/bildung/ausbildung/berufsausbildungsbeihilfe-bab",
      }));
  }
}
