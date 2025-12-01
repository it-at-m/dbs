import type {
  EligibilityCheckInterface,
  EligibilityResult,
  FormData,
} from "@/types/EligibilityCheckInterface";

export class KindergeldCheck implements EligibilityCheckInterface {
  getName(): string {
    return "Kindergeld";
  }

  evaluate(formData: FormData): EligibilityResult {
    // Simplified: Check if they have children
    const hasChildren = formData.anzahlKinder && formData.anzahlKinder > 0;

    if (hasChildren) {
      const childCount = formData.anzahlKinder || 0;
      return {
        eligible: true,
        subsidyName: this.getName(),
        reason:
          `Sie haben ${childCount} ${childCount === 1 ? 'Kind' : 'Kinder'} angegeben. Sie könnten für Kindergeld berechtigt sein.`,
        url: "https://www.arbeitsagentur.de/familie-und-kinder/kindergeld",
      };
    }

    return {
      eligible: false,
      subsidyName: this.getName(),
    };
  }
}

