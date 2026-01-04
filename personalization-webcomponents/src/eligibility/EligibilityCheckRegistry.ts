import type { EligibilityCheckInterface, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";
import { OrderedNextSectionStrategy } from "@/eligibility/NextSectionStrategy.ts";
import { BuergergeldCheck } from "@/eligibility/BuergergeldCheck.ts";
import { BafoegCheck } from "@/eligibility/BafoegCheck.ts";
import { KindergeldCheck } from "@/eligibility/KindergeldCheck.ts";
import { WohnGeldCheck } from "@/eligibility/WohnGeldCheck.ts";

export interface EligibilityEvaluationResult {
  eligible: EligibilityResult[];
  ineligible: EligibilityResult[];
  all: EligibilityResult[];
  visibleFields: FormDataField[];
  visibleSections: FormSection[];
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
  }

  registerCheck(check: EligibilityCheckInterface): void {
    this.checks.push(check);
  }

  getVisibleSections(formData: FormData): EligibilityEvaluationResult {
    const allResults = [];
    const allMissingFields = new Set<FormDataField>();

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

      if(firstSection === null){
        throw new Error("alles kaputt");
      }

      const newFields = this.sectionFields[firstSection].filter((field) =>
        allMissingFields.has(field)
      );

      this.visibleSections.push(firstSection);
      for (const field of newFields) {
        this.visibleFields.add(field);
      }

      return {
        eligible,
        ineligible,
        all: allResults,
        visibleSections: this.visibleSections,
        visibleFields: Array.from(this.visibleFields),
      };
    }

    for (const section of this.visibleSections) {
      const fieldsInSection = this.sectionFields[section].filter((field) =>
        allMissingFields.has(field) || this.visibleFields.has(field)
      );

      fieldsInSection.forEach(field => this.visibleFields.add(field));
    }

    const allCurrentSectionsFilled = Array.from(this.visibleFields).every(
      (visibleField) => !allMissingFields.has(visibleField)
    );
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

      if(nextSection === null){
        break;
      }

      const newFields = this.sectionFields[nextSection].filter((field) =>
        allMissingFields.has(field)
      );

      if(newFields.length === 0){
        skippedSections.push(nextSection);
        continue;
      }

      this.visibleSections.push(nextSection);
      for (const field of newFields) {
        this.visibleFields.add(field);
      }

      return {
        eligible,
        ineligible,
        all: allResults,
        visibleSections: this.visibleSections,
        visibleFields: Array.from(this.visibleFields),
      };
    }

    return {
      eligible,
      ineligible,
      all: allResults,
      visibleSections: this.visibleSections,
      visibleFields: Array.from(this.visibleFields),
    };
  }
}
