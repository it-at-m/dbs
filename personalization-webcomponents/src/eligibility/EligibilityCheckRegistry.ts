import type { EligibilityCheckInterface } from "@/types/EligibilityCheckInterface";
import { WohnGeldCheck } from "./WohnGeldCheck";
// import { KindergeldCheck } from "./KindergeldCheck";

export class EligibilityCheckRegistry {
  private checks: EligibilityCheckInterface[] = [];

  constructor() {
    // Register all eligibility checks here
    this.registerCheck(new WohnGeldCheck());
    
    // Uncomment to enable additional checks:
    // this.registerCheck(new KindergeldCheck());
    // this.registerCheck(new BildungUndTeilhabeCheck());
  }

  registerCheck(check: EligibilityCheckInterface): void {
    this.checks.push(check);
  }

  getAllChecks(): EligibilityCheckInterface[] {
    return this.checks;
  }
}

