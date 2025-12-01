# Eligibility Check System

This POC demonstrates a modular eligibility check system for determining which subsidies a person may be eligible for based on form data.

## Architecture

### Interface: `EligibilityCheckInterface`

Located in: `src/types/EligibilityCheckInterface.ts`

All eligibility checks must implement this interface:

```typescript
interface EligibilityCheckInterface {
  getName(): string;
  evaluate(formData: FormData): EligibilityResult;
}
```

### Components

1. **EligibilityCheckInterface** - Defines the contract for all eligibility checks
2. **FormData** - Structured input data (firstName, lastName)
3. **EligibilityResult** - Output containing eligibility status, reason, and URL
4. **EligibilityCheckRegistry** - Central registry managing all checks

## Adding a New Eligibility Check

### Step 1: Create a new check class

Create a new file in `src/eligibility/YourSubsidyCheck.ts`:

```typescript
import type {
  EligibilityCheckInterface,
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

export class YourSubsidyCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Your Subsidy Name";
  }

  evaluate(formData: FormData): EligibilityResult {
    // Your eligibility logic here
    const isEligible = /* your condition */;

    if (isEligible) {
      return {
        eligible: true,
        subsidyName: this.getName(),
        reason: "Explanation why they might be eligible",
        url: "https://link-to-subsidy-info.de",
      };
    }

    return {
      eligible: false,
      subsidyName: this.getName(),
    };
  }
}
```

### Step 2: Register the check

Add your check to `src/eligibility/EligibilityCheckRegistry.ts`:

```typescript
import { YourSubsidyCheck } from "./YourSubsidyCheck";

constructor() {
  this.registerCheck(new WohnGeldCheck());
  this.registerCheck(new YourSubsidyCheck()); // Add this line
}
```

## Current Implementations

### WohnGeldCheck (Active)
- **Trigger**: First name is "luis" (case-insensitive)
- **URL**: https://www.muenchen.de/rathaus/Stadtverwaltung/Sozialreferat/Wohnungsamt/Mietzuschuss.html

### KindergeldCheck (Example, Commented Out)
- **Trigger**: Last name is "mueller" (case-insensitive)
- **URL**: https://www.muenchen.de/rathaus/Stadtverwaltung/Sozialreferat/Jugendamt/Leistungen/Kindergeld.html

To enable KindergeldCheck, uncomment the import and registration in `EligibilityCheckRegistry.ts`.

## How It Works

1. User enters data in the form (first name, last name)
2. On save or load, `checkEligibility()` is called
3. All registered checks are evaluated with the form data
4. Only checks returning `eligible: true` are displayed
5. Each eligible result shows:
   - Subsidy name
   - Eligibility reason
   - "Mehr erfahren" button linking to more information

## Future Enhancements

- Add more form fields (age, income, family size, etc.)
- Implement complex eligibility rules
- Add API integration for real-time eligibility checks
- Support multi-step forms
- Add eligibility score/confidence level
- Support for partial eligibility with recommendations

