
# ChecklistServiceNavigatorReadDTO


## Properties

Name | Type
------------ | -------------
`id` | string
`lhmExtId` | string
`title` | string
`lastUpdate` | Date
`situationId` | string
`checklistItemServiceNavigatorDtos` | [Array&lt;ChecklistItemServiceNavigatorDTO&gt;](ChecklistItemServiceNavigatorDTO.md)

## Example

```typescript
import type { ChecklistServiceNavigatorReadDTO } from ''

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "lhmExtId": null,
  "title": null,
  "lastUpdate": null,
  "situationId": null,
  "checklistItemServiceNavigatorDtos": null,
} satisfies ChecklistServiceNavigatorReadDTO

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ChecklistServiceNavigatorReadDTO
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


