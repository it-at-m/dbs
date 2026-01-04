import type { FormSection } from "@/eligibility/EligibilityCheckRegistry.ts";
import type { EligibilityResult, FormData } from "@/types/EligibilityCheckInterface.ts";

export interface NextSectionStrategy {
  getNextSection(
    consideredSections: FormSection[],
    formData: FormData,
    results: EligibilityResult[]
  ): FormSection|null;
}

export class OrderedNextSectionStrategy implements NextSectionStrategy {
  order: FormSection[] = [
    "personalInfo",
    "financialInfo",
    "householdInfo",
    "educationEmployment",
    "specialCircumstances",
    "insuranceBenefits",
  ];

  getNextSection(
    consideredSections: FormSection[],
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    formData: FormData,
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    eligibilityResults: EligibilityResult[]
  ): FormSection|null {
    for (const section of this.order){
      if(!consideredSections.includes(section)){
        return section;
      }
    }
    return null;
  }
}