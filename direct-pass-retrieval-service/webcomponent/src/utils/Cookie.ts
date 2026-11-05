class Cookie {
    key: string;
    value: string;

    constructor(key: string, value: string) {
        this.key = key;
        this.value = value;
    }
}

/**
 * Get Cookie by its name . Undefined if cookie is not present.
 * @param cookieName Name of the Cookie
 */
export function getCookie(cookieName: string): Cookie | undefined {
    let cookie: Cookie | undefined;

    document.cookie.split(";").forEach((element) => {
        const [key, value] = element.split("=");
        if (key.trim() === cookieName) {
            cookie = new Cookie(key.trim(), value);
        }
    });

    return cookie;
}

export function getXSRFToken(): string {
    const xsrfToken = getCookie("XSRF-TOKEN");
    if (xsrfToken === undefined) {
        return "";
    }
    return xsrfToken.value;
}
