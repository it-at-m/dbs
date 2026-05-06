---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "DBS"
  text: "Documentation for the Digital Citizen Service of the City of Munich"

features:
  - title: "Introduction"
    icon: 📖
    details: "Read more about what the DBS is and how it works."
    link: /introduction
  - title: "Development"
    icon: 🛠️
    details: "Getting DBS running locally for development."
    link: /development
  - title: "Deployment"
    icon: 🚀
    details: "Documentation on how to deploy the DBS."
    link: /deployment
---

The DBS (Digitaler Bürgerservice / Digital Citizen Service) is a digital service provided by the government of the city of Munich, designed to facilitate access to municipal services for citizens. The platform employs a user-centered approach, allowing citizens to quickly identify relevant services by answering targeted, straightforward questions about their current life situations.

This process generates a personalized checklist of services tailored to the user's inputs, enhancing the user experience through an intuitive interface optimized for mobile devices. The system is built on modern web technologies and follows an Open Source model, enabling other municipalities to leverage the source code and foster interoperability.

It currently consists of these architecture modules:

- [DBS-Ticketing](/architecture#ticketing)
- [DBS-P13N](/architecture#p13n)
- [DBS-Login](/architecture#login)
- [E-Appointment](https://github.com/it-at-m/eappointment) (Terminvereinbarung)
