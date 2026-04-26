# Faculty of Technology Management System

A desktop-based university management system built with Java, JavaFX, and MySQL. The project separates the user interface and the backend server into two Maven modules and supports multiple academic workflows such as course management, attendance, continuous assessment, medical handling, notices, registration, and eligibility tracking.

## Overview

This project provides a role-based academic management platform for:

- `Admin`
- `Dean`
- `Lecturer`
- `Student`
- `Tech_Officer`

The frontend is a JavaFX client application, while the backend runs as a socket-based Java server. Data is stored in MySQL.

## Key Features

- Role-based login with JWT-backed session handling
- Admin dashboard for users, notices, courses, timetable, and lecturer-course assignment
- Lecturer tools for assigned courses, CA marks, final marks, session management, eligibility, GPA/grade views, and profile updates
- Student tools for registered courses, attendance, grades, eligibility, medical submissions, and profile access
- Technical officer tools for attendance workflows, medical record management, CA workflows, and dashboard reporting
- Notice management and timetable viewing
- Course registration and academic record support

## Tech Stack

- `Java 17`
- `JavaFX 17`
- `Maven`
- `MySQL 8`
- `Jackson`
- `HikariCP`
- `JJWT`
- `ControlsFX`
- `Docker Compose` for database/backend container setup

## Project Structure

```text
JavaMiniProject/
├─ backend/                  # Socket server, commands, services, DAO layer
├─ frontend/                 # JavaFX desktop client
├─ database/                 # SQL dump / seed data
├─ documents/                # Project diagrams and supporting documents
├─ docker-compose.yaml       # MySQL + backend container setup
└─ README.md
```

## Architecture

### Frontend

- JavaFX application entry point: `com.example.frontend.Launcher`
- Loads FXML-based screens from `frontend/src/main/resources/view`
- Communicates with the backend using `ServerClient`
- Default client connection: `localhost:5000`

### Backend

- Backend entry point: `backend/src/main/java/Main.java`
- Starts a multi-threaded socket server on port `5000`
- Uses a command-based request handling approach
- Connects to MySQL through `HikariCP`

## Prerequisites

Before running the project, make sure you have:

- `JDK 17` or newer installed
- `Maven` available, or use the Maven wrapper in `frontend/`
- `MySQL 8` installed locally, or Docker Desktop for containerized setup

## Database Notes

The repository currently contains two different database configurations:

- `docker-compose.yaml` uses:
  - database: `lms_db`
  - port: `3307`
  - username: `root`
  - password: `root123`
- `backend/src/main/java/utility/DataSource.java` currently uses:
  - database: `lms_db`
  - port: `3306`
  - username: `root`
  - password: empty string

If you run the backend locally, you should update `DataSource.java` or your MySQL instance so these values match.

## Running the Project

### Option 1: Run with local MySQL

1. Create a MySQL database named `lms_db`.
2. Import `database/lms_db_dump.sql`.
3. Make sure `backend/src/main/java/utility/DataSource.java` points to the correct MySQL host, port, username, and password.
4. Start the backend server:

```powershell
cd backend
mvn compile exec:java -Dexec.mainClass=Main
```

5. Start the frontend:

```powershell
cd frontend
.\mvnw.cmd javafx:run
```

### Option 2: Run database and backend with Docker

1. Start the services:

```powershell
docker-compose up --build
```

2. Start the frontend separately:

```powershell
cd frontend
.\mvnw.cmd javafx:run
```

3. If the frontend cannot connect, confirm the backend is available on port `5000` and align the backend database settings if needed.

## Main Screens and Modules

### Admin

- User management
- Course management
- Lecturer assignment
- Notice management
- Timetable management
- Registration period setup

### Lecturer

- Dashboard and assigned courses
- Continuous assessment management
- Final marks upload
- Attendance and eligibility views
- Course materials
- Profile management
- Session creation

### Student

- Dashboard
- Course registration
- Attendance
- Grades
- Eligibility
- Medical submission and record viewing
- Profile management

### Technical Officer

- Attendance management
- Medical management
- CA support workflows
- Profile management

## Default Communication Ports

- Backend socket server: `5000`
- Docker MySQL host port: `3307`
- Backend container port mapping: `8080:8080` in compose, though the current Java backend entry point shown in the repository is socket-based

## Known Notes

- The backend server in the current codebase is socket-based, not a standard REST API.
- Some environment values in Docker and local Java configuration may need alignment before first run.
- The repository includes both source resources and generated Maven target output; always run the latest built frontend when checking UI changes.

## Development Tips

- Frontend FXML views are under `frontend/src/main/resources/view`
- Frontend controllers are under `frontend/src/main/java/com/example/frontend/controller`
- Backend command registration is under `backend/src/main/java/command/repository`
- Database access classes are under `backend/src/main/java/dao`

## Future Improvements

- Centralize configuration using environment variables or `.properties`
- Align Docker and local database configuration
- Add automated tests
- Add API documentation / protocol documentation for frontend-backend messages
- Add CI checks for both frontend and backend builds

## License

No license file is currently included in this repository. Add one if you plan to distribute or open-source the project.
