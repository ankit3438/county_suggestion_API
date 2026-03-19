# Fleet Studio - Counties Application

A Spring Boot REST API application that provides county suggestions based on search queries. The application allows users to search for US counties by name and/or state.

## Features

- **County Search API** - Search counties by name, state, or both
- **Case-Insensitive Search** - Search queries are case-insensitive
- **Result Limiting** - Returns up to 5 matching results
- **In-Memory Database** - H2 database for quick development and testing
- **Auto Data Loading** - Automatically loads county data from `data.json` on startup
- **RESTful API** - Easy-to-use GET endpoint
- **Comprehensive Testing** - Full test coverage with JUnit 5 and Mockito

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.11**
- **Spring Data JPA**
- **H2 Database** (In-memory)
- **Lombok**
- **Maven**
- **JUnit 5** (Jupiter)
- **Mockito** (for testing)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git (optional)

## Installation & Setup

### 1. Clone or Download the Project
```bash
cd Application
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Prepare Data
Place the `data.json` file in the `src/main/resources/` directory:
```
Application/
└── src/main/resources/
    └── data.json
```

The `data.json` file should contain county data in the following format:
```json
[
  {
    "fips": "53015",
    "state": "WA",
    "name": "Cowlitz"
  },
  {
    "fips": "53001",
    "state": "WA",
    "name": "Adams"
  }
]
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

Or using `mvnw`:
```bash
./mvnw spring-boot:run
```

The application will start on **http://localhost:3000**

## Directory Structure

```
Application/
├── src/main/java/com/FleetStudio/
│   ├── Application.java                 # Main Spring Boot application
│   ├── Controller/
│   │   └── ApplicationController.java    # REST endpoint definitions
│   ├── Service/
│   │   └── CountyService.java           # Business logic for county search
│   ├── Repository/
│   │   └── CountyRepository.java        # Database queries
│   ├── Entity/
│   │   └── County.java                  # County entity model
│   ├── Dto/
│   │   └── CountySuggestion.java        # Response DTO
│   └── Utility/
│       └── DataLoader.java              # Auto-loads data on startup
├── src/main/resources/
│   ├── application.properties           # Configuration file
│   ├── data.json                        # County data (place here)
│   ├── static/                          # Static files
│   └── templates/                       # HTML templates
├── src/test/java/com/FleetStudio/
│   ├── Controller/
│   │   └── ApplicationControllerTest.java
│   ├── Service/
│   │   └── CountyServiceTest.java
│   └── Repository/
│       └── CountyRepositoryTest.java
├── pom.xml                              # Maven dependencies
├── mvnw / mvnw.cmd                      # Maven wrapper
└── README.md                            # This file
```

## API Endpoints

### GET /suggest
Search for county suggestions based on a query string.

**Parameters:**
- `q` (required) - Search query (county name and/or state code)

**Request Examples:**
```bash
# Search by county name and state
curl "http://localhost:3000/suggest?q=cowlitz,wa"

# Search by state code
curl "http://localhost:3000/suggest?q=wa"

# Search by county name
curl "http://localhost:3000/suggest?q=cowl"
```

**Response Format:**
```json
[
  {
    "fips": "53015",
    "state": "WA",
    "name": "Cowlitz"
  },
  {
    "fips": "53001",
    "state": "WA",
    "name": "Adams"
  }
]
```

**Response Codes:**
- `200 OK` - Search completed successfully
- `400 Bad Request` - Missing required `q` parameter

## Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.application.name=Application

# H2 database configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create
spring.h2.console.enabled=true

# Disable SQL logging (optional)
logging.level.org.hibernate.SQL=OFF
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=OFF

# Server port
server.port=3000
```

### H2 Console
Access the H2 database console at: **http://localhost:8080/h2-console**
- URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (leave blank)

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=CountyServiceTest
mvn test -Dtest=ApplicationControllerTest
mvn test -Dtest=CountyRepositoryTest
```

### Run with Coverage
```bash
mvn test -Dtest=*Test
```

## Test Coverage

The application includes comprehensive test coverage:

### CountyServiceTest (8 tests)
- Search with name and state
- Search by state only
- Search by name only
- No results scenario
- Result limiting to 5
- Whitespace trimming
- Output mapping

### ApplicationControllerTest (5 tests)
- Valid query handling
- State-only query
- Name-only query
- No results
- Missing parameter handling

### CountyRepositoryTest (3 tests)
- Find by name
- Find by state
- Find by name and state combined

## Key Classes

### CountyService
Handles the business logic for searching counties:
- Parses the query string (handles comma-separated name and state)
- Calls repository methods based on query format
- Maps entities to DTOs
- Limits results to 5

### ApplicationController
Exposes the REST endpoint:
- `@GetMapping("/suggest")` - Main search endpoint
- Returns `ResponseEntity<List<CountySuggestion>>`

### CountyRepository
Spring Data JPA repository with custom query methods:
- `findByNameContainingIgnoreCase()` - Case-insensitive name search
- `findByStateIgnoreCase()` - Case-insensitive state search
- `findByNameContainingIgnoreCaseAndStateIgnoreCase()` - Combined search

### DataLoader
Automatically loads data on application startup:
- Reads `data.json` from classpath
- Parses JSON using Jackson ObjectMapper
- Saves all counties to the database

## Usage Examples

### Example 1: Search by County Name and State
```bash
curl "http://localhost:3000/suggest?q=Cowlitz,WA"
```
Response:
```json
[
  {
    "fips": "53015",
    "state": "WA",
    "name": "Cowlitz"
  }
]
```

### Example 2: Search by State
```bash
curl "http://localhost:3000/suggest?q=WA"
```
Response: Array of all Washington counties (up to 5 results)

### Example 3: Search by County Name
```bash
curl "http://localhost:3000/suggest?q=cowl"
```
Response: All counties with "cowl" in the name (case-insensitive)

## Common Issues & Solutions

### Data Not Loading
- Ensure `data.json` is placed in `src/main/resources/`
- Check that the JSON format is correct
- Restart the application

### Connection Refused
- Verify the application is running on port 3000
- Check if the port is already in use: `netstat -an | findstr :3000`

### H2 Console Not Accessible
- Ensure `spring.h2.console.enabled=true` in `application.properties`
- Access it at `http://localhost:8080/h2-console` (different port)

## Development Notes

- The application uses constructor injection via Lombok's `@RequiredArgsConstructor`
- All search operations are case-insensitive
- The `DataLoader` component implements `CommandLineRunner` for automatic initialization
- Results are always limited to 5 entries
- Whitespace is automatically trimmed from search queries

## Future Enhancements

- Add pagination support
- Support advanced filtering options
- Add database persistence (PostgreSQL)
- Implement caching for frequently searched counties
- Add authentication & authorization
- API rate limiting

## Author

Developed by: **Ankit Jha**
- Email: ankitjha481@gmail.com

## License

This project is open source and available under the MIT License.

## Support

For issues or questions, please contact the development team.

