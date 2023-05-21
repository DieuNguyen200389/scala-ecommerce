# Scala REST API Application with Play Framework

### Technology Stack
- Scala 2.13
- Play Framework 2.8.19
- Slick 5.0.0 (DB Access/Evolutions)
- PostgreSQL
- Guice (DI)
- Silhouette (Authn/Authz)
- HTTPClient (Play WS)
- JSON Conversion (Play JSON)
- Logging (Logback)

### Getting Started

#### 1. Setup `PostgreSQL` Database
You can install PostgreSQL on your local machine or running the docker compose in the `/docker/database` folder
to get PostgreSQL ready.

#### 2. Run application 
You need to download and install sbt for this application to run.
_Note: I've added the `SBT bin` to this project to build the source code without SBT installation_
Once you have sbt installed, the following at the command prompt will start up Play in development mode:
```bash
./sbt run
```

Play will start up on the HTTP port at <http://localhost:8080/>.   You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.

#### 3. Run Unit Tests
```bash
./sbt clean test
```

or To generate code coverage report with SCoverage
```bash
./sbt clean coverage test coverageReport
```

#### 4. Run Integration Tests
```bash
./sbt clean integration/test
```

### Usage
_Ref: Postman collection at `postman` folder_
All API endpoints must require authentication except SignIn and SignUp

1. Create an User by using `POST /SignUp` endpoint
2. Using `POST- /SignIn` endpoint to login with newly created user to get JWT token in `X-Auth` response header
3. Only authenticated users with the Admin role should be allowed to perform CRUD operations on Users.
   - Get All Users via `GET /v1/users` endpoint
   - Get User by ID via `GET /v1/users/{id}` endpoint
   - Update User via `PUT /v1/users/{id}` endpoint
   - Delete User via `DELETE /v1/users/{id}` endpoint
   - Change password via `POST /v1/users/password/change` endpoint
4. Only authenticated users with the Operator or Admin role should be allowed to retrieve product information from the External Products. (And then register these external products to the local Products table via exposed APIs)
   And perform CUD operation on Products.
   All authenticated Users should be allowed to retrieve the Products information so that they can create the Orders.
   - Get All Products via `GET /v1/products` endpoint
   - Get Product by ID via `GET /v1/products/{id}` endpoint
   - Update Product via `PUT /v1/products/{id}` endpoint
   - Create new Product via `POST /v1/products` endpoint
   - Delete Product via `DELETE /v1/products/{id}` endpoint
   - Get external products via `GET /v1/external/products` endpoint
5. All authenticated Users should be allowed to perform CRUD operations on their own Orders.
   Admin should be allowed to perform CRUD operations on all Orders.
    - Get All Orders via `GET /v1/orders` endpoint
    - Get Order by ID via `GET /v1/orders/{id}` endpoint
    - Update Order via `PUT /v1/orders/{id}` endpoint
    - Create new Order via `POST /v1/orders` endpoint
    - Delete Order via `DELETE /v1/orders/{id}` endpoint