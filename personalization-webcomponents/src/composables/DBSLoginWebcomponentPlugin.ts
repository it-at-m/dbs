import {onMounted, ref} from 'vue';
import AuthorizationEventDetails from "@/types/AuthorizationEventDetails";

/**
 * Event from the Login-Webcomponent which notifies when the
 * oauth2-token changes.
 */
const AUTH_REFRESH_EVENT_NAME = "authorization-event";

/**
 * Plugin to use for webcomponents which rely on the `dbs-login`-Webcomponent
 * This Plugin registers necessary event-handlers to get notified of authentication-changes
 * and checks predefined cookies of the active domain for access tokens to use.
 *
 * It exposes two properties to be used by the parent component:
 *
 * @param loginCallback Callback-Method which is called after the user is successfully signed in.
 * @param logoutCallback Callback-Method which is called after the user is logged out.
 *
 * @return loading By default true, until authentication fails. Should be set to false after successfull loading of data.
 * @return loggedIn default false, turns true after a successfull authentification.
 */
export function useDBSLoginWebcomponentPlugin(
  loginCallback: (accessToken: AuthorizationEventDetails) => void = () => {},
  logoutCallback: () => void = () => {},
) {
  const authLoading = ref(true)
  const loggedIn = ref(false)
  let prevAuthDetails : AuthorizationEventDetails | undefined = undefined;

  onMounted(() => {
    document.addEventListener(AUTH_REFRESH_EVENT_NAME, (ev: any) => {
      authLoading.value = true;
      authChanged(ev.detail);
    });
  })

  function authChanged(newAuthDetails: AuthorizationEventDetails | undefined) {
    authLoading.value = false;
    if (newAuthDetails) {
      loggedIn.value = true;
      if(JSON.stringify(newAuthDetails) !== JSON.stringify(prevAuthDetails)) {
        loginCallback(newAuthDetails);
        prevAuthDetails = newAuthDetails;
      } else {
        // details unchanged, do nothing.
      }
    } else {
      loggedIn.value = false;
      logoutCallback();
    }
  }

  // expose managed state as return value
  return {authLoading, loggedIn}
}
