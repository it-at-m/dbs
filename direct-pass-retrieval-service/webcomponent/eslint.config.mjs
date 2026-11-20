import pluginVue from "eslint-plugin-vue";

export default [
    {
        ignores: [
            "eslint.config.mjs",
            "**/target/*",
            "**/dist/*",
            "**/processes/*",
        ],
    },
    ...pluginVue.configs["flat/recommended"],
    {
        languageOptions: {
            globals: {
                NodeJS: true,
            },
            ecmaVersion: 6,
            sourceType: "module",
            parserOptions: {
                parser: "@typescript-eslint/parser",
                ecmaFeatures: {
                    jsx: false,
                },
            },
        },
        rules: {
            "vue/multi-word-component-names": "off",
            "vue/no-v-html": "off",
            "vue/html-indent": "off",
            "vue/html-closing-bracket-newline": "off",
            "vue/singleline-html-element-content-newline": "off",
        },
    },
];
