export interface FormData {
  // Personal Information (existing + new)
  firstName: string;
  lastName: string;
  geburtsdatum?: string; // ISO date string (YYYY-MM-DD)
  
  // Location
  stadt?: string; // City name (e.g., "München")
  
  // Financial Information
  nettoEinkommenMonatlich?: number; // Monthly net income in EUR
  mieteMietzinsMonatlich?: number; // Monthly rent in EUR
  
  // Household Information
  anzahlPersonenHaushalt?: number; // Number of people in household
  anzahlKinder?: number; // Number of children (conditional field)
  
  // Status flags
  istStudent?: boolean;
  beziehtAlg2?: boolean; // Receiving unemployment benefits
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

