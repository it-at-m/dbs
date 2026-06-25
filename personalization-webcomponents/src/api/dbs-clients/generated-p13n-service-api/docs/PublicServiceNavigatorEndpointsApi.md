# PublicServiceNavigatorEndpointsApi

All URIs are relative to *http://localhost:39146*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getServicesByIds**](PublicServiceNavigatorEndpointsApi.md#getservicesbyids) | **GET** /public/servicenavigator | Lookup ServiceNavigator Services by ServiceID. Returns a list of services for the given service IDs. |



## getServicesByIds

> Array&lt;ChecklistItemServiceNavigatorDTO&gt; getServicesByIds(ids, lang)

Lookup ServiceNavigator Services by ServiceID. Returns a list of services for the given service IDs.

### Example

```ts
import {
  Configuration,
  PublicServiceNavigatorEndpointsApi,
} from '';
import type { GetServicesByIdsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new PublicServiceNavigatorEndpointsApi();

  const body = {
    // string
    ids: ids_example,
    // string | Language for translated service content. Falls back to \'de\' if not provided. (optional)
    lang: de,
  } satisfies GetServicesByIdsRequest;

  try {
    const data = await api.getServicesByIds(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **ids** | `string` |  | [Defaults to `undefined`] |
| **lang** | `string` | Language for translated service content. Falls back to \&#39;de\&#39; if not provided. | [Optional] [Defaults to `undefined`] |

### Return type

[**Array&lt;ChecklistItemServiceNavigatorDTO&gt;**](ChecklistItemServiceNavigatorDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `*/*`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

