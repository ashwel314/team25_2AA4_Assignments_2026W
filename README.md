# Catan Simulation Engine: Phase 2
**team25_2AA4_Assignments-2026W** </br>
**Course:** SFWRENG 2AA4  </br>
**Project:** Assignment 2 - human-in-the-Loop & Design Evolution </br>

## Project Overview
This project is a discrete-event simulator for the board game **Settlers of Catan**. The system models a 19-tile hexagonal grid, four autonomous agents, and a resource distribution engine. The simulation runs for a maximum of 8,192 rounds or until an agent achieves 10 victory points.

In Phase 2, this project evolves the Assignment 1 discrete-event simulator into an interactive game engine. It introduces a Human Player interface, a Step-Forward mechanism for synchronized visualization, and a refactored Object-Oriented Architecture to support polymorphic agent behavior.


## Game Rules Implementation
The simulation strictly adheres to the core Catan mechanics as specified in the project requirements:

### 1. Resource Production
* **Dice Rolls**: Each turn begins with a dice roll (2-12). If a 7 is rolled, no resources are produced.
* **Yield**: Tiles with the matching roll value produce one resource for a Settlement and two for a City.
* **The Desert**: The "Desert" tile (Roll Value 0) produces no resources.

### 2. Building & Development
* **Settlement Cost**: 1 Brick, 1 Lumber, 1 Wool, 1 Wheat.
* **City Upgrade**: 3 Ore, 2 Wheat. A City must replace an existing Settlement.
* **The Distance Rule**: A Settlement can only be placed if all adjacent intersections (Nodes) are currently unoccupied.

### 3. The "Rule of Seven"
* When a 7 is rolled, any Agent with more than 7 resource cards in their hand must discard half of them (rounded down).
* This prevents agents from hoarding resources indefinitely and forces strategic building.

### 4. Victory Conditions 
* **Settlements**: Worth 1 Victory Point.
* **Cities**: Worth 2 Victory Points.
* **Winning**: The simulation terminates immediately when any agent reaches 10 Victory Points or the 8,192 round limit is hit.

## Assignment 2 Evolutions
The system has been extended to meet the following new requirements:

### 1. Human-in-the-Loop
* **Regex Command Parser**: A dedicated parser handles manual input for the human player (Agent 0).
* **Supported Commands**: Commands include `roll`, `build settlement [id]`, `build road [id]`, `list`, and `go`.
* **Input Validation**: The parser performs both syntactic (regex matching) and semantic (game rule validation) checks.

### 2. Design Refactoring 
* **Polymorphism**: The `Agent` class is now an **Abstract Base Class**, replacing the previous boolean-flag-based design.
* **Subclass Specialization**: `HumanAgent` and `ComputerAgent` extend the base class to provide distinct turn-taking logic while sharing resource management code.
* **Open-Closed Principle**: The new architecture allows for adding new AI types or player types without modifying the core `Game` loop.

### 3. Step-Forward & Visualization 
* **Execution Control**: The simulation includes a "Step-Forward" mechanism where the game pauses until the user inputs `go`.
* **State Export**: After every action, the system exports a `game_state.json` file to sync with the external Python visualizer.
---

## How to Run the Simulator

### 1. Clone the Repository
git clone https://github.com/sharmaankita3387/team25_2AA4_Assignments_2026W.git

### 2. Open the Project
Open the project using an IDE such as IntelliJ IDEA or Eclipse.

### 3. Run the Program
Locate the directory:
Assignment3/src

Open Demonstrator.java.
Click Run Demonstrator.main().

### 4. Simulator Execution
Running the Demonstrator class will start the simulator and display the game in the terminal.
You can interact with the game through command line prompts.

[![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=sharmaankita3387_team25_2AA4_Assignments_2026W) 
