import type { EligibilityCheckResult, EligibilityResult, FormData, FormDataField } from "@/types/EligibilityCheckInterface";





// type WithRequired<T, K extends readonly (keyof T)[]> = T & {
//   [P in K[number]]-?: NonNullable<T[P]>;
// };

type CheckedOnly<T, K extends readonly (keyof T)[]> = {
  [P in K[number]]: NonNullable<T[P]>;
};

export class RuleBuilder<TChecked extends readonly FormDataField[] = []> {
  private missingFields: Set<FormDataField> = new Set<FormDataField>();
  private checkedFields: Set<FormDataField> = new Set<FormDataField>();
  private result?: EligibilityCheckResult;

  constructor(
    private readonly formData: FormData,
    private readonly subsidyName: string
  ) { }

  private hasFailed(): boolean {
    return this.result?.eligible === false;
  }

  failIfField<K extends FormDataField>(
    field: K,
    condition: (data: CheckedOnly<FormData, [...TChecked, K]>) => boolean,
    message: string
  ): RuleBuilder<[...TChecked, K]> {
    if (this.hasFailed()) {
      return this as RuleBuilder<[...TChecked, K]>;
    }

    this.checkedFields.add(field);
    const value = this.formData[field];

    if (value === undefined) {
      this.missingFields.add(field);
    } else {
      if (condition(this.formData as CheckedOnly<FormData, [...TChecked, K]>)) {
        this.result = {
          eligible: false,
          reason: message,
        };
      }
    }

    return this as RuleBuilder<[...TChecked, K]>;
  }

  failIfFields<K extends readonly FormDataField[]>(
    fields: K,
    condition: (data: CheckedOnly<FormData, [...TChecked, ...K]>) => boolean,
    message: string
  ): RuleBuilder<[...TChecked, ...K]> {
    if (this.hasFailed()) {
      return this as RuleBuilder<[...TChecked, ...K]>;
    }

    fields.forEach(f => this.checkedFields.add(f));

    let missing = false;

    for (const field of fields) {
      if (this.formData[field] === undefined) {
        this.missingFields.add(field);
        missing = true;
      }
    }

    if (!missing) {
      if (
        condition(this.formData as CheckedOnly<FormData, [...TChecked, ...K]>)
      ) {
        this.result = {
          eligible: false,
          reason: message,
        };
      }
    }

    return this as RuleBuilder<[...TChecked, ...K]>;
  }

  orElseSuccess(
    success: (
      data: CheckedOnly<FormData, TChecked>
    ) => Omit<EligibilityCheckResult, "type" | "eligible">
  ): EligibilityResult {
    if (this.hasFailed() && this.result) {
      return {
        eligible: false,
        subsidyName: this.subsidyName,
        reason: this.result.reason,
        url: this.result.url,
        checkedFields: this.checkedFields,
      };
    }

    if (this.missingFields.size > 0) {
      return {
        subsidyName: this.subsidyName,
        reason: "Bitte geben Sie alle erforderlichen Informationen an.",
        missingFields: this.missingFields,
        checkedFields: this.checkedFields,
      };
    }

    const { reason, url } = success(
      this.formData as CheckedOnly<FormData, TChecked>
    );

    return {
      eligible: true,
      subsidyName: this.subsidyName,
      reason,
      url,
      checkedFields: this.checkedFields,
    };
  }
}
