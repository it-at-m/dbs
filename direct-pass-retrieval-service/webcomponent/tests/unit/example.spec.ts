import { describe, expect, it, vi } from "vitest";
import { getPassword, resetPassword } from "@/api/PRServiceAPI";

describe("password reset API", () => {
    it("encodes reset keys in requests", async () => {
        const fetchMock = vi.fn().mockResolvedValue(new Response());
        vi.stubGlobal("fetch", fetchMock);

        await getPassword("key/with spaces");
        await resetPassword("key/with spaces");

        expect(fetchMock).toHaveBeenNthCalledWith(
            1,
            expect.stringContaining("key%2Fwith%20spaces")
        );
        expect(fetchMock).toHaveBeenNthCalledWith(
            2,
            expect.stringContaining("resetKey=key%2Fwith+spaces"),
            expect.any(Object)
        );
    });
});
