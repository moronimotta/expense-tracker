# Expense Tracker Application

A Spring Boot REST API application for personal expense tracking and goal management with role-based security and Firebase integration.

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)
- [Authentication & Security](#authentication--security)
- [Testing](#testing)
- [Development](#development)
- [Configuration](#configuration)

## ✨ Features

### Core Functionality
- **User Management**: Create and manage user accounts with role-based access control
- **Expense Tracking**: Record and categorize personal expenses
- **Goal Management**: Set financial goals with automatic progress tracking
- **Security**: Admin-only endpoints and user access validation
- **Data Persistence**: Firebase Firestore integration for cloud storage

### Technical Features
- **REST API**: Full CRUD operations with JSON responses
- **MVC Architecture**: Clean separation of concerns
- **Role-Based Security**: USER and ADMIN roles with different permissions
- **Input Validation**: Request validation with custom exception handling
- **Soft Delete**: Non-destructive data removal
- **Goal Synchronization**: Automatic calculation of goal progress based on expenses

## 🏗️ Architecture

The application follows **Model-View-Controller (MVC)** pattern with clean separation:

```
src/main/java/com/expensetracker/app/
├── controllers/       # REST endpoints and request handling
├── services/          # Business logic and security
├── repositories/      # Data access layer (Firebase Firestore)
├── models/            # Entity classes and domain objects
│   └── enums/         # Enumeration types
├── dto/               # Data Transfer Objects for API
├── config/            # Configuration classes
├── exceptions/        # Custom exception classes
└── utils/             # Utility classes (future use)
```

### Layer Responsibilities

#### Controllers (`controllers/`)
- Handle HTTP requests and responses
- Input validation and request mapping
- Delegate business logic to services

#### Services (`services/`)
- **SecurityService**: Authentication, authorization, and access control
- Business logic and workflow orchestration
- Transaction management

#### Repositories (`repositories/`)
- Data access abstraction
- Firebase Firestore operations
- Query optimization and indexing

#### Models (`models/`)
- **BaseEntity**: Common fields (id, timestamps, soft delete)
- **User**: User account information
- **Expense**: Expense records with categorization
- **Goal**: Financial goals with progress tracking

#### DTOs (`dto/`)
- Request/Response data structures
- API input validation
- Global exception handling

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Firebase Project** with Firestore enabled
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd expense-tracker
   ```

2. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Enable Firestore Database
   - Generate a service account key
   - Place the JSON file at `src/main/resources/firebase-service-account.json`

3. **Build the project**
   ```bash
   ./mvnw clean compile
   ```

4. **Run tests**
   ```bash
   ./mvnw test
   ```

5. **Start the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Quick Test Scripts

- **Build**: `./build.bat` (Windows) or `./mvnw compile`
- **Test**: `./test.bat` (Windows) or `./mvnw test`
- **Run**: `./run.bat` (Windows) or `./mvnw spring-boot:run`

## 🔌 API Endpoints

### User Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/users` | Create new user | Public |
| GET | `/users` | List all users | Admin only |
| GET | `/users/{id}` | Get user by ID | Admin only |
| PUT | `/users/{id}` | Update user | Admin only |
| DELETE | `/users/{id}` | Delete user | Admin only |

### Expense Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/expenses` | Create expense | User (own) / Admin |
| GET | `/expenses/user/{userId}` | Get user expenses | User (own) / Admin |
| GET | `/expenses/{id}` | Get expense by ID | User (own) / Admin |
| PUT | `/expenses/{id}` | Update expense | User (own) / Admin |
| DELETE | `/expenses/{id}` | Delete expense | User (own) / Admin |

### Goal Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/goals` | Create goal | User (own) / Admin |
| GET | `/goals` | List all goals | Admin only |
| GET | `/goals/user/{userId}` | Get user goals | User (own) / Admin |
| GET | `/goals/{id}` | Get goal by ID | User (own) / Admin |
| PUT | `/goals/{id}` | Update goal | User (own) / Admin |
| DELETE | `/goals/{id}` | Delete goal | User (own) / Admin |
| POST | `/goals/{id}/sync` | Sync goal progress | User (own) / Admin |

### Request Examples

#### Create User
```json
POST /users
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securepassword",
  "role": "USER"
}
```

#### Create Expense
```json
POST /expenses
{
  "userId": "user-id-here",
  "description": "Grocery shopping",
  "amount": 85.50,
  "category": "Food",
  "date": "10/03/2025",
  "goalId": "goal-id-here"
}
```

#### Create Goal
```json
POST /goals
{
  "userId": "user-id-here",
  "title": "Monthly Budget",
  "description": "October spending limit",
  "targetAmount": 1000.00,
  "startDate": "10/01/2025",
  "endDate": "10/31/2025",
  "mode": "LIMIT",
  "category": "General"
}
```

## 🗄️ Database Schema

### User
- `id` (String): Unique identifier
- `name` (String): Full name
- `email` (String): Unique email address
- `password` (String): Encrypted password
- `role` (UserRole): USER or ADMIN
- `createdAt`, `updatedAt`, `deletedAt` (Timestamp): Audit fields

### Expense
- `id` (String): Unique identifier
- `userId` (String): Reference to User
- `description` (String): Expense description
- `amount` (BigDecimal): Expense amount
- `category` (ExpenseCategory): Predefined categories
- `date` (Timestamp): Expense date
- `goalId` (String): Optional reference to Goal
- `createdAt`, `updatedAt`, `deletedAt` (Timestamp): Audit fields

### Goal
- `id` (String): Unique identifier
- `userId` (String): Reference to User
- `title` (String): Goal title
- `description` (String): Goal description
- `targetAmount` (BigDecimal): Target amount
- `currentAmount` (BigDecimal): Current progress
- `startDate`, `endDate` (Timestamp): Goal period
- `mode` (GoalMode): LIMIT or INVESTMENT
- `status` (GoalStatus): ACTIVE, UNDER_LIMIT, EXCEEDED, SURPASSED
- `category` (String): Goal category
- `createdAt`, `updatedAt`, `deletedAt` (Timestamp): Audit fields

### Enumerations

#### UserRole
- `USER`: Standard user access
- `ADMIN`: Administrative access

#### ExpenseCategory
- `FOOD`, `TRAVEL`, `UTILITIES`, `HOUSING`
- `TRANSPORTATION`, `ENTERTAINMENT`, `GENERAL`, `OTHER`

#### GoalMode
- `LIMIT`: Spending limit goal
- `INVESTMENT`: Savings/investment goal

#### GoalStatus
- `ACTIVE`: Goal is active
- `UNDER_LIMIT`: Spending is under limit
- `EXCEEDED`: Limit has been exceeded
- `SURPASSED`: Investment goal reached

## 🔐 Authentication & Security

### Role-Based Access Control

The application implements two-tier security:

1. **Admin-Only Endpoints**: Require ADMIN role
   - `GET /users` - List all users
   - `GET /goals` - List all goals

2. **User Access Validation**: Users can only access their own data
   - Expenses: Users can only manage their own expenses
   - Goals: Users can only manage their own goals
   - Admins can access all data

### Security Service

The `SecurityService` provides:
- `requireAdmin()`: Validates admin role
- `validateUserAccess(userId)`: Ensures user can access specific user data
- `setCurrentUserId(userId)`: Sets current user context (for testing)

### Exception Handling

Security violations return appropriate HTTP status codes:
- `403 Forbidden`: Admin role required
- `403 Forbidden`: Access denied to other user's data
- `400 Bad Request`: Invalid input data
- `409 Conflict`: Duplicate email address
- `500 Internal Server Error`: Server errors

## 🧪 Testing

The application includes comprehensive test suite:

### Test Categories

1. **Unit Tests**
   - SecurityServiceTest (5 tests)
   - Repository layer tests
   - Service layer tests

2. **Integration Tests**
   - ExpenseTrackerIntegrationTest (7 tests)
   - End-to-end API testing
   - Database integration testing

3. **Security Tests**
   - RoleBasedSecurityTest (3 tests)
   - SecurityTest (2 tests)
   - Access control validation

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=SecurityServiceTest

# Run with coverage
./mvnw test jacoco:report
```

### Test Results
- **Total Tests**: 18
- **Success Rate**: 100%
- **Coverage**: Comprehensive coverage of all layers

## 🛠️ Development

### Build Commands

```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw package

# Run application
./mvnw spring-boot:run
```

### Development Tools

- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Build Tool**: Maven (wrapper included)
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Database**: Firebase Firestore
- **Logging**: Spring Boot default logging (Logback)

### Code Style

The project follows standard Java conventions:
- **Package naming**: `com.expensetracker.app.*`
- **Class naming**: PascalCase
- **Method naming**: camelCase
- **Constants**: UPPER_SNAKE_CASE
- **Indentation**: 4 spaces

## ⚙️ Configuration

### Application Properties

```properties
# Application
spring.application.name=app
server.port=8080

# Firebase
firebase.credentials.path=classpath:firebase-service-account.json

# Database (H2 for testing)
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop

# Logging
logging.level.com.expensetracker=DEBUG
```

### Firebase Configuration

1. Create Firebase project
2. Enable Firestore Database
3. Generate service account key
4. Place JSON file in `src/main/resources/`
5. Update `FirebaseConfig.java` if needed

### Required Firestore Indexes

For optimal performance, create these composite indexes:

```javascript
// Expenses by user and date range
collection: "expenses"
fields: [
  { field: "deletedAt", order: "ASCENDING" },
  { field: "userId", order: "ASCENDING" },
  { field: "date", order: "ASCENDING" }
]
```

## 📖 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Firebase Firestore Documentation](https://firebase.google.com/docs/firestore)
- [Maven Documentation](https://maven.apache.org/guides/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is for educational purposes as part of CSE310 coursework.

---

**Built with Spring Boot, Firebase, and Java 21**