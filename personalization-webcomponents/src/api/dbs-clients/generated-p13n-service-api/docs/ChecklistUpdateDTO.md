# ChecklistUpdateDTO

## Properties

| Name             | Type                                                 |
| ---------------- | ---------------------------------------------------- |
| `id`             | string                                               |
| `lhmExtId`       | string                                               |
| `title`          | string                                               |
| `checklistItems` | [Array&lt;ChecklistItemDTO&gt;](ChecklistItemDTO.md) |

## Example

```typescript
import type { ChecklistUpdateDTO } from "";

// TODO: Update the object below with actual values
const example = {
  id: null,
  lhmExtId: null,
  title: null,
  checklistItems: null,
} satisfies ChecklistUpdateDTO;

console.log(example);

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example);
console.log(exampleJSON);

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ChecklistUpdateDTO;
console.log(exampleParsed);
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)
