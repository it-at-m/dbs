import type {FetchParams, Middleware,} from "@/api/dbs-clients/generated-p13n-service-api";
import {
  ChecklistsApi,
  Configuration,
  PublicServiceNavigatorEndpointsApi,
} from "@/api/dbs-clients/generated-p13n-service-api";
import {getAccessToken, getAPIBaseURL, getXSRFToken,} from "@/util/Constants.ts";

const PATHNAME_P13N_BACKEND_CLIENT_ROUTES = "/clients/api/p13n-backend";
const PATHNAME_P13N_BACKEND_PUBLIC_ROUTES = "/public/api/p13n-backend";

export function useChecklistsApi() {
  return new ChecklistsApi(
    new Configuration({
      basePath: getAPIBaseURL() + PATHNAME_P13N_BACKEND_CLIENT_ROUTES,
      headers: {
        Authorization: "Bearer " + getAccessToken(),
        "x-xsrf-token": getXSRFToken(),
      },
      credentials: "include",
    })
  );
}

/**
 * As our backend has all public routes (as in routes that don't require authentication) prefixed behind the "/public" path
 * but our api-gw automatically adds this "public" prefix to all requests on this route
 * we have to strip the "dupliceate" public-part from the pathname when accessing these public routes.
 */
const removePublicPrefixMiddleware: Middleware = {
  pre: async (context: FetchParams): Promise<FetchParams> => {
    const url = new URL(context.url);

    if (
      url.pathname.startsWith(PATHNAME_P13N_BACKEND_PUBLIC_ROUTES + "/public")
    ) {
      url.pathname = url.pathname.replace(
        PATHNAME_P13N_BACKEND_PUBLIC_ROUTES + "/public",
        PATHNAME_P13N_BACKEND_PUBLIC_ROUTES
      );
    }

    return {
      ...context,
      url: url.toString(),
    };
  },
};

export function usePublicServiceNavigatorEndpoints() {
  return new PublicServiceNavigatorEndpointsApi(
    new Configuration({
      basePath: getAPIBaseURL() + PATHNAME_P13N_BACKEND_PUBLIC_ROUTES,
      middleware: [removePublicPrefixMiddleware],
    })
  );
}
