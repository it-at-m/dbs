import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";
import { calculateAge } from "@/eligibility/util.ts";

export class BuergergeldCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    return this.rules(formData, "Bürgergeld")
      .failIfField(
        "nationality",
        ({ nationality }) => nationality !== "German",
        "Sie benötigen die deutsche Staatsbürgerschaft oder eine gültige Aufenthaltserlaubnis/Niederlassungserlaubnis."
      )
      .failIfField(
        "hasFinancialHardship",
        ({ hasFinancialHardship }) => !hasFinancialHardship,
        "Bürgergeld ist für Personen in finanzieller Notlage vorgesehen."
      )
      .failIfField(
        "residenceInGermany",
        ({ residenceInGermany }) => !residenceInGermany,
        "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein."
      )
      .failIfField(
        "dateOfBirth",
        ({ dateOfBirth }) => {
          const age = calculateAge(dateOfBirth);
          return age < 15;
        },
        "Sie müssen mindestens 15 Jahre alt sein."
      )
      .failIfField(
        "pensionEligible",
        ({ pensionEligible }) => pensionEligible,
        "Personen, die die Altersgrenze für ihre Rente erreicht haben, sind nicht berechtigt."
      )
      .failIfField(
        "workAbility",
        ({ workAbility }) => workAbility === 'none',
        "Sie müssen in der Lage sein, mindestens 3 Stunden täglich zu arbeiten."
      )
      .failIfField(
        "employmentStatus",
        ({ employmentStatus }) =>
          employmentStatus === 'student' ||
          employmentStatus === 'self_employed' ||
          employmentStatus === 'retired',
        "Studierende, Selbständige und Rentner sind nicht berechtigt."
      )
      .failIfField(
        "receivesUnemploymentBenefit2",
        ({ receivesUnemploymentBenefit2 }) => receivesUnemploymentBenefit2,
        "Sie beziehen bereits Bürgergeld."
      )
      .failIfField(
        "receivesPension",
        ({ receivesPension }) => receivesPension,
        "Rentenbezieher sind nicht berechtigt."
      )
      .failIfField(
        "receivesHousingBenefit",
        ({ receivesHousingBenefit }) => receivesHousingBenefit,
        "Wohngeldbezieher sind in der Regel nicht zusätzlich für Bürgergeld berechtigt."
      )
      .failIfField(
        "householdSize",
        ({ householdSize }) => householdSize < 1,
        "Haushaltsangaben sind erforderlich."
      )
      .failIfFields(
        ["dateOfBirth", "assets"] as const,
        ({ dateOfBirth, assets }) => {
          const age = calculateAge(dateOfBirth);
          const assetLimit = 15000 + (age! * 500);
          return assets > assetLimit;
        },
        formData.dateOfBirth && formData.assets
          ? `Ihr Vermögen (${formData.assets}€) überschreitet die Freigrenze (ca. ${(15000 + (calculateAge(formData.dateOfBirth)! * 500)).toFixed(0)}€).`
          : "Ihr Vermögen überschreitet die Freigrenze."
      )
      .orElseSuccess(() => ({
      reason: "Sie erfüllen die Grundvoraussetzungen für Bürgergeld. Das Bürgergeld ist Teil der Grundsicherung für Arbeitsuchende und sichert Ihren Lebensunterhalt.",
      url: "https://www.arbeitsagentur.de/arbeitslosengeld-2/buergergeld",
    }));
  }
}
