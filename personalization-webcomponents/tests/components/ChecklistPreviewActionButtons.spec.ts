import { mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";

import ChecklistPreviewActionButtons from "../../src/components/checklistPreview/ChecklistPreviewActionButtons.vue";

// Minimal stub for muc-button to propagate native click events and allow
// clicking on both an icon and content area.
const MucButtonStub = {
  name: "muc-button",
  emits: ["click"],
  // Intentionally keep structure simple: a button with two children so we can
  // simulate clicks on both regions and ensure currentTarget is the button.
  template: `
    <button data-test="muc-button" @click="onClick">
      <span class="icon"><slot name="icon"></slot></span>
      <span class="content"><slot></slot></span>
    </button>
  `,
  methods: {
    onClick(e: MouseEvent) {
      // Forward the original DOM event so the parent handler receives the
      // correct currentTarget (the button element) and target (the child).
      this.$emit("click", e);
    },
  },
};

describe("ChecklistPreviewActionButtons - print handler", () => {
  const t = (key: string) => key;

  beforeEach(() => {
    vi.restoreAllMocks();
  });

  function mountCmp() {
    return mount(ChecklistPreviewActionButtons, {
      props: {
        // Use non-default language to hide the save button and keep ordering
        // predictable: copy, then print.
        currentLang: "en",
        t,
      },
      global: {
        stubs: {
          "muc-button": MucButtonStub,
        },
      },
    });
  }

  it("blurs button and prints when clicking on icon", async () => {
    const wrapper = mountCmp();

    const printSpy = vi.spyOn(window, "print").mockImplementation(() => void 0);

    const buttons = wrapper.findAll('[data-test="muc-button"]');
    expect(buttons.length).toBeGreaterThanOrEqual(2);
    const printBtn = buttons[buttons.length - 1];

    // Spy on blur of the actual button element to ensure it is invoked.
    const blurSpy = vi.spyOn(printBtn.element as HTMLElement, "blur");

    // Focus then click on the icon child; event.target will be the icon span,
    // event.currentTarget will be the button. Our handler should blur the
    // button and then call window.print.
    (printBtn.element as HTMLElement).focus();
    await printBtn.find(".icon").trigger("click");

    expect(blurSpy).toHaveBeenCalledTimes(1);
    expect(printSpy).toHaveBeenCalledTimes(1);
  });

  it("blurs button and prints when clicking on text content", async () => {
    const wrapper = mountCmp();

    const printSpy = vi.spyOn(window, "print").mockImplementation(() => void 0);

    const buttons = wrapper.findAll('[data-test="muc-button"]');
    expect(buttons.length).toBeGreaterThanOrEqual(2);
    const printBtn = buttons[buttons.length - 1];

    const blurSpy = vi.spyOn(printBtn.element as HTMLElement, "blur");

    (printBtn.element as HTMLElement).focus();
    await printBtn.find(".content").trigger("click");

    expect(blurSpy).toHaveBeenCalledTimes(1);
    expect(printSpy).toHaveBeenCalledTimes(1);
  });
});
