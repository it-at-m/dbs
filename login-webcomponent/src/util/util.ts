export function parseTime(exp: number, long: boolean) {
  const current = new Date(exp * 1000);
  return long
    ? current.toLocaleTimeString("en-US", {
        hour12: false,
        hour: "numeric",
        minute: "numeric",
        second: "numeric",
      })
    : current.toLocaleTimeString("en-US", {
        minute: "numeric",
        second: "numeric",
      });
}

export function getQueryParam(param: string) {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  return urlParams.get(param);
}
