# PETCARE - Local Java Web App

This workspace contains a small Java servlet-based web application (frontend HTML/JS + backend servlets + JDBC) intended to run on Apache Tomcat.

Quick setup
1. Install MySQL and create database `petcare`.
2. Create tables (run SQL below).
3. Place MySQL Connector/J jar into `WEB-INF/lib` or compile with the jar on the classpath.
4. Deploy the project to Tomcat's `webapps/PETCARE` (copy files and `WEB-INF` folder) or build a WAR and drop it into `webapps`.

Database schema
```sql
CREATE DATABASE IF NOT EXISTS petcare;
USE petcare;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  email VARCHAR(255),
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) DEFAULT 'user'
);

CREATE TABLE booking (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  email VARCHAR(255),
  number VARCHAR(50),
  pet_type VARCHAR(100),
  service_id BIGINT DEFAULT 0
);
```

Files added/changed
- `com/petcare/util/DatabaseConnection.java` - JDBC helper. Edit URL/USER/PASSWORD as needed.
- `com/petcare/util/PasswordUtil.java` - simple SHA-256 hashing helper (replace with BCrypt for production).
- `com/petcare/model/*.java` - Booking, User, DAOs.
- `com/petcare/servlet/*.java` - UserServlet (signin/signup) and BookingServlet.
- `booking.js` - updated to POST form data to `/booking` endpoint.

Notes
- The servlets use `javax.servlet` imports. If you're using Tomcat 10+ (Jakarta namespace), change imports to `jakarta.servlet.*` or use Tomcat 9.
- `PasswordUtil` uses BCrypt and `DatabaseConnection` uses HikariCP for connection pooling.
- You can configure the database without editing source by setting environment variables:

  - PETCARE_DB_URL (default: jdbc:mysql://localhost:3306/petcare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC)
  - PETCARE_DB_USER (default: root)
  - PETCARE_DB_PASSWORD (default: empty)

  Example (macOS / zsh):

  ```bash
  export PETCARE_DB_URL="jdbc:mysql://localhost:3306/petcare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
  export PETCARE_DB_USER="root"
  export PETCARE_DB_PASSWORD="your_db_password"
  ```

- The project is already converted to a Maven layout and builds a WAR (see `pom.xml`).

How to build (simple)

1) This project now uses Maven. Build the WAR and deploy to Tomcat with the helper script.

2) Create the MySQL database and tables. You said you already have MySQL installed — run these commands in your MySQL shell (or via a client):

```sql
CREATE DATABASE IF NOT EXISTS petcare;
USE petcare;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  email VARCHAR(255),
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) DEFAULT 'user'
);

CREATE TABLE IF NOT EXISTS booking (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  email VARCHAR(255),
  number VARCHAR(50),
  pet_type VARCHAR(100),
  service_id BIGINT DEFAULT 0
);
```

3) Build and deploy with Maven (from project root):

```bash
# package and deploy to Tomcat (script will cp WAR into Tomcat webapps)
./build_and_deploy.sh
```

If you prefer to build with Maven directly and then copy the WAR manually:

```bash
mvn clean package
cp target/PETCARE-1.0.0.war /opt/homebrew/opt/tomcat/libexec/webapps/PETCARE.war
# then restart Tomcat
```

Notes about servlet API and Tomcat
- The servlets in this project use `javax.servlet.*` via the `javax.servlet-api` dependency (scope provided). Tomcat 9 is compatible. If you use Tomcat 10+ (Jakarta), replace imports with `jakarta.servlet.*` or run the Tomcat jakarta migration tool.


