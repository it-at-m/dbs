# ChecklistsApi

All URIs are relative to _http://localhost:39146_

| Method                                                              | HTTP request                                          | Description                             |
| ------------------------------------------------------------------- | ----------------------------------------------------- | --------------------------------------- |
| [**checkChecklistEntry**](ChecklistsApi.md#checkchecklistentry)     | **POST** /checklist/{checklistID}/{serviceID}/check   | Check a Checklist-Entry                 |
| [**createChecklist**](ChecklistsApi.md#createchecklist)             | **POST** /checklist                                   | Create a new checklist                  |
| [**deleteChecklist**](ChecklistsApi.md#deletechecklist)             | **DELETE** /checklist/{checklistID}                   | Delete a checklist                      |
| [**getChecklist**](ChecklistsApi.md#getchecklist)                   | **GET** /checklist/{checklistID}                      | Get specific checklist by checklist-id. |
| [**getChecklists**](ChecklistsApi.md#getchecklists)                 | **GET** /checklist                                    | Get all checklists by user.             |
| [**uncheckChecklistEntry**](ChecklistsApi.md#uncheckchecklistentry) | **POST** /checklist/{checklistID}/{serviceID}/uncheck | Uncheck a Checklist-Entry               |
| [**updateChecklist**](ChecklistsApi.md#updatechecklist)             | **PUT** /checklist/{checklistID}                      | Update a checklist                      |

## checkChecklistEntry

> ChecklistServiceNavigatorReadDTO checkChecklistEntry(checklistID, serviceID)

Check a Checklist-Entry

Checks a checklist-entry.

### Example

```ts
import {
  Configuration,
  ChecklistsApi,
} from '';
import type { CheckChecklistEntryRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new ChecklistsApi();

  const body = {
    // string
    checklistID: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // string
    serviceID: serviceID_example,
  } satisfies CheckChecklistEntryRequest;

  try {
    const data = await api.checkChecklistEntry(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

| Name            | Type     | Description | Notes                     |
| --------------- | -------- | ----------- | ------------------------- |
| **checklistID** | `string` |             | [Defaults to `undefined`] |
| **serviceID**   | `string` |             | [Defaults to `undefined`] |

### Return type

[**ChecklistServiceNavigatorReadDTO**](ChecklistServiceNavigatorReadDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `*/*`

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **200**     | OK          | -                |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

## createChecklist

> ChecklistReadDTO createChecklist(checklistCreateDTO)

Create a new checklist

Creates a new checklist using the provided checklist details.

### Example

```ts
import {
  Configuration,
  ChecklistsApi,
} from '';
import type { CreateChecklistRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new ChecklistsApi();

  const body = {
    // ChecklistCreateDTO
    checklistCreateDTO: ...,
  } satisfies CreateChecklistRequest;

  try {
    const data = await api.createChecklist(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

| Name                   | Type                                        | Description | Notes |
| ---------------------- | ------------------------------------------- | ----------- | ----- |
| **checklistCreateDTO** | [ChecklistCreateDTO](ChecklistCreateDTO.md) |             |       |

### Return type

[**ChecklistReadDTO**](ChecklistReadDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `*/*`

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **201**     | Created     | -                |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

## deleteChecklist

> deleteChecklist(checklistID)

Delete a checklist

Deletes a checklist by checklistId.

### Example

```ts
import {
  Configuration,
  ChecklistsApi,
} from '';
import type { DeleteChecklistRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new ChecklistsApi();

  const body = {
    // string
    checklistID: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies DeleteChecklistRequest;

  try {
    const data = await api.deleteChecklist(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

| Name            | Type     | Description | Notes                     |
| --------------- | -------- | ----------- | ------------------------- |
| **checklistID** | `string` |             | [Defaults to `undefined`] |

### Return type

`void` (Empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **200**     | OK          | -                |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

## getChecklist

> ChecklistServiceNavigatorReadDTO getChecklist(checklistID)

Get specific checklist by checklist-id.

Returns a checklist by checklistId

### Example

```ts
import {
  Configuration,
  ChecklistsApi,
} from '';
import type { GetChecklistRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new ChecklistsApi();

  const body = {
    // string
    checklistID: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetChecklistRequest;

  try {
    const data = await api.getChecklist(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

| Name            | Type     | Description | Notes                     |
| --------------- | -------- | ----------- | ------------------------- |
| **checklistID** | `string` |             | [Defaults to `undefined`] |

### Return type

[**ChecklistServiceNavigatorReadDTO**](ChecklistServiceNavigatorReadDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `*/*`

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **200**     | OK          | -                |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

## getChecklists

> Array&lt;ChecklistReadDTO&gt; getChecklists()

Get all checklists by user.

Returns all checklists of an user (identified by JWT-Token)

### Example

```ts
import type { GetChecklistsRequest } from "";

import { ChecklistsApi, Configuration } from "";

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new ChecklistsApi();

  try {
    const data = await api.getChecklists();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**Array&lt;ChecklistReadDTO&gt;**](ChecklistReadDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `*/*`

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **200**     | OK          | -                |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

## uncheckChecklistEntry

> ChecklistServiceNavigatorReadDTO uncheckChecklistEntry(checklistID, serviceID)

Uncheck a Checklist-Entry

Unchecks a checklist-entry.

### Example

```ts
import {
  Configuration,
  ChecklistsApi,
} from '';
import type { UncheckChecklistEntryRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new ChecklistsApi();

  const body = {
    // string
    checklistID: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // string
    serviceID: serviceID_example,
  } satisfies UncheckChecklistEntryRequest;

  try {
    const data = await api.uncheckChecklistEntry(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

| Name            | Type     | Description | Notes                     |
| --------------- | -------- | ----------- | ------------------------- |
| **checklistID** | `string` |             | [Defaults to `undefined`] |
| **serviceID**   | `string` |             | [Defaults to `undefined`] |

### Return type

[**ChecklistServiceNavigatorReadDTO**](ChecklistServiceNavigatorReadDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `*/*`

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **200**     | OK          | -                |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

## updateChecklist

> ChecklistServiceNavigatorReadDTO updateChecklist(checklistID, checklistUpdateDTO)

Update a checklist

Updates a checklist using the provided checklist details.

### Example

```ts
import {
  Configuration,
  ChecklistsApi,
} from '';
import type { UpdateChecklistRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new ChecklistsApi();

  const body = {
    // string
    checklistID: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // ChecklistUpdateDTO
    checklistUpdateDTO: ...,
  } satisfies UpdateChecklistRequest;

  try {
    const data = await api.updateChecklist(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

| Name                   | Type                                        | Description | Notes                     |
| ---------------------- | ------------------------------------------- | ----------- | ------------------------- |
| **checklistID**        | `string`                                    |             | [Defaults to `undefined`] |
| **checklistUpdateDTO** | [ChecklistUpdateDTO](ChecklistUpdateDTO.md) |             |                           |

### Return type

[**ChecklistServiceNavigatorReadDTO**](ChecklistServiceNavigatorReadDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `*/*`

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **200**     | OK          | -                |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)
