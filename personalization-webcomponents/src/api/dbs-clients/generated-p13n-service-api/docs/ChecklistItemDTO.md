# ChecklistItemDTO

## Properties

| Name        | Type    |
| ----------- | ------- |
| `serviceID` | string  |
| `checked`   | Date    |
| `title`     | string  |
| `note`      | string  |
| `required`  | boolean |

## Example

```typescript
import type { ChecklistItemDTO } from "";

// TODO: Update the object below with actual values
const example = {
  serviceID: null,
  checked: null,
  title: null,
  note: null,
  required: null,
} satisfies ChecklistItemDTO;

console.log(example);

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example);
console.log(exampleJSON);

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ChecklistItemDTO;
console.log(exampleParsed);
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)
