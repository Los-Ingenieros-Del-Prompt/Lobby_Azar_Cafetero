# Azar Cafetero - Lobby Service

Welcome to the **Lobby Service** repository for Azar Cafetero. This microservice acts as the orchestrator and matchmaking engine for the multiplayer platform. It is responsible for bridging the gap between authenticated players and active game sessions, ensuring a smooth transition from the waiting room to the game board.

## 🚀 Technology Stack

- **[Java & Spring Boot](https://spring.io/projects/spring-boot)**: The core framework providing a scalable environment for managing complex, concurrent room states.
- **[Maven](https://maven.apache.org/)**: Build and dependency management.
- **REST APIs**: Exposes endpoints for room creation and player interaction.
- **[Docker](https://www.docker.com/)**: Containerized for robust, scalable cloud deployment alongside the rest of the microservices.
- **SonarQube**: Enforcing strict code quality, test coverage, and maintainability.

## 🛠️ Architecture & Responsibilities

The Lobby Service is the central hub for multiplayer organization:

### 1. Matchmaking & Waiting Rooms
- **Room Creation**: Players can initialize new waiting rooms for specific games (e.g., Brisca, Parqués), configuring rulesets and maximum player counts.
- **Joining/Leaving**: Manages concurrent requests as players join or leave rooms, ensuring capacities are respected and preventing race conditions.
- **State Broadcasting**: While this service handles the logic, it heavily relies on the WebSocket Gateway to broadcast state changes (like "Player X joined") to all clients in the waiting room via STOMP topics (e.g., `/topic/lobby/{roomId}`).

### 2. Room Lifecycle & Cleanup
- **Automated Sweeping**: Contains background tasks or event listeners designed to identify and delete "ghost" rooms—tables that were created but never joined, or tables abandoned by all human players.
- **Game Initialization**: Once a waiting room reaches capacity or the host starts the game, the Lobby Service triggers the initialization of the specific game engine (Brisca or Parqués), transferring the player session states.

### 3. Bot Management Integration
- Coordinates the addition of AI opponents to waiting rooms when a player chooses to play against the computer or fill empty seats.

## 🏃‍♂️ Getting Started

### Prerequisites
- Java 17+ (JDK)
- Maven 3.8+

### Running Locally

Use the included Maven wrapper to start the service in your local development environment:

```bash
./mvnw spring-boot:run
```

### Docker Deployment

Build and run using Docker to simulate the production environment:

```bash
docker build -t azarcafetero-lobby .
docker run -p 8081:8081 azarcafetero-lobby
```

## 🧪 Testing

The Lobby Service logic handles heavy concurrency, making rigorous testing essential.

Run the unit and integration test suite:
```bash
./mvnw test
```