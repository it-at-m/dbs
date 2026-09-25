import { getAPIBaseURL } from "@/utils/Constants";
import { getXSRFToken } from "@/utils/Cookie";

const PATH_GET_PASSWORD = "/password/";
const PATH_RESET_PASSWORD = "/password/reset";

export function getPassword(resetKey: string): Promise<Response> {
    return fetch(
        `${getAPIBaseURL()}${PATH_GET_PASSWORD}${encodeURIComponent(resetKey)}`
    );
}

export function resetPassword(resetKey: string): Promise<Response> {
    const query = new URLSearchParams({ resetKey });
    return fetch(`${getAPIBaseURL()}${PATH_RESET_PASSWORD}?${query}`, {
        method: "POST",
        credentials: "include",
        headers: { "x-xsrf-token": getXSRFToken() },
    });
}
