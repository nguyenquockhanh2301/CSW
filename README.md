# Employee Web Service

A small Java web application for managing employees with `id`, `name`, and `salary` fields. It provides a browser-based tester and JSON endpoints for listing, adding, and updating employees.

## Requirements

- Java JDK 21
- Apache Maven 3.9 or later
- Apache Tomcat 8.5

The project uses SQLite for storage. The database file is created automatically at:

```text
%USERPROFILE%\csw-employees.db
```

## Build and Run

1. Build the WAR file:

   ```bash
   mvn clean package
   ```

2. Copy `target/employee-webservice.war` into Tomcat's `webapps` directory.

3. Start Tomcat and open:

   ```text
   http://localhost:8080/employee-webservice/
   ```

## API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/employee-webservice/api/employees` | List all employees |
| `POST` | `/employee-webservice/api/employees` | Add an employee |
| `PUT` | `/employee-webservice/api/employees` | Update an employee |

