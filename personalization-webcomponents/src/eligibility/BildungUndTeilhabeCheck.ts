import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";

export class BildungUndTeilhabeCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    return this.rules(formData, "Bildung und Teilhabe")
      .failIfField(
        "numberOfChildren",
        ({ numberOfChildren }) => numberOfChildren <= 0,
        "Sie haben keine Kinder angegeben."
      )
      .failIfField(
        "childrenAges",
        ({ childrenAges }) => {
          if (childrenAges.length === 0) return true;
          return !childrenAges.some(age => age < 25);
        },
        "Bildung und Teilhabe wird nur für Kinder unter 25 Jahren gewährt."
      )
      .failIfField(
        "residenceInGermany",
        ({ residenceInGermany }) => !residenceInGermany,
        "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein."
      )
      .failIfFields(
        ["receivesUnemploymentBenefit2", "receivesHousingBenefit"] as const,
        ({ receivesUnemploymentBenefit2, receivesHousingBenefit }) =>
          !receivesUnemploymentBenefit2 && !receivesHousingBenefit,
        "Sie müssen Bürgergeld oder Wohngeld beziehen, um Leistungen für Bildung und Teilhabe zu erhalten."
      )
      .orElseSuccess(({ numberOfChildren }) => ({
        reason: `Sie haben ${numberOfChildren} ${numberOfChildren === 1 ? 'Kind' : 'Kinder'} und beziehen eine berechtigende Leistung. Sie könnten für Leistungen zur Bildung und Teilhabe berechtigt sein.`,
        url: "https://www.bmas.de/DE/Arbeit/Grundsicherung-Buergergeld/Bildungspaket/bildungspaket.html",
      }));
  }
}
