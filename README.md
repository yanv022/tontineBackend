# Spring Boot JWT Authentication with PostgreSQL

This is a Spring Boot application that implements JWT authentication with PostgreSQL database.

## Prerequisites

- Java 17
- Maven
- PostgreSQL
- Postman (for testing)

## Database Setup

1. Create a PostgreSQL database named `testdb`:
```sql
CREATE DATABASE testdb;
```

2. Update database configuration in `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/testdb
spring.datasource.username=postgres
spring.datasource.password=123456
```

3. The tables will be automatically created when you start the application.

4. Insert the required roles into the database:
```sql
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Testing with Postman

### 1. User Registration (Sign Up)

- **URL**: `POST http://localhost:8080/api/auth/signup`
- **Headers**: 
  - Content-Type: application/json
- **Body**:
```json
{
    "username": "testuser",
    "email": "test@example.com",
    "password": "123456",
    "role": ["user"]
}
```

For admin registration, use:
```json
{
    "username": "admin",
    "email": "admin@example.com",
    "password": "123456",
    "role": ["admin"]
}
```

### 2. User Authentication (Sign In)

- **URL**: `POST http://localhost:8080/api/auth/signin`
- **Headers**: 
  - Content-Type: application/json
- **Body**:
```json
{
    "username": "testuser",
    "password": "123456"
}
```
or 

```json
{
    "username": "admin",
    "password": "123456"
}
```


The response will contain the JWT token:
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "roles": ["ROLE_USER"]
}
```

### 3. Accessing Protected Resources

Use the token received from signin to access protected endpoints:

1. Public Content:
- **URL**: `GET http://localhost:8080/api/test/all`
- No authentication needed

2. User Content (Protected):
- **URL**: `GET http://localhost:8080/api/test/user`
- **Headers**: 
  - Authorization: Bearer {your_token}

3. Admin Content (Protected):
- **URL**: `GET http://localhost:8080/api/test/admin`
- **Headers**: 
  - Authorization: Bearer {your_token}

### 4. Tontine Group Management

All tontine endpoints require authentication. Use the token from signin in the Authorization header.

#### Create Tontine Group
- **URL**: `POST http://localhost:8080/api/tontine/groups`
- **Headers**: 
  - Content-Type: application/json
  - Authorization: Bearer {your_token}
- **Body**:
```json
{
    "name": "Family Tontine",
    "amount": 1000.00,
    "currency": "EUR",
    "frequency": "MONTHLY"
}
```
Note: Currency can be either "EUR" or "FCFA", frequency can be "WEEKLY", "MONTHLY", or "YEARLY"

#### Get All Tontine Groups
- **URL**: `GET http://localhost:8080/api/tontine/groups`
- **Headers**: 
  - Authorization: Bearer {your_token}
- Regular users will see only their groups
- Admins will see all groups

#### Get Specific Tontine Group
- **URL**: `GET http://localhost:8080/api/tontine/groups/{id}`
- **Headers**: 
  - Authorization: Bearer {your_token}
- Users can only view their own groups
- Admins can view any group

#### Update Tontine Group
- **URL**: `PUT http://localhost:8080/api/tontine/groups/{id}`
- **Headers**: 
  - Content-Type: application/json
  - Authorization: Bearer {your_token}
- **Body**:
```json
{
    "name": "Updated Family Tontine",
    "amount": 2000.00,
    "currency": "FCFA",
    "frequency": "WEEKLY"
}
```
- Users can only update their own groups
- Admins can update any group

#### Delete Tontine Group
- **URL**: `DELETE http://localhost:8080/api/tontine/groups/{id}`
- **Headers**: 
  - Authorization: Bearer {your_token}
- Users can only delete their own groups
- Admins can delete any group

## Testing Flow

1. Create a new user using the signup endpoint
2. Login with the created user using the signin endpoint
3. Copy the JWT token from the response
4. Use the token in the Authorization header to access protected endpoints
5. Test different endpoints with different user roles
6. Create and manage tontine groups using the tontine endpoints

## Common HTTP Status Codes

- 200: Success
- 400: Bad Request (e.g., invalid input)
- 401: Unauthorized (invalid/missing token)
- 403: Forbidden (insufficient permissions)
- 404: Not Found
- 500: Internal Server Error