import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";

export class HilfeZumLebensunterhaltCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    return this.rules(formData, "Hilfe zum Lebensunterhalt")
      .failIfField(
        "residenceInGermany",
        ({ residenceInGermany }) => !residenceInGermany,
        "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein."
      )
      .failIfField(
        "pensionEligible",
        ({ pensionEligible }) => pensionEligible,
        "Personen, die die Altersgrenze erreicht haben, sollten Grundsicherung im Alter beantragen."
      )
      .failIfField(
        "workAbility",
        ({ workAbility }) => workAbility !== 'none',
        "Hilfe zum Lebensunterhalt ist für Personen vorgesehen, die vorübergehend nicht arbeiten können."
      )
      .failIfField(
        "receivesUnemploymentBenefit1",
        ({ receivesUnemploymentBenefit1 }) => receivesUnemploymentBenefit1,
        "Bezieher von Arbeitslosengeld sind nicht für Hilfe zum Lebensunterhalt berechtigt."
      )
      .failIfField(
        "receivesUnemploymentBenefit2",
        ({ receivesUnemploymentBenefit2 }) => receivesUnemploymentBenefit2,
        "Bezieher von Bürgergeld sind nicht für Hilfe zum Lebensunterhalt berechtigt."
      )
      .orElseSuccess(() => ({
        reason: "Sie erfüllen die Grundvoraussetzungen für Hilfe zum Lebensunterhalt. Diese Leistung richtet sich an Menschen, die vorübergehend nicht arbeiten können.",
        url: "https://www.bmas.de/DE/Soziales/Sozialhilfe/Leistungen-der-Sozialhilfe/hilfe-zum-lebensunterhalt.html",
      }));
  }
}
