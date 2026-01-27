<!-- General project links -->
[open-issues]: https://github.com/it-at-m/refarch-templates/issues
[new-issue]: https://github.com/it-at-m/refarch-templates/issues/new/choose
[milestones]: https://github.com/it-at-m/refarch-templates/milestones
[project-board]: https://github.com/orgs/it-at-m/projects/16
[documentation]: https://refarch.oss.muenchen.de/templates
[contribution-documentation]: https://refarch.oss.muenchen.de/contribute
[itm-opensource]: https://opensource.muenchen.de/
[license]: LICENSE
[code-of-conduct]: .github/CODE_OF_CONDUCT.md

<!-- Project specific links -->
[refarch-documentation]: https://refarch.oss.muenchen.de/
[refarch-code]: https://github.com/it-at-m/refarch
[spring-documentation]: https://spring.io/
[vuejs-documentation]:  https://vuejs.org/
[getting-started-documentation]: https://refarch.oss.muenchen.de/templates/getting-started
[develop-documentation]: https://refarch.oss.muenchen.de/templates/develop
[document-documentation]: https://refarch.oss.muenchen.de/templates/document
[organize-documentation]: https://refarch.oss.muenchen.de/templates/organize

<!-- Shields.io links -->
[documentation-shield]: https://img.shields.io/badge/documentation-blue?style=for-the-badge
[new-issue-shield]: https://img.shields.io/badge/new%20issue-blue?style=for-the-badge
[made-with-love-shield]: https://img.shields.io/badge/made%20with%20%E2%9D%A4%20by-it%40M-yellow?style=for-the-badge
[license-shield]: https://img.shields.io/github/license/it-at-m/refarch-templates?style=for-the-badge

# dbs
[![Made with love by it@M][made-with-love-shield]][itm-opensource]
[![GitHub license][license-shield]][license]

The repository contains components for the Digital Citizen Service (German: "Digitaler Bürgerservice" DBS) of the City of Munich.

## Build with

The project is built with technologies we use in our projects ([reference architecture][refarch-documentation]):

- [Spring][spring-documentation]
- [Vue.js][vuejs-documentation]

## Components

- [personalization service](./personalization-service) and it's corresponding [webcomponents](./personalization-webcomponents)
- [ticketing-eventing](./ticketing-eventing): Event notification via Zammad webhooks and Apache Kafka

## Getting Started

### personalization-service

1. Build it with `mvn clean install`
2. Run the stack using `podman compose up` or `docker compose up` in the stack folder
3. Run the PersonalizationServiceApplication

### personalization-webcomponents

1. Install dependencies with `npm i`
2. Run local dev server with `npm run dev`

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please open an issue with the tag "enhancement", fork the repo and
create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Open an issue with the tag "enhancement"
2. Fork the Project
3. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
4. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
5. Push to the Branch (`git push origin feature/AmazingFeature`)
6. Open a Pull Request

## License

Distributed under the MIT License. See [LICENSE][license] file for more information.

## Contact

it@M - opensource@muenchen.de
