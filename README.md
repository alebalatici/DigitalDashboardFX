# DigitalDashboardFX
### -------- work in progress -----------------------------------------------------------

## Project Structure
```
.
├── data/                       # JSON Runtime Files
├── src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── calculations/   # Physics formulas / Graph Algorithms needed for Journey parameter calculation
│   │   │   ├── core/           # Domain Entities and Validators
│   │   │   ├── gui/            # Components and Views - JavaFX
│   │   │   ├── repo/           # Repositories (In-Memory / File Storage)
│   │   │   ├── session/        # Saves all the parameters for the current section
│   │   │   └── utils/          # General Classes for files and string conversion management
│   │   │
│   │   └── resources/          # Static Resources
│   │       ├── animations/     # Media Files (GIF / MP4)
│   │       ├── default_data/   # Test data
│   │       ├── map/            # SVG and PNG map components
│   │       └── style/          # CSS files for the application's theme
│   │
│   └── test/java/org/example/  # Unit tests
└── pom.xml / build.gradle
```
## Domain Model & Class Hierarchy
```mermaid
classDiagram
    class EngineType {
        <<enumeration>>
        ICE_GASOLINE
        ICE_DIESEL
        ELECTRIC
    }

    class Vehicle {
        -int id
        -String brand
        -String model
        -int releaseYear
        -int totalKilometres
        -EngineType engineType
        -double fuelCapacity
        -double currentFuel
        -double baseConsumption
        -double currentSpeed
        -double currentRpm
        -double engineTemperature
        -double batteryHealth
    }

    class PointOfInterest {
        <<abstract>>
        -String name
        -String country
        -double x
        -double y
    }

    class City {
        -double weekdayCongestionFactor
        -double weekendCongestionFactor
    }

    class RestStation {
        <<abstract>>
        -int averageStopDuration
    }

    class GasStation {
        -boolean hasElectricCharger
        -double chargingPowerKw
    }

    class Hotel {
        -int stars
    }

    class Restaurant {
        -String cuisineType
        -double rating
    }

    PointOfInterest <|-- City
    PointOfInterest <|-- RestStation
    RestStation <|-- GasStation
    RestStation <|-- Hotel
    RestStation <|-- Restaurant
```
<img width="1918" height="974" alt="Screenshot (23)" src="https://github.com/user-attachments/assets/abba0b53-e8ca-4c53-b56c-378f8ebb0f81" />

<img width="1272" height="842" alt="Screenshot (24)" src="https://github.com/user-attachments/assets/582c38fa-41bd-410e-8cb5-cd564e2f5915" />

<img width="1261" height="837" alt="Screenshot (26)" src="https://github.com/user-attachments/assets/90649a4f-95bb-47cb-9a9b-daedd4ecdd1d" />

<img width="1273" height="838" alt="Screenshot (29)" src="https://github.com/user-attachments/assets/c8a39a90-07a1-414c-b605-501462708a52" />

<img width="1258" height="891" alt="Screenshot (27)" src="https://github.com/user-attachments/assets/f2ea9c21-8925-4904-bbf4-84173c0b8362" />

<img width="1271" height="832" alt="Screenshot (30)" src="https://github.com/user-attachments/assets/2df0cd96-5747-48b5-97dc-c3a7176bb1b4" />