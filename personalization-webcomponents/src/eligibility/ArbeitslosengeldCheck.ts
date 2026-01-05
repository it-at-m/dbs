import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";

export class ArbeitslosengeldCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    return this.rules(formData, "Arbeitslosengeld")
      .failIfField(
        "pensionEligible",
        ({ pensionEligible }) => pensionEligible,
        "Personen, die die Altersgrenze für ihre Rente erreicht haben, sind nicht berechtigt."
      )
      .failIfField(
        "residenceStatus",
        ({ residenceStatus }) => residenceStatus === 'none',
        "Sie benötigen die deutsche Staatsbürgerschaft oder eine gültige Aufenthaltserlaubnis/Niederlassungserlaubnis."
      )
      .failIfField(
        "residenceInGermany",
        ({ residenceInGermany }) => !residenceInGermany,
        "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein."
      )
      .failIfField(
        "workAbility",
        ({ workAbility }) => workAbility === 'none',
        "Sie müssen in der Lage sein, mindestens 15 Stunden pro Woche zu arbeiten."
      )
      .failIfField(
        "employmentStatus",
        ({ employmentStatus }) => employmentStatus !== 'unemployed',
        "Arbeitslosengeld ist für arbeitslose Personen vorgesehen."
      )
      .failIfField(
        "receivesUnemploymentBenefit1",
        ({ receivesUnemploymentBenefit1 }) => receivesUnemploymentBenefit1,
        "Sie beziehen bereits Arbeitslosengeld."
      )
      .failIfField(
        "receivesPension",
        ({ receivesPension }) => receivesPension,
        "Rentenbezieher sind nicht für Arbeitslosengeld berechtigt."
      )
      .orElseSuccess(() => ({
        reason: "Sie erfüllen die Grundvoraussetzungen für Arbeitslosengeld. Das Arbeitslosengeld sichert Sie sozial ab und ersetzt einen Teil Ihres fehlenden Einkommens.",
        url: "https://www.arbeitsagentur.de/arbeitslos-arbeit-finden/arbeitslosengeld",
      }));
  }
}
