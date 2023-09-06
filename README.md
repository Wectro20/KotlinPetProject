# Cryptocurrency Price Monitoring System

<a href="https://github.com/Wectro20/KotlinPetProject/actions/workflows/gradle.yml">
<img src="https://github.com/Wectro20/KotlinPetProject/actions/workflows/gradle.yml/badge.svg" alt="build"> 
<br>
</a>
The Cryptocurrency Price Monitoring System is a Spring Boot application that fetches real-time prices of cryptocurrencies and stores them in a MongoDB database. It includes components for data retrieval, processing, and persistence. This README provides an overview of the project, its setup, usage instructions, and how to deploy it using Docker.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
    - [Configuration](#configuration)
    - [Building and Running via Bash Script](#building-and-running-via-bash-script)
- [API Endpoints](#api-endpoints)
- [Docker Deployment](#docker-deployment)

## Features

- Fetches real-time cryptocurrency prices from an external API.
- Stores cryptocurrency prices in a MongoDB database.
- Provides RESTful API endpoints for querying and exporting data.
- Supports pagination and querying by cryptocurrency name.
- Offers a background job for continuous price updates.

## Prerequisites

Before you begin, ensure you have the following prerequisites:

- Java Development Kit (JDK) 17
- Gradle (for building the application)
- Docker and Docker Compose (for containerized deployment)
- MongoDB (can be set up locally or using Docker)

## Getting Started

Follow these steps to set up and run the Cryptocurrency Price Monitoring System:

### Configuration

1. Clone this repository to your local machine.

2. Set up a MongoDB instance and configure the connection details in `src/main/resources/application.properties`.

### Building and Running via Bash Script

To simplify the process of building and running the Cryptocurrency Price Monitoring System, you can use the provided Bash script. The script automates the following steps:

1. Building the application using Gradle.
2. Creating a Docker image for the application.
3. Starting the application and its services using Docker Compose.

#### Using the Bash Script

1. Open a terminal window.

2. Navigate to the project directory.

3. Make the script executable by running:

   ```bash
   chmod +x start-app.sh
   ```
   ```bash
   ./start-app.sh
   ```
The script will build the application, create a Docker image, and start the services. You can access the application's RESTful API endpoints at [http://localhost:8080](http://localhost:8080).

## API Endpoints

The Cryptocurrency Price Monitoring System provides the following API endpoints:

- **GET /findall**: Get a list of all stored cryptocurrency prices.
- **GET /cryptocurrencies/minprice**: Get the cryptocurrency with the minimum price.
- **GET /cryptocurrencies/maxprice**: Get the cryptocurrency with the maximum price.
- **GET /cryptocurrencies**: Get a paginated list of cryptocurrency prices. Supports querying by name, pagination, and size.
- **GET /cryptocurrencies/csv**: Download a CSV file containing cryptocurrency data.


## Docker Deployment

To deploy the Cryptocurrency Price Monitoring System using Docker, follow these steps:

1. Make sure Docker and Docker Compose are installed.

2. Modify the `docker-compose.yml` file with the necessary configuration.

3. Run the following commands:

 ```bash
 ./gradlew build
 docker build -t cryptocurrency-app .
 docker-compose up
```

Docker Compose will start the application and its associated services. You can access the application's RESTful API endpoints at [http://localhost:8080](http://localhost:8080).






