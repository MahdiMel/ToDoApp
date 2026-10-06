# Pomodoro Eisenhower Matrix 🍅🔳

**🏆 Project 4/7 of a 7-Day Coding Challenge**

Day 4/7 of the 7 days, 7 projects challenge.

A lightweight Java Swing desktop application that combines the focus of a Pomodoro timer with the strategic prioritization of the Eisenhower Matrix. , features a cloud-synced backend using Supabase (PostgreSQL) to store and manage tasks across sessions.

## Features
* **Eisenhower Matrix UI:** Automatically routes tasks into four distinct colored quadrants (Do First, Schedule, Delegate, Eliminate).
* **Pomodoro Timer:** Integrated 25-minute countdown timer to keep focus sessions on track.
* **Cloud Sync:** Tasks are saved and fetched in real-time using a Supabase PostgreSQL database.
* **Frictionless Completion:** Double-click any task in the UI to mark it as completed and instantly delete it from the cloud.
* **Multi-User Support:** Prompts for a unique user identifier on launch to load isolated task lists.

## Tech Stack
* **Language:** Java (Swing UI)
* **Build Tool:** Maven
* **Database:** PostgreSQL (Supabase) via JDBC
* **Environment Management:** `dotenv-java` for secure credential handling

## Prerequisites
* Java JDK 17 or higher
* Maven installed
* A [Supabase](https://supabase.com/) account and project

## Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/Mahdi-Mel/ToDoApp.git](https://github.com/Mahdi-Mel/ToDoApp.git)
   cd ToDoApp
2. **Database Setup:**
    Run the following SQL script in your Supabase SQL Editor to create the necessary schema and table:
    ```sql
    CREATE SCHEMA IF NOT EXISTS pomodoro;

    CREATE TABLE IF NOT EXISTS pomodoro.tasks (
        id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
        user_identifier VARCHAR(255) NOT NULL,
        task_name TEXT NOT NULL,
        category VARCHAR(50) NOT NULL,
        created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc', NOW())
    );

    CREATE INDEX idx_user_identifier ON pomodoro.tasks(user_identifier);
    ```

3. **Environment Variables:**
    Create a `.env` file in the root directory of the project and add your Supabase credentials:
    ```env
    # Ensure you remove the ?user=... query parameters from the URL
    SUPABASE_URL=jdbc:postgresql://[your-pooler-url.supabase.com:5432/postgres](https://your-pooler-url.supabase.com:5432/postgres)
    SUPABASE_USER=postgres.your_project_ref
    SUPABASE_PASSWORD=your_database_password
    ```

4. **Build and Run:**
    ```bash
    mvn clean install
    mvn exec:java -Dexec.mainClass="PomodoroApp"
    ```
    *(Alternatively, open the project in IntelliJ IDEA, reload the Maven dependencies, and run `PomodoroApp.main()`)*
