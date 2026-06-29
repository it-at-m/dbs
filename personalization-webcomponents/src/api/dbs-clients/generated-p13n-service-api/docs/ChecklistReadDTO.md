# ChecklistReadDTO

## Properties

| Name             | Type                                                 |
| ---------------- | ---------------------------------------------------- |
| `id`             | string                                               |
| `lhmExtId`       | string                                               |
| `title`          | string                                               |
| `situationId`    | string                                               |
| `lastUpdate`     | Date                                                 |
| `checklistItems` | [Array&lt;ChecklistItemDTO&gt;](ChecklistItemDTO.md) |

## Example

```typescript
import type { ChecklistReadDTO } from "";

// TODO: Update the object below with actual values
const example = {
  id: null,
  lhmExtId: null,
  title: null,
  situationId: null,
  lastUpdate: null,
  checklistItems: null,
} satisfies ChecklistReadDTO;

console.log(example);

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example);
console.log(exampleJSON);

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ChecklistReadDTO;
console.log(exampleParsed);
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)
