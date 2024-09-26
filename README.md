# LCCore

LCCore is a comprehensive Minecraft plugin designed to enhance server management and gameplay experience. It offers a wide range of features, including chat customization, party management, map checkpoints, staff moderation tools, and much more.

## Table of Contents

- [Features](#features)
- [Commands](#commands)
  - [1. Achievements](#1-achievements)
  - [2. Chat Commands](#2-chat-commands)
    - [Display Commands](#display-commands)
    - [Format Commands](#format-commands)
    - [Meme Commands](#meme-commands)
    - [Party Commands](#party-commands)
    - [Checkpoint Commands](#checkpoint-commands)
    - [Debug Commands](#debug-commands)
  - [3. Flow and Tab Completion](#3-flow-and-tab-completion)
  - [4. Hiding and Showing Elements](#4-hiding-and-showing-elements)
  - [5. Map Commands](#5-map-commands)
  - [6. Miscellaneous and Practice](#6-miscellaneous-and-practice)
  - [7. Save Management](#7-save-management)
  - [8. Settings and Shop](#8-settings-and-shop)
  - [9. Staff and Punishment](#9-staff-and-punishment)
  - [10. Tags](#10-tags)
  - [11. Additional Utilities](#11-additional-utilities)
- [Setup](#setup)
  - [Cloning the Repository](#cloning-the-repository)
  - [Project Configuration](#project-configuration)
  - [Database Setup](#database-setup)
    - [Option 1: Docker](#option-1-docker)
    - [Option 2: Local MySQL Server](#option-2-local-mysql-server)
  - [Running the Project](#running-the-project)

## Features

- **Achievements GUI**: Display and track player achievements through a user-friendly interface.
- **Chat Customization**: Personalize chat appearance with colors, formats, and fun commands.
- **Party System**: Create and manage parties with invite, kick, promote, and chat functionalities.
- **Map Management**: Handle map completions, checkpoints, reloads, and bonuses.
- **Practice Mode**: Toggle practice mode for players to hone their skills.
- **Staff Tools**: Access moderation commands like mute, teleport, and lockdown.
- **Tag Management**: Create, edit, equip, and manage user tags.
- **Utilities**: Commands for night vision, pings, spawns, statistics, warping, and world teleportation.

## Commands

### 1. Achievements

- **OpenAchievementGUI**: Opens the achievements graphical user interface.

### 2. Chat Commands

#### Display Commands

- **Chat Color and Star Settings**: Customize your chat appearance with colors and star icons.

#### Format Commands

- **Text Formatting**: Apply bold, italics, strikethrough, and underline to your chat messages.

#### Meme Commands

- **Fun Commands**: Enjoy meme-like commands such as `/eggs` and `/fail`.

#### Party Commands

- **Party Management**:
  - `/party create`: Create a new party.
  - `/party join <player>`: Join another player's party.
  - `/party leave`: Leave your current party.
  - `/party invite <player>`: Invite a player to your party.
  - `/party kick <player>`: Remove a player from your party.
  - `/party promote <player>`: Promote a party member to leader.
  - `/party chat <message>`: Send a message to your party members.

#### Checkpoint Commands

- **Map Checkpoints**:
  - `/checkpoint set`: Set a checkpoint on the map.
  - `/checkpoint delete`: Remove a checkpoint.
  - `/checkpoint change`: Modify an existing checkpoint.

#### Debug Commands

- **Testing and Debugging**: Access commands designed for testing plugin functionalities.

### 3. Flow and Tab Completion

- **Enhanced User Input**: Improved tab completion and reflection commands for a smoother experience.

### 4. Hiding and Showing Elements

- **Visibility Controls**:
  - `/hide`: Conceal specific elements from your view.
  - `/show`: Reveal hidden elements.

### 5. Map Commands

- **Map Management**:
  - `/map completions`: Check your map completion status.
  - `/map reload`: Reload map configurations.
  - `/map bonus`: Manage map bonuses.

### 6. Miscellaneous and Practice

- **Practice Mode**:
  - `/practice`: Enter practice mode.
  - `/unpractice`: Exit practice mode.

### 7. Save Management

- **Save Controls**:
  - `/checksaves`: Verify the status of your saves.
  - `/givesave <player>`: Grant a save to another player.
  - `/resetsaves`: Reset all your saves.

### 8. Settings and Shop

- **Personal Settings**:
  - `/settings`: Access and modify your personal settings.
  - `/star`: Adjust your star settings.
  - `/getchatcolor`: Retrieve or change your chat color.

### 9. Staff and Punishment

- **Moderation Tools**:
  - `/mute <player>`: Mute a player.
  - `/unmute <player>`: Unmute a player.
  - `/teleport <player>`: Teleport to a player.
  - `/lockdown`: Enable or disable server lockdown.

### 10. Tags

- **Tag Management**:
  - `/tag create <name>`: Create a new tag.
  - `/tag edit <name>`: Edit an existing tag.
  - `/tag equip <name>`: Equip a tag.
  - `/tag search <query>`: Search for tags.
  - `/tag remove <name>`: Remove a tag.

### 11. Additional Utilities

- **Useful Commands**:
  - `/nightvision`: Toggle night vision effect.
  - `/ping`: Check your latency to the server.
  - `/spawn`: Teleport to the server spawn point.
  - `/stats`: View your statistics.
  - `/warp <location>`: Warp to a predefined location.
  - `/worldtp <world>`: Teleport to a different world.

## Setup

### Cloning the Repository

Clone the LCCore repository:

```bash
git clone https://github.com/Leg0shii/LCCore.git
```

- Open the project in **IntelliJ IDEA**.

### Project Configuration

- Run the **`Init Project`** run configuration in IntelliJ to set up the project environment.

### Database Setup

Choose one of the following options to set up your database:

#### Option 1: Docker

1. **Install Docker**: [Download Docker](https://www.docker.com/get-started) for your operating system.
   - *Note*: You may need to enable certain Windows features if you haven't used Docker before.
2. **Start Docker**: Launch the Docker application.
3. **Run Docker Compose**:
   ```bash
   docker-compose up
   ```
   - This will set up the required MySQL database container using the provided `docker-compose.yml` file.

#### Option 2: Local MySQL Server

1. **Install MySQL Server 8.1**: [Download MySQL](https://dev.mysql.com/downloads/mysql/).
2. **Create Database**:
   - Log in to your MySQL server using the command line or a GUI tool like MySQL Workbench.
   - Create a new database called `serverpro_db`:
     ```sql
     CREATE DATABASE serverpro_db;
     ```
3. **Configure Authentication**:
   - Run the following command to use native password authentication:
     ```sql
     ALTER USER 'YOURDBUSER'@'%' IDENTIFIED WITH mysql_native_password BY 'YOURDBPASSWORD';
     ```
     - Replace `'YOURDBUSER'` and `'YOURDBPASSWORD'` with your actual MySQL username and password.
4. **Update Database Credentials**:
   - Modify the database configuration file at `./server/plugins/LCCore/database.yml`:
     ```yaml
     username: YOURDBUSER
     password: YOURDBPASSWORD
     database: serverpro_db
     host: localhost
     port: 3306
     ```
     - Ensure the credentials match your MySQL setup.

### Running the Project

- In IntelliJ IDEA, run the **`Linkcraft Test`** run configuration to start the project.
  - This will launch the Minecraft server with the LCCore plugin loaded.

---

**Note**: Ensure all dependencies and required plugins are properly installed and configured before running the project. Consult the plugin documentation or support channels if you encounter any issues.
