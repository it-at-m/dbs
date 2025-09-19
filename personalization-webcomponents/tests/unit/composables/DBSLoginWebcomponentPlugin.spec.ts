/**
 * DBSLoginWebcomponentPlugin composable tests
 * Testing library/framework: Vitest (describe/it/expect/vi) with jsdom environment.
 * Vue test runner utilities: @vue/test-utils (mount).
 *
 * Focus: useDBSLoginWebcomponentPlugin listens to document "authorization-event"
 * and exposes { authLoading, loggedIn }.
 * - On truthy detail (AuthorizationEventDetails): authLoading=false, loggedIn=true, loginCallback (only when details changed)
 * - On falsy/undefined detail: authLoading=false, loggedIn=false, logoutCallback
 */

import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { defineComponent, h, nextTick } from "vue";
import { mount } from "@vue/test-utils";

import AuthorizationEventDetails from "@/types/AuthorizationEventDetails";
import { useDBSLoginWebcomponentPlugin } from "@/composables/DBSLoginWebcomponentPlugin";

const AUTH_EVENT = "authorization-event";

function dispatchAuth(detail?: AuthorizationEventDetails | undefined) {
  document.dispatchEvent(new CustomEvent(AUTH_EVENT, { detail, bubbles: true }));
}

describe("useDBSLoginWebcomponentPlugin", () => {
  let consoleErrorSpy: ReturnType<typeof vi.spyOn>;

  // Harness mounts a tiny component to trigger onMounted so the composable registers its listener
  const mountHarness = (
    loginCb?: (d: AuthorizationEventDetails) => void,
    logoutCb?: () => void
  ) => {
    let exposed: { authLoading?: any; loggedIn?: any } = {};
    const Comp = defineComponent({
      name: "Harness",
      setup() {
        const { authLoading, loggedIn } = useDBSLoginWebcomponentPlugin(
          loginCb ?? (() => {}),
          logoutCb ?? (() => {})
        );
        Object.assign(exposed, { authLoading, loggedIn });
        return () => h("div");
      },
    });
    const wrapper = mount(Comp);
    return {
      wrapper,
      exposed: exposed as {
        authLoading: { value: boolean };
        loggedIn: { value: boolean };
      },
    };
  };

  beforeEach(() => {
    document.body.replaceChildren();
    consoleErrorSpy = vi.spyOn(console, "error").mockImplementation(() => {});
  });

  afterEach(() => {
    consoleErrorSpy.mockRestore();
  });

  it("initial state: authLoading=true, loggedIn=false", () => {
    const { exposed } = mountHarness();
    expect(exposed.authLoading.value).toBe(true);
    expect(exposed.loggedIn.value).toBe(false);
  });

  it("on successful authorization-event: sets loggedIn=true, authLoading=false and invokes loginCallback once", async () => {
    const loginCb = vi.fn();
    const { exposed } = mountHarness(loginCb);

    const details = new AuthorizationEventDetails(
      "Max Mustermann",
      "max@example.com",
      "keycloak",
      "level3",
      "token-123"
    );

    dispatchAuth(details);
    await nextTick();

    expect(loginCb).toHaveBeenCalledTimes(1);
    expect(loginCb).toHaveBeenCalledWith(details);
    expect(exposed.authLoading.value).toBe(false);
    expect(exposed.loggedIn.value).toBe(true);
  });

  it("emitting the same details twice does not re-invoke loginCallback (change detection)", async () => {
    const loginCb = vi.fn();
    const { exposed } = mountHarness(loginCb);

    const details = new AuthorizationEventDetails(
      "Max Mustermann",
      "max@example.com",
      "keycloak",
      "level3",
      "token-123"
    );

    dispatchAuth(details);
    await nextTick();
    dispatchAuth(details);
    await nextTick();

    expect(loginCb).toHaveBeenCalledTimes(1);
    expect(exposed.loggedIn.value).toBe(true);
    expect(exposed.authLoading.value).toBe(false);
  });

  it("different details re-trigger loginCallback", async () => {
    const loginCb = vi.fn();
    mountHarness(loginCb);

    const d1 = new AuthorizationEventDetails("Max", "max@example.com", "keycloak", "level3", "token-1");
    const d2 = new AuthorizationEventDetails("Max", "max@example.com", "keycloak", "level3", "token-2");

    dispatchAuth(d1);
    await nextTick();
    dispatchAuth(d2);
    await nextTick();

    expect(loginCb).toHaveBeenCalledTimes(2);
  });

  it("on undefined detail: sets loggedIn=false, authLoading=false and calls logoutCallback once", async () => {
    const loginCb = vi.fn();
    const logoutCb = vi.fn();
    const { exposed } = mountHarness(loginCb, logoutCb);

    dispatchAuth(undefined);
    await nextTick();

    expect(loginCb).not.toHaveBeenCalled();
    expect(logoutCb).toHaveBeenCalledTimes(1);
    expect(exposed.loggedIn.value).toBe(false);
    expect(exposed.authLoading.value).toBe(false);
  });

  it("toggles from logged-in to logged-out on subsequent undefined detail", async () => {
    const logoutCb = vi.fn();
    const { exposed } = mountHarness(() => {}, logoutCb);

    const details = new AuthorizationEventDetails("Max", "max@example.com", "keycloak", "level3", "token-1");
    dispatchAuth(details);
    await nextTick();

    expect(exposed.loggedIn.value).toBe(true);

    dispatchAuth(undefined);
    await nextTick();

    expect(exposed.loggedIn.value).toBe(false);
    expect(logoutCb).toHaveBeenCalledTimes(1);
  });

  it("login -> logout -> login with the SAME details does not re-invoke loginCallback (prevAuthDetails remembered)", async () => {
    const loginCb = vi.fn();
    mountHarness(loginCb);

    const d = new AuthorizationEventDetails("Max", "max@example.com", "keycloak", "level3", "token-1");

    dispatchAuth(d);
    await nextTick();

    dispatchAuth(undefined);
    await nextTick();

    dispatchAuth(d);
    await nextTick();

    expect(loginCb).toHaveBeenCalledTimes(1);
  });

  it("login -> logout -> login with DIFFERENT details invokes loginCallback twice", async () => {
    const loginCb = vi.fn();
    mountHarness(loginCb);

    const d1 = new AuthorizationEventDetails("Max", "max@example.com", "keycloak", "level3", "token-1");
    const d2 = new AuthorizationEventDetails("Max", "max@example.com", "keycloak", "level3", "token-2");

    dispatchAuth(d1);
    await nextTick();

    dispatchAuth(undefined);
    await nextTick();

    dispatchAuth(d2);
    await nextTick();

    expect(loginCb).toHaveBeenCalledTimes(2);
  });

  it("authLoading becomes false after handling any authorization-event", async () => {
    const { exposed } = mountHarness();
    const d = new AuthorizationEventDetails("Max", "max@example.com", "keycloak", "level3", "token-9");

    dispatchAuth(d);
    await nextTick();
    expect(exposed.authLoading.value).toBe(false);

    dispatchAuth(undefined);
    await nextTick();
    expect(exposed.authLoading.value).toBe(false);
  });
});