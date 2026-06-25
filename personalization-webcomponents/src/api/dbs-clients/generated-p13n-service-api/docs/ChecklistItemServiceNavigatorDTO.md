
# ChecklistItemServiceNavigatorDTO


## Properties

Name | Type
------------ | -------------
`serviceID` | string
`checked` | Date
`title` | string
`publicUrl` | string
`note` | string
`isExternal` | boolean
`required` | boolean
`appointmentServiceUrl` | string
`appointmentService` | boolean
`onlineServices` | [Array&lt;OnlineServiceDTO&gt;](OnlineServiceDTO.md)

## Example

```typescript
import type { ChecklistItemServiceNavigatorDTO } from ''

// TODO: Update the object below with actual values
const example = {
  "serviceID": null,
  "checked": null,
  "title": null,
  "publicUrl": null,
  "note": null,
  "isExternal": null,
  "required": null,
  "appointmentServiceUrl": null,
  "appointmentService": null,
  "onlineServices": null,
} satisfies ChecklistItemServiceNavigatorDTO

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ChecklistItemServiceNavigatorDTO
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


