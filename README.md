# egtmanager_service
Erivan Gemini Task manager backend service api

## Running the Application

This README provides instructions on building and running the Spring Boot application using Docker and Docker Compose.

**Prerequisites:**

* **Java 21:** Ensure you have Java 21 installed on your system.
* **Docker:** Install Docker Desktop or Docker Engine on your machine.
* **Docker Compose:** Install Docker Compose.
* **MongoDB:** The docker-compose.yml file will start a MongoDB container and attach to the application.

**Building the Application:**

1. **Clone the repository:**
   ```bash
   git clone https://github.com/erivansousa/egtmanager_service.git

2. **Navigate to the project directory:**
   ```bash
   cd <project_directory>

3. **Build the Docker image:**
    ```bash
    docker build -t <image_name> .

Replace <image_name> with your desired image name.

**Running the Application with Docker Compose:**

1. **Start the application:**
    ```bash
    docker-compose up -d

This will build the image (if not already built) and start the containers in detached mode. This includes both your Spring Boot application and the MongoDB container.

2. **Access the application:**

The application will be accessible at http://localhost:8080

**Stopping the Application:**

1. **Stop the containers:**
    ```bash
    docker-compose down

**Additional Notes:**

The docker-compose.yml file defines the services and their dependencies. You can modify it to adjust the configuration, such as port mapping or environment variables.
The Dockerfile defines the steps to build the Docker image. You can customize it to include additional dependencies or configurations.

### Troubleshooting:

If you encounter any issues, check the Docker logs for error messages.
Ensure that the ports specified in the docker-compose.yml file are not already in use.
Verify that the application is running correctly by checking the logs or accessing the application through the browser.
If you're using a local MongoDB installation, make sure it's running and accessible.
