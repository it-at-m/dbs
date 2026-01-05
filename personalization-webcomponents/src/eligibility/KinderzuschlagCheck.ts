import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";

export class KinderzuschlagCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    return this.rules(formData, "Kinderzuschlag")
      .failIfField(
        "numberOfChildren",
        ({ numberOfChildren }) => numberOfChildren <= 0,
        "Sie haben keine Kinder angegeben."
      )
      .failIfField(
        "childrenAges",
        ({ childrenAges }) => {
          if (!childrenAges || childrenAges.length === 0) return true;
          return !childrenAges.some(age => age < 25);
        },
        "Kinderzuschlag wird nur für Kinder unter 25 Jahren gewährt."
      )
      .failIfField(
        "residenceInGermany",
        ({ residenceInGermany }) => !residenceInGermany,
        "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein."
      )
      .failIfFields(
        ["nationality", "residenceStatus"] as const,
        ({ nationality, residenceStatus }) => 
          nationality !== 'German' && residenceStatus === 'none',
        "Sie benötigen die deutsche Staatsbürgerschaft oder eine gültige Aufenthaltserlaubnis/Niederlassungserlaubnis."
      )
      .failIfFields(
        ["isSingleParent", "grossMonthlyIncome"] as const,
        ({ isSingleParent, grossMonthlyIncome }) => {
          if (isSingleParent) {
            return grossMonthlyIncome < 600;
          } else {
            return grossMonthlyIncome < 900;
          }
        },
        formData.isSingleParent
          ? "Als Alleinerziehende/r müssen Sie mindestens 600€ Einkommen haben."
          : "Paare müssen mindestens 900€ Einkommen haben."
      )
      .failIfField(
        "receivesChildBenefit",
        ({ receivesChildBenefit }) => !receivesChildBenefit,
        "Sie müssen Kindergeld beziehen, um Kinderzuschlag zu erhalten."
      )
      .failIfField(
        "receivesUnemploymentBenefit2",
        ({ receivesUnemploymentBenefit2 }) => receivesUnemploymentBenefit2,
        "Bezieher von Bürgergeld sind nicht für Kinderzuschlag berechtigt."
      )
      .orElseSuccess(({ numberOfChildren }) => ({
        reason: `Sie haben ${numberOfChildren} ${numberOfChildren === 1 ? 'Kind' : 'Kinder'} und beziehen Kindergeld. Sie könnten für Kinderzuschlag berechtigt sein.`,
        url: "https://www.arbeitsagentur.de/familie-und-kinder/kinderzuschlag",
      }));
  }
}
