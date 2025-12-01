export interface FormData {
  firstName: string;
  lastName: string;
}

export interface EligibilityResult {
  eligible: boolean;
  subsidyName: string;
  reason?: string;
  url?: string;
}

export interface EligibilityCheckInterface {
  getName(): string;
  evaluate(formData: FormData): EligibilityResult;
}

