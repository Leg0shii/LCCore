# LCCore

## Table of Contents
- [Setup](#setup)
    - [Cloning the Repository](#cloning-the-repository)
    - [Project Configuration](#project-configuration)
    - [Database Setup](#database-setup)
        - [Option 1: Docker](#option-1-docker)
        - [Option 2: Local MySQL Server](#option-2-local-mysql-server)
- [Running the Project](#running-the-project)

## Setup

### Cloning the Repository

1. Clone the LCCore repository:
   ```
   git clone https://github.com/Leg0shii/LCCore.git
   ```

2. Open the project in IntelliJ

### Project Configuration

3. Run the 'Init Project' run configuration in IntelliJ

### Database Setup

Choose one of the following options to set up your database:

#### Option 1: Docker

1. Install Docker (Note: You may need to enable certain Windows features if you haven't used Docker before)
2. Start the Docker application
3. In the project directory, run:
   ```
   docker-compose up
   ```

#### Option 2: Local MySQL Server

1. Download and install MySQL Server 8.1
2. Create a new database called `serverpro_db`
3. Configure MySQL to use native password authentication by running the following command:
   ```sql
   ALTER USER 'YOURDBUSER'@'%' IDENTIFIED WITH mysql_native_password BY 'YOURDBPASSWORD';
   ```
   Replace `'YOURDBUSER'` and `'YOURDBPASSWORD'` with your actual MySQL username and password
4. Update the database credentials in `./server/plugins/LCCore/database.yml` using the information from your MySQL setup

## Running the Project

5. In IntelliJ, run the 'Linkcraft Test' run configuration to start the project