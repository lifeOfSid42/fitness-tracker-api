# Fitness Tracker API

A RESTful API built with Spring Boot for managing users, workout plans, and activity logs in a fitness tracking system.

## Table of Contents

- [Overview](#overview)
- [Technical Stack](#technical-stack)
- [Data Models](#data-models)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Database Configuration](#database-configuration)

## Overview

The Fitness Tracker API is a Spring Boot application that provides a comprehensive backend for fitness tracking
applications. It enables users to create accounts, manage workout plans, and log their fitness activities with
role-based access control.

## Technical Stack

- Java 17
- Spring Boot 3.5.3
- Spring Security
- Spring Data JPA
- H2 Database
- Lombok
- SpringDoc OpenAPI (Swagger UI)
- Maven
- Slf4j

## Data Models

### User

- Properties:
    - ID (Long)
    - Username (unique, 3-50 characters)
    - Password (min 6 characters)
    - Email (unique, validated)
    - Full Name (max 100 characters)
    - Role (USER/ADMIN)
    - Created/Updated timestamps
- Relationships:
    - One-to-Many with WorkoutPlan
    - One-to-Many with ActivityLog

### WorkoutPlan

- Properties:
    - ID (Long)
    - Name (max 100 characters)
    - Description (max 1000 characters)
    - Start Date (present or future)
    - End Date (future)
    - Difficulty Level (BEGINNER/INTERMEDIATE/ADVANCED)
- Relationships:
    - Many-to-One with User (createdBy)
    - One-to-Many with ActivityLog

## Prerequisites

- JDK 17 or higher
- Maven 3.6.0 or higher
- Git (optional)

## Setup and Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/lifeOfSid42/fitness-tracker-api.git
   cd fitness-tracker-api

2. Build the project:
   ```bash
   mvn clean install

3. Run the application:
   ```bash
   mvn spring-boot:run

The application will start on `http://localhost:7070`

## API Documentation

Access the Swagger UI documentation at:
[http://localhost:7070/swagger-ui/index.html](http://localhost:7070/swagger-ui/index.html)

## Security

### Authentication

- Uses Spring Security with Basic Authentication
- JWT tokens for API authentication

### Default Credentials

- Admin Account:
    - Username: `admin`
    - Password: `admin123`

### Authorization

Role-based access control with two roles:

- USER: Basic access to own resources
- ADMIN: Full access to all resources

### Endpoints Security

#### Public Endpoints

- POST /api/auth/login
- POST /api/users/register

#### Protected Endpoints

- User-specific operations (requires authentication)
- Admin operations (requires ADMIN role)

## Database Configuration

### H2 Console

Access the H2 database console at:
[http://localhost:7070/h2-console](http://localhost:7070/h2-console)

Configuration:

- JDBC URL: `jdbc:h2:mem:fitnesstrackerdb`
- Username: `sa`
- Password: (empty)
