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
    sidebar: [
      { text: "Overview", collapsed: false, items: [
          {text: "Home", link: "/"},
          {text: "Architecture", link: "/architecture"},
      ]},
      { text: "Components", collapsed: false, items: [
          {text: "dbs-ticketing", link: "/dbs-ticketing", collapsed: true, items: [
              {text: "Development", link: "/dbs-ticketing/development"},
              {text: "Deployment", link: "/dbs-ticketing/deployment"},
          ]},
          {text: "dbs-p13n", link: "/dbs-p13n", collapsed: true, items: [
              {text: "Development", link: "/dbs-p13n/development"},
              {text: "Deployment", link: "/dbs-p13n/deployment"},
          ]},
          {text: "dbs-login", link: "/dbs-login"},
      ]}
    ],
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
