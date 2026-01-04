import type {
  EligibilityCheckInterface,
  EligibilityResult,
  FormData,
  FormDataField,
} from "@/types/EligibilityCheckInterface";

export class BürgergeldCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Bürgergeld";
  }

  evaluate(formData: FormData): EligibilityResult {
    // Start with empty missing fields and build it up
    const missingFields = new Set<FormDataField>();

    // 1. Check financial difficulty (must be true)
    if (formData.hasFinancialHardship === undefined || formData.hasFinancialHardship === null) {
      missingFields.add('hasFinancialHardship');
    } else if (formData.hasFinancialHardship !== true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Bürgergeld ist für Personen in finanzieller Notlage vorgesehen.",
      };
    }

    // 2. Check citizenship
    if (formData.nationality === undefined || formData.nationality === null) {
      missingFields.add('nationality');
    }

    // 3. Check residency status (only if not German)
    if (formData.nationality !== 'Deutsch' && formData.nationality !== undefined && formData.nationality !== null) {
      if (formData.residenceStatus === undefined || formData.residenceStatus === null) {
        missingFields.add('residenceStatus');
      } else {
        const hasValidResidency = 
          formData.residenceStatus === 'Aufenthaltserlaubnis' || 
          formData.residenceStatus === 'Niederlassungserlaubnis';
        
        if (!hasValidResidency) {
          return {
            eligible: false,
            subsidyName: this.getName(),
            reason: "Sie benötigen die deutsche Staatsbürgerschaft oder eine gültige Aufenthaltserlaubnis/Niederlassungserlaubnis.",
          };
        }
      }
    }

    // 4. Check habitual residence in Germany
    if (formData.residenceInGermany === undefined || formData.residenceInGermany === null) {
      missingFields.add('residenceInGermany');
    } else if (formData.residenceInGermany !== true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Ihr gewöhnlicher Aufenthalt muss in Deutschland sein.",
      };
    }

    // 5. Check age requirement (must be >= 15)
    if (formData.age === undefined || formData.age === null) {
      missingFields.add('age');
    } else if (formData.age < 15) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie müssen mindestens 15 Jahre alt sein.",
      };
    }

    // 6. Check not pensionable (not reached pension age)
    if (formData.pensionEligible === undefined || formData.pensionEligible === null) {
      missingFields.add('pensionEligible');
    } else if (formData.pensionEligible === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Personen, die die Altersgrenze für ihre Rente erreicht haben, sind nicht berechtigt.",
      };
    }

    // 7. Check work ability (must NOT be none)
    if (formData.workAbility === undefined || formData.workAbility === null) {
      missingFields.add('workAbility');
    } else if (formData.workAbility === 'keine') {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie müssen in der Lage sein, mindestens 3 Stunden täglich zu arbeiten.",
      };
    }

    // 8. Check employment status (must NOT be in education, self-employed, or pension)
    if (formData.employmentStatus === undefined || formData.employmentStatus === null) {
      missingFields.add('employmentStatus');
    } else if (
      formData.employmentStatus === 'student' ||
      formData.employmentStatus === 'selbststaendig' ||
      formData.employmentStatus === 'rentner'
    ) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Studierende, Selbständige und Rentner sind nicht berechtigt.",
      };
    }

    // 9. Check NOT already receiving conflicting benefits
    if (formData.receivesUnemploymentBenefit2 === undefined || formData.receivesUnemploymentBenefit2 === null) {
      missingFields.add('receivesUnemploymentBenefit2');
    } else if (formData.receivesUnemploymentBenefit2 === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Sie beziehen bereits Bürgergeld.",
      };
    }

    if (formData.receivesPension === undefined || formData.receivesPension === null) {
      missingFields.add('receivesPension');
    } else if (formData.receivesPension === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Rentenbezieher sind nicht berechtigt.",
      };
    }

    if (formData.receivesHousingBenefit === undefined || formData.receivesHousingBenefit === null) {
      missingFields.add('receivesHousingBenefit');
    } else if (formData.receivesHousingBenefit === true) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Wohngeldbezieher sind in der Regel nicht zusätzlich für Bürgergeld berechtigt.",
      };
    }

    // 10. Check household members (must be >= 1)
    if (formData.householdSize === undefined || formData.householdSize === null) {
      missingFields.add('householdSize');
    } else if (formData.householdSize < 1) {
      return {
        eligible: false,
        subsidyName: this.getName(),
        reason: "Haushaltsangaben sind erforderlich.",
      };
    }

    // 11. Check assets limit (simplified - actual limits are complex and depend on household)
    if (formData.assets === undefined || formData.assets === null) {
      missingFields.add('assets');
    } else if (formData.age !== undefined && formData.age !== null) {
      // Can only check asset limit if age is known
      const assetLimit = 15000 + (formData.age * 500);
      if (formData.assets > assetLimit) {
        return {
          eligible: false,
          subsidyName: this.getName(),
          reason: `Ihr Vermögen (${formData.assets}€) überschreitet die Freigrenze (ca. ${assetLimit.toFixed(0)}€).`,
        };
      }
    }

    // Only return missingFields at the end
    if (missingFields.size > 0) {
      return {
        missingFields,
        subsidyName: this.getName(),
        reason: "Bitte geben Sie alle erforderlichen Informationen an.",
      };
    }

    // Only return eligible at the end
    return {
      eligible: true,
      subsidyName: this.getName(),
      reason:
        "Sie erfüllen die Grundvoraussetzungen für Bürgergeld. Das Bürgergeld ist Teil der Grundsicherung für Arbeitsuchende und sichert Ihren Lebensunterhalt.",
      url: "https://www.arbeitsagentur.de/arbeitslosengeld-2/buergergeld",
    };
  }
}
