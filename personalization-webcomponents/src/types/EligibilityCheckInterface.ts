export interface FormData {
  // Personal Information
  firstName?: string;
  lastName?: string;
  dateOfBirth?: string; // ISO date string (YYYY-MM-DD)
  age?: number; // Age (can be derived)
  gender?: 'männlich' | 'weiblich' | 'divers' | 'keine Angabe';
  maritalStatus?: 'ledig' | 'verheiratet' | 'geschieden' | 'verwitwet' | 'getrennt';
  nationality?: 'Deutsch' | 'EU' | 'Nicht-EU';
  residenceStatus?: 'Aufenthaltserlaubnis' | 'Niederlassungserlaubnis' | 'Keine'; // For non-German citizens
  residenceInGermany?: boolean; // Habitual residence in Germany
  
  // Financial Information
  grossMonthlyIncome?: number;
  netMonthlyIncome?: number;
  assets?: number;
  monthlyRent?: number;
  
  // Household Information
  householdSize?: number;
  numberOfChildren?: number;
  childrenAges?: number[]; // Array of children ages
  
  // Education & Employment
  employmentStatus?: 'angestellt' | 'selbststaendig' | 'arbeitslos' | 'student' | 'rentner' | 'sonstiges';
  educationLevel?: 'kein_abschluss' | 'hauptschule' | 'realschule' | 'abitur' | 'ausbildung' | 'studium';
  isStudent?: boolean;
  
  // Special Circumstances
  hasDisability?: boolean;
  disabilityDegree?: number; // 0-100
  receivesUnemploymentBenefit1?: boolean;
  receivesUnemploymentBenefit2?: boolean;
  receivesPension?: boolean;
  pensionEligible?: boolean; // Eligible for pension (reached pension age)
  isPregnant?: boolean;
  isSingleParent?: boolean;
  hasCareNeeds?: boolean;
  citizenBenefitLast3Years?: boolean; // Received Bürgergeld in last 3 years
  
  // Insurance & Benefits
  healthInsurance?: 'gesetzlich' | 'privat' | 'keine';
  hasCareInsurance?: boolean;
  receivesChildBenefit?: boolean;
  receivesHousingBenefit?: boolean;
  receivesStudentAid?: boolean;
  
  // Additional for BAföG
  hasFinancialHardship?: boolean; // Financial difficulty
  workAbility?: 'voll' | 'eingeschraenkt' | 'keine'; // Work ability
}

export type FormDataField = keyof FormData;

export interface EligibilityResult {
  eligible?: boolean;
  missingFields?: Set<FormDataField>;
  subsidyName: string;
  reason?: string;
  url?: string;
}

export interface EligibilityCheckInterface {
  getName(): string;
  evaluate(formData: FormData): EligibilityResult;
}

