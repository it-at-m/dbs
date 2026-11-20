/**
 * Query-Parameter which contains the ticket-number
 */
export const QUERY_PARAM_Q_STRING = "q";

export function getAPIBaseURL(): string {
    return (
        import.meta.env.VITE_VUE_APP_API_URL ?? new URL(import.meta.url).origin
    );
}
