import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";



import { ArbeitslosengeldCheck } from "@/eligibility/ArbeitslosengeldCheck.ts";
import { BafoegCheck } from "@/eligibility/BafoegCheck.ts";
import { BerufsausbildungsbeihilfeCheck } from "@/eligibility/BerufsausbildungsbeihilfeCheck.ts";
import { BildungUndTeilhabeCheck } from "@/eligibility/BildungUndTeilhabeCheck.ts";
import { BuergergeldCheck } from "@/eligibility/BuergergeldCheck.ts";
import { GrundsicherungCheck } from "@/eligibility/GrundsicherungCheck.ts";
import { HilfeZumLebensunterhaltCheck } from "@/eligibility/HilfeZumLebensunterhaltCheck.ts";
import { KindergeldCheck } from "@/eligibility/KindergeldCheck.ts";
import { KinderzuschlagCheck } from "@/eligibility/KinderzuschlagCheck.ts";
import { OrderedNextSectionStrategy } from "@/eligibility/NextSectionStrategy.ts";
import { WohnGeldCheck } from "@/eligibility/WohnGeldCheck.ts";


export interface EligibilityEvaluationResult {
  eligible: EligibilityResult[];
  ineligible: EligibilityResult[];
  all: EligibilityResult[];
  visibleFields: FormDataField[];
  visibleSections: FormSection[];
}

export type PrefilledFields = {
  [K in FormDataField]?: FormData[K];
};

export interface PrefilledEligibilityEvaluationResult {
  eligible: EligibilityResult[];
  ineligible: EligibilityResult[];
  all: EligibilityResult[];
  visibleFields: FormDataField[];
  visibleSections: FormSection[];
  prefilledFields: PrefilledFields;
}

export type FormSection =
  | "personalInfo"
  | "financialInfo"
  | "householdInfo"
  | "educationEmployment"
  | "specialCircumstances"
  | "insuranceBenefits";

export type SectionFieldMap = Record<FormSection, FormDataField[]>;

export class EligibilityCheckRegistry {
  private checks: EligibilityCheckInterface[] = [];
  private visibleSections: FormSection[] = [];
  private visibleFields = new Set<FormDataField>();

  private nextSectionStrategy = new OrderedNextSectionStrategy();

  private sectionFields: SectionFieldMap = {
    personalInfo: [
      "firstName",
      "lastName",
      "age",
      "dateOfBirth",
      "gender",
      "maritalStatus",
      "nationality",
      "residenceStatus",
      "residenceInGermany",
    ],
    financialInfo: [
      "grossMonthlyIncome",
      "netMonthlyIncome",
      "assets",
      "monthlyRent",
    ],
    householdInfo: [
      "householdSize",
      "numberOfChildren",
      "childrenAges",
      "isSingleParent",
      "livesWithParents"
    ],
    educationEmployment: ["employmentStatus", "educationLevel", "isStudent"],
    specialCircumstances: [
      "hasDisability",
      "disabilityDegree",
      "isPregnant",
      "hasCareNeeds",
      "pensionEligible",
      "citizenBenefitLast3Years",
      "hasFinancialHardship",
      "workAbility",
    ],
    insuranceBenefits: [
      "healthInsurance",
      "hasCareInsurance",
      "receivesUnemploymentBenefit1",
      "receivesUnemploymentBenefit2",
      "receivesPension",
      "receivesChildBenefit",
      "receivesHousingBenefit",
      "receivesStudentAid",
    ],
  };

  constructor() {
    // Register all eligibility checks here
    this.registerCheck(new WohnGeldCheck());
    this.registerCheck(new BuergergeldCheck());
    this.registerCheck(new BafoegCheck());
    this.registerCheck(new KindergeldCheck());
    this.registerCheck(new KinderzuschlagCheck());
    this.registerCheck(new ArbeitslosengeldCheck());
    this.registerCheck(new GrundsicherungCheck());
    this.registerCheck(new BildungUndTeilhabeCheck());
    this.registerCheck(new HilfeZumLebensunterhaltCheck());
    this.registerCheck(new BerufsausbildungsbeihilfeCheck());
  }

  registerCheck(check: EligibilityCheckInterface): void {
    this.checks.push(check);
  }

  addAndPrefillField(
    field: FormDataField,
    prefilledFields: PrefilledFields,
    prefillFormData?: FormData,
  ): PrefilledFields {
    if (!this.visibleFields.has(field)) {
      this.visibleFields.add(field);
      if (prefillFormData === undefined) {
        console.log("prefill undefined");

        return prefilledFields;
      }

      console.log("prefill")
      return {
        ...prefilledFields,
        [field]: prefillFormData[field],
      };
    }

    return {
      ...prefilledFields,
    };
  }

  refreshEligibilityForm(formData: FormData, prefillFormData?: FormData): PrefilledEligibilityEvaluationResult {
    const allResults = [];
    const allMissingFields = new Set<FormDataField>();

    let prefilledFields: PrefilledFields = {};

    for (const check of this.checks) {
      const result = check.evaluate(formData);

      if (result.missingFields) {
        result.missingFields.forEach((field) => {
          allMissingFields.add(field);
        });
      }

      allResults.push(result);
    }

    const eligible = allResults.filter((result) => result.eligible === true);
    const ineligible = allResults.filter((result) => result.eligible === false);

    if (this.visibleSections.length === 0) {
      const firstSection = this.nextSectionStrategy.getNextSection(
        this.visibleSections,
        formData,
        allResults
      );

      if (firstSection === null) {
        throw new Error("alles kaputt");
      }

      const newFields = this.sectionFields[firstSection].filter((field) =>
        allMissingFields.has(field)
      );

      this.visibleSections.push(firstSection);
      for (const field of newFields) {
        prefilledFields = this.addAndPrefillField(
          field,
          prefilledFields,
          prefillFormData
        );
      }

      return {
        eligible,
        ineligible,
        all: allResults,
        visibleSections: this.visibleSections,
        visibleFields: Array.from(this.visibleFields),
        prefilledFields
      };
    }

    for (const section of this.visibleSections) {
      const fieldsInSection = this.sectionFields[section].filter(
        (field) => allMissingFields.has(field) || this.visibleFields.has(field)
      );

      fieldsInSection.forEach((field) =>
        prefilledFields = this.addAndPrefillField(
          field,
          prefilledFields,
          prefillFormData
        )
      );
    }

    const allCurrentSectionsFilled = Array.from(this.visibleFields).every(
      (visibleField) => formData[visibleField] !== undefined
    );
    console.log("m", allMissingFields);
    console.log("v", this.visibleFields);
    const skippedSections: FormSection[] = [];

    while (
      this.visibleSections.length + skippedSections.length <
        Object.keys(this.sectionFields).length &&
      allCurrentSectionsFilled
    ) {
      const nextSection = this.nextSectionStrategy.getNextSection(
        [...this.visibleSections, ...skippedSections],
        formData,
        allResults
      );

      if (nextSection === null) {
        break;
      }

      const newFields = this.sectionFields[nextSection].filter((field) =>
        allMissingFields.has(field)
      );

      console.log("newFields", newFields)
      if (newFields.length === 0) {
        skippedSections.push(nextSection);
        continue;
      }

      this.visibleSections.push(nextSection);
      let newPrefilledFields = {};
      for (const field of newFields) {
        newPrefilledFields = this.addAndPrefillField(
          field,
          newPrefilledFields,
          prefillFormData
        );
      }
      if (Object.values(newPrefilledFields).filter(value => value !== undefined).length === newFields.length) {
        prefilledFields = {...prefilledFields, ...newPrefilledFields};
        continue;
      }

        return {
          eligible,
          ineligible,
          all: allResults,
          visibleSections: this.visibleSections,
          visibleFields: Array.from(this.visibleFields),
          prefilledFields,
        };
    }

    return {
      eligible,
      ineligible,
      all: allResults,
      visibleSections: this.visibleSections,
      visibleFields: Array.from(this.visibleFields),
      prefilledFields,
    };
  }
}
