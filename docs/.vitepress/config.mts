import { defineConfig } from "vitepress";
import { withMermaid } from "vitepress-plugin-mermaid";

// https://vitepress.dev/reference/site-config
const vitepressConfig = defineConfig({
  title: "DBS",
  description:
    "Documentation for the Digital Citizen Service of the City of Munich",
  base: "/dbs/",
  head: [
    [
      "link",
      {
        rel: "icon",
        href: `https://assets.muenchen.de/logos/lhm/icon-lhm-muenchen-32.png`,
      },
    ],
  ],
  lastUpdated: true,
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    socialLinks: [{ icon: "github", link: "https://github.com/it-at-m/dbs" }],
    search: {
      provider: "local",
    },
  },
  markdown: {
    image: {
      lazyLoading: true,
    },
  },
  ignoreDeadLinks: "localhostLinks",
});

export default withMermaid(vitepressConfig);
