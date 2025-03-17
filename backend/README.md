![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)

# SC FleetFinder

FleetFinder is a full-stack web app that helps players of the MMO *Star Citizen* find and join gameplay groups 
based on shared interests.

> 🚀 **Version:** v1.0.0  
> 🧪 Current focus: Core CRUD functionality, REST API, and frontend prototype

---

## 🔍 What is FleetFinder?

FleetFinder is a companion app for the MMO video game *Star Citizen*, a space simulation game developed by Cloud 
Imperium Games. As the game is still in alpha and lacks a built-in group finder, FleetFinder helps players team up for 
collaborative or combative gameplay sessions.

---

## 💡 Why I Built This

This project allows me to apply and expand my software development skills beyond my CS coursework at WGU. It solves a 
real problem for *Star Citizen* players while helping me grow as a fullstack developer.

---

## 🛠️ Tech Stack

### Backend
- Java 22
- Spring Boot (REST APIs, caching, dependency injection)
- Spring Data JPA
- Maven

### Frontend
- Angular 19
- TypeScript
- Bootstrap, HTML/CSS

### Database
- MySQL 8.2
- MySQL Workbench
- Flyway for DB migrations

### Testing
- JUnit 5, Spring Boot Test
- Mockito, MockMVC
- Postman

### Deployment / DevOps
- Docker & Docker Compose
- AWS (EC2 or other hosting)
- Flyway for DB migrations

### Supporting Libraries
- Lombok
- ModelMapper
- EhCache
- dotenv (env var management)
- SLF4J + Logback (logging)

### Planned Integrations
- Jenkins (CI/CD)
- Keycloak (authentication)
- RabbitMQ (async message handling)
- WebSockets (real-time chat and notifications)

---

## 🚦 FleetFinder v1 (Current)

**Version 1** includes the core CRUD functionality:

- MySQL database with group listings and static lookup tables
- Spring Boot backend with RESTful APIs
- Angular frontend with reactive forms and a listing table
- Responsive layout for different screen sizes
- Unit + integration tests for services, controllers, and mappers

> Note: Group listings are created using a hardcoded test user in v1. User accounts will be implemented in v2.

---

## 🗺️ Roadmap

### Coming in v2 (MVP)
- User authentication with Keycloak
- CI/CD pipeline with Jenkins
- Secured API endpoints
- Refactored frontend using Angular Material
- Listing search/filter functionality
- Real-time messaging

### Planned Post-MVP Features
- Admin moderation tools
- Report and remove listings
- Save listing templates
- Basic analytics for listing trends

---

## 📦 How to Run (coming soon)

Instructions for running locally with Docker Compose and populating the database will be added after deployment 
is finalized.

---

## 🙌 Contributing / Contact

This is a personal portfolio project and not currently open for contributions, but feel free to reach out with 
feedback or questions!

---

## License

SC FleetFinder is licensed under the GNU General Public License v3.0.
See the [LICENSE](./LICENSE) file for details.