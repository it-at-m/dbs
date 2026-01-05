import type {
  EligibilityCheckInterface,
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

import { RuleBuilder } from "./RuleBuilder";

export abstract class AbstractEligibilityCheck
  implements EligibilityCheckInterface
{
  public abstract evaluate(formData: FormData): EligibilityResult;

  protected rules(formData: FormData, subsidyName: string): RuleBuilder {
    return new RuleBuilder(formData, subsidyName);
  }
}
