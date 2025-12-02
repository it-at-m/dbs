export interface FormData {
  // Personal Information
  firstName: string;
  lastName: string;
  geburtsdatum?: string; // ISO date string (YYYY-MM-DD)
  alter?: number; // Age (can be derived)
  geschlecht?: 'männlich' | 'weiblich' | 'divers' | 'keine Angabe';
  familienstand?: 'ledig' | 'verheiratet' | 'geschieden' | 'verwitwet' | 'getrennt';
  staatsangehoerigkeit?: 'Deutsch' | 'EU' | 'Nicht-EU';
  aufenthaltsstatus?: 'Aufenthaltserlaubnis' | 'Niederlassungserlaubnis' | 'Keine'; // For non-German citizens
  wohnsitzInDeutschland?: boolean; // Habitual residence in Germany
  
  // Financial Information
  bruttoEinkommenMonatlich?: number;
  nettoEinkommenMonatlich?: number;
  vermoegen?: number;
  mieteMietzinsMonatlich?: number;
  
  // Household Information
  anzahlPersonenHaushalt?: number;
  anzahlKinder?: number;
  kinderAlter?: number[]; // Array of children ages
  
  // Education & Employment
  beschaeftigungsstatus?: 'angestellt' | 'selbststaendig' | 'arbeitslos' | 'student' | 'rentner' | 'sonstiges';
  bildungsstand?: 'kein_abschluss' | 'hauptschule' | 'realschule' | 'abitur' | 'ausbildung' | 'studium';
  istStudent?: boolean;
  
  // Special Circumstances
  hatBehinderung?: boolean;
  gradDerBehinderung?: number; // 0-100
  beziehtAlg1?: boolean;
  beziehtAlg2?: boolean;
  beziehtRente?: boolean;
  rentenberechtigt?: boolean; // Eligible for pension (reached pension age)
  istSchwanger?: boolean;
  istAlleinerziehend?: boolean;
  hatPflegebeduerftigkeit?: boolean;
  buergergeldLetzte3Jahre?: boolean; // Received Bürgergeld in last 3 years
  
  // Insurance & Benefits
  krankenversicherung?: 'gesetzlich' | 'privat' | 'keine';
  hatPflegeversicherung?: boolean;
  beziehtKindergeld?: boolean;
  beziehtWohngeld?: boolean;
  beziehtBafög?: boolean;
  
  // Additional for BAföG
  hatFinanzielleNotlage?: boolean; // Financial difficulty
  arbeitsfahigkeit?: 'voll' | 'eingeschraenkt' | 'keine'; // Work ability
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

