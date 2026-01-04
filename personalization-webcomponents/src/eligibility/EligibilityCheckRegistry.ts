import type { 
  EligibilityCheckInterface, 
  EligibilityResult, 
  FormData,
  FormDataField 
} from "@/types/EligibilityCheckInterface";
import { BaföGCheck } from "./BaföGCheck";
import { WohnGeldCheck } from "./WohnGeldCheck";
import { BürgergeldCheck } from "./BürgergeldCheck";
import { KindergeldCheck } from "./KindergeldCheck";

export interface EligibilityEvaluationResult {
  eligible: EligibilityResult[];
  ineligible: EligibilityResult[];
  all: EligibilityResult[];
  missingFields: FormDataField[];
}

export class EligibilityCheckRegistry {
  private checks: EligibilityCheckInterface[] = [];
  private displayedFields: Set<FormDataField> = new Set<FormDataField>();
  private permanentlyMissingFields: Set<FormDataField> = new Set<FormDataField>();
  
  constructor() {
    // Register all eligibility checks here
    this.registerCheck(new WohnGeldCheck());
    this.registerCheck(new BaföGCheck());
    this.registerCheck(new BürgergeldCheck());
    this.registerCheck(new KindergeldCheck());
  }

  registerCheck(check: EligibilityCheckInterface): void {
    this.checks.push(check);
  }

  evaluateAll(formData: FormData): EligibilityEvaluationResult {
    const allResults: EligibilityResult[] = [];
    const missingFieldsSet = new Set<FormDataField>();

    for (const check of this.checks) {
      const result = check.evaluate(formData);
      allResults.push(result);

      if (result.missingFields) {
        result.missingFields.forEach(field => {
          missingFieldsSet.add(field);
          // Once a field is missing, mark it as permanently missing
          this.permanentlyMissingFields.add(field);
        });
      }
    }

    // Merge current missing fields with permanently missing fields
    this.permanentlyMissingFields.forEach(field => missingFieldsSet.add(field));

    // Separate eligible and ineligible results
    const eligible = allResults.filter(result => result.eligible === true);
    const ineligible = allResults.filter(result => result.eligible === false);

    return {
      eligible,
      ineligible,
      all: allResults,
      missingFields: Array.from(missingFieldsSet),
    };
  }

  /**
   * Reset the permanently missing fields tracking.
   * Call this when you want to start fresh (e.g., user clears the form).
   */
  resetMissingFieldsTracking(): void {
    this.permanentlyMissingFields.clear();
  }

  /**
   * Get all fields that have been marked as missing at any point.
   */
  getPermanentlyMissingFields(): FormDataField[] {
    return Array.from(this.permanentlyMissingFields);
  }
}
