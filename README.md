## Table of Contents

- [Technologies Used](#technologies-used)
- [Setup and Installation](#setup-and-installation)
- [Project Structure](#project-structure)
- [Features](#features)
  - [User Management](#user-management)
  - [Product Management](#product-management)
  - [Authentication](#authentication)
  - [CRUD Operations](#crud-operations)
- [Endpoints](#endpoints)
  - [User Endpoints](#user-endpoints)
  - [Product Endpoints](#product-endpoints)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Technologies Used

- **Spring Boot**: Framework for building the backend application.
- **Spring Security**: Provides authentication and authorization.
- **JPA (Hibernate)**: ORM for database interactions.
- **MySQL**: Database for storing user and product data.
- **JWT (JSON Web Tokens)**: For authentication and authorization.
- **Lombok**: To reduce boilerplate code (getters, setters, constructors).
- **ModelMapper**: For object-to-object mapping.
- **JUnit 5**: For unit testing the application.

## Setup and Installation

Follow these steps to set up the project locally:

1. **Clone the repository**:

    ```bash
    git clone https://github.com/your-username/spring-boot-user-product-management.git
    cd spring-boot-user-product-management
    ```

2. **Set up the database**:
    - Create a MySQL database called `product_management`.
    - Set up the connection in the `application.properties` file.

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/product_management
    spring.datasource.username=root
    spring.datasource.password=your-password
    spring.jpa.hibernate.ddl-auto=update
    ```

3. **Build the project**:
    - Make sure you have Maven installed.
    - Run the following Maven command to build the application:

    ```bash
    mvn clean install
    ```

4. **Run the project**:

    ```bash
    mvn spring-boot:run
    ```

5. **Test the application** by navigating to `http://localhost:8080` on your browser or using Postman.

## Project Structure

```
src
 └── main
     ├── java
     │   └── com
     │       └── example
     │           └── product
     │               ├── controller
     │               │   └── UserController.java
     │               │   └── ProductController.java
     │               ├── model
     │               │   └── UserModel.java
     │               │   └── ProductModel.java
     │               ├── repository
     │               │   └── UserRepository.java
     │               │   └── ProductRepository.java
     │               ├── service
     │               │   └── UserService.java
     │               │   └── ProductService.java
     │               │   └── JwtService.java
     │               ├── security
     │               │   └── SecurityConfig.java
     │               ├── ProductApplication.java
     └── resources
         └── application.properties
```

## Features

### User Management

- **User Registration**: Users can register by providing a `firstName`, `lastName`, `email`, and `password`.
- **User Login**: Users can log in using their email and password to get a JWT token for authentication.
- **Get User Details**: Users can retrieve their personal details (only if authenticated).
- **Delete User**: Users can delete their account (only if authenticated).
- **Update User**: Users can update their personal information.

### Product Management

- **Product CRUD**: Authenticated users can create, read, update, and delete products.
- **Product Association**: Each product is associated with a user. A user can only manage their own products.
- **List Products**: Users can list all their products.

### Authentication

- **JWT Authentication**: The system uses JWT tokens for authentication and authorization.
- **Security**: Spring Security is used to protect endpoints, ensuring only authenticated users can perform certain actions.

### CRUD Operations

- Users can perform the following CRUD operations:
    - **Create**: Create a new user or product.
    - **Read**: Retrieve a list of users or products.
    - **Update**: Modify user details or product attributes.
    - **Delete**: Remove a user or product from the system.

## Endpoints

### User Endpoints

- **POST /user/register**  
  - Register a new user.  
  - Request body:  
    ```json
    {
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "password": "password123"
    }
    ```

- **POST /user/login**  
  - Login and get a JWT token.  
  - Request body:  
    ```json
    {
      "email": "john.doe@example.com",
      "password": "password123"
    }
    ```

- **GET /user**  
  - Get the logged-in user's details.  
  - Requires JWT authentication in headers.

- **GET /users**  
  - Get all users that have registered.  
  - Requires JWT authentication in headers.

- **PUT /user**  
  - Update user details.  
  - Request body:  
    ```json
    {
      "firstName": "Jonathan",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "password": "newpassword123"
    }
    ```

- **DELETE /user**  
  - Delete the logged-in user.  
  - Requires JWT authentication in headers.

### Product Endpoints

- **POST /product**  
  - Create a new product.  
  - Request body:  
    ```json
    {
      "name": "Product 1",
      "description": "Product description",
      "price": 20.0
    }
    ```

- **GET /product**  
  - Get all products of the logged-in user.  
  - Requires JWT authentication in headers.

- **PUT /product/{productId}**  
  - Update a product.  
  - Request body:  
    ```json
    {
      "name": "Updated Product",
      "description": "Updated description",
      "price": 30.0
    }
    ```

- **DELETE /product/{productId}**  
  - Delete a product.  
  - Requires JWT authentication in headers.


- **GET /products/seaarch?productname**  
  - Retrieves all product Name with the queried details.


## Testing

- Unit tests are written using **JUnit 5** and **Mockito**.
- To run the tests:

    ```bash
    mvn test
    ```


This README provides a complete overview of the Spring Boot application that includes user management and product management functionality. Make sure to tailor it further if there are any additional requirements or modifications specific to your project.
