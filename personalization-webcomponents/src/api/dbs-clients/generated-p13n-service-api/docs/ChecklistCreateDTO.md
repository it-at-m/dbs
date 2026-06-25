# ChecklistCreateDTO

## Properties

| Name             | Type                                                 |
| ---------------- | ---------------------------------------------------- |
| `title`          | string                                               |
| `situationId`    | string                                               |
| `checklistItems` | [Array&lt;ChecklistItemDTO&gt;](ChecklistItemDTO.md) |

## Example

```typescript
import type { ChecklistCreateDTO } from "";

// TODO: Update the object below with actual values
const example = {
  title: null,
  situationId: null,
  checklistItems: null,
} satisfies ChecklistCreateDTO;

console.log(example);

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example);
console.log(exampleJSON);

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ChecklistCreateDTO;
console.log(exampleParsed);
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)
