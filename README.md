# Overview

The purpose is to build an API that helps users keep a log of their expenses and create goals to either limit or invest their money.

The API enables users to:
- **Track Expenses**: Log daily expenses with categories, amounts, and dates
- **Set Financial Goals**: Create budget limits (spending caps) or investment targets (savings goals)
- **Monitor Progress**: Automatically sync expenses with goals to track spending vs. targets
- **Analyze Spending**: View financial analytics and active goals
- **Secure Access**: Role-based authentication (USER and ADMIN roles) with access control


[Software Demo Video](https://youtu.be/zTlVR5kOF1I)

# Development Environment

## Tools Used

- **IDE**: Visual Studio Code with Java Extension Pack
- **Build Tool**: Apache Maven 3.9+ (with Maven Wrapper included)
- **Version Control**: Git
- **API Testing**: Postman / cURL
- **Database**: Google Firebase Firestore (Cloud NoSQL database)
- **Java Version**: Java 21 (OpenJDK)

## Programming Language and Libraries

**Language**: Java 21

**Core Frameworks**:
- **Spring Boot 3.5.6** - Main application framework for building REST APIs
- **Spring Web** - For creating RESTful web services with annotations
- **Spring Data JPA** - Database abstraction layer (used alongside Firestore)

**Firebase Integration**:
- **Firebase Admin SDK 9.4.2** - For Firestore database operations
- **Google Cloud Firestore** - NoSQL document database

**Testing**:
- **JUnit 5** - Unit and integration testing framework
- **Mockito** - Mocking framework for unit tests
- **Spring Boot Test** - Testing utilities for Spring Boot applications

**Additional Libraries**:
- **Lombok** - Reduces boilerplate code with annotations (@Data, @Getter, etc.)
- **Jackson** - JSON serialization/deserialization
- **SLF4J** - Logging facade

**Architecture**: 
The application follows the **Model-View-Controller (MVC)** pattern with clear separation of concerns:
- Controllers handle HTTP requests
- Services contain business logic
- Repositories manage data persistence
- DTOs handle data transfer between layers

# Useful Websites

- [Google Firebase](https://firebase.google.com/docs?hl=pt-br)
- [Devto](https://dev.to/whathebea/how-to-use-junit-and-mockito-for-unit-testing-in-java-4pjb)
- [Oracle](https://docs.oracle.com/en/java/javase/21/)
- [W3Schools](https://www.w3schools.com/java/java_ref_reference.asp)
- [Medium](https://medium.com/@ahmettemelkundupoglu/java-record-class-vs-dto-data-transfer-object-a-comprehensive-guide-with-pros-cons-and-949cd6881dea)

# Future Work

- Improve test writing

- Study different types of architectures for Java projects

- Use other frameworks to develop APIs