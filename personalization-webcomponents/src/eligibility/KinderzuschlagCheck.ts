import type {
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { AbstractEligibilityCheck } from "./AbstractEligibilityCheck";

export class KinderzuschlagCheck extends AbstractEligibilityCheck {
  evaluate(
    formData: FormData
  ): EligibilityResult {
    const builder = this.rules(formData, "Kinderzuschlag")
      .failIfField(
        "householdSize",
        ({ householdSize }) => householdSize === 1,
        "Für Kinderzuschlag müssen Kinder im Haushalt leben."
      );

    if (formData.householdSize !== 1) {
      builder.failIfField(
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
        );
    }
    builder.failIfField(
      "residenceInGermany",
      ({ residenceInGermany }) => !residenceInGermany,
      "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein."
    );

    builder.failIfField("nationality", () => false, "");

    if (formData.nationality !== 'German') {
      builder.failIfField(
        "residenceStatus",
        ({ residenceStatus }) => residenceStatus === 'none',
        "Sie benötigen die deutsche Staatsbürgerschaft oder eine gültige Aufenthaltserlaubnis/Niederlassungserlaubnis."
      );
    }

    return builder
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
      .orElseSuccess(() => {
        const count = formData.numberOfChildren || 0;
        return {
          reason: `Sie haben ${count} ${count === 1 ? 'Kind' : 'Kinder'} und beziehen Kindergeld. Sie könnten für Kinderzuschlag berechtigt sein.`,
          url: "https://www.arbeitsagentur.de/familie-und-kinder/kinderzuschlag",
        };
      });
  }
}
