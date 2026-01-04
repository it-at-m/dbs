export interface FormData {
  // Personal Information
  firstName?: string;
  lastName?: string;
  dateOfBirth?: string; // ISO date string (YYYY-MM-DD)
  gender?: 'male' | 'female' | 'diverse' | 'unspecified';
  maritalStatus?: 'single' | 'married' | 'divorced' | 'widowed' | 'separated';
  nationality?: 'German' | 'EU' | 'Non-EU';
  residenceStatus?: 'residence_permit' | 'permanent_residence' | 'none'; // For non-German citizens
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
  livesWithParents?: boolean; // Lives with parents

  // Education & Employment
  employmentStatus?: 'employed' | 'self_employed' | 'unemployed' | 'student' | 'retired' | 'other';
  educationLevel?: 'no_degree' | 'lower_secondary' | 'secondary' | 'high_school' | 'vocational_training' | 'university';
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
  healthInsurance?: 'public' | 'private' | 'none';
  hasCareInsurance?: boolean;
  receivesChildBenefit?: boolean;
  receivesHousingBenefit?: boolean;
  receivesStudentAid?: boolean;
  
  // Additional for BAföG
  hasFinancialHardship?: boolean; // Financial difficulty
  workAbility?: 'full' | 'limited' | 'none'; // Work ability
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
