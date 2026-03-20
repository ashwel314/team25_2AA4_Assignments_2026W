# Catan Simulation Engine: Phase 3
**team25_2AA4_Assignments-2026W** 
**Course:** SFWRENG 2AA4 
**Project:** Assignment 3 - Design Patterns & Machine Intelligence

## Project Overview
This project is a discrete-event simulator for the board game Settlers of Catan. The system models a 19-tile hexagonal grid, four autonomous agents, and a resource distribution engine. The simulation runs for a maximum of 8,192 rounds or until an agent achieves 10 victory points.

In Phase 3, this project evolves the Phase 2 interactive game engine by introducing three design patterns that address structural weaknesses in the codebase, implementing a rule-based machine intelligence for the computer agent, and adding full undo/redo functionality for the human player. The focus of this phase is on producing well-structured, extensible software that adheres to SOLID and object-oriented principles.


## Game Rules Implementation
The simulation strictly adheres to the core Catan mechanics as specified in the project requirements:

### 1. Resource Production
- **Dice Rolls:** Each turn begins with a dice roll (2-12). If a 7 is rolled, no resources are produced.
- **Yield:** Tiles with the matching roll value produce one resource for a Settlement and two for a City.
- **The Desert:** The "Desert" tile (Roll Value 0) produces no resources.

### 2. Building & Development
- **Settlement Cost:** 1 Brick, 1 Lumber, 1 Wool, 1 Wheat.
- **City Upgrade:** 3 Ore, 2 Wheat. A City must replace an existing Settlement.
- **The Distance Rule:** A Settlement can only be placed if all adjacent intersections (Nodes) are currently unoccupied.

### 3. The "Rule of Seven"
- When a 7 is rolled, any Agent with more than 7 resource cards in their hand must discard half of them (rounded down).
- This prevents agents from hoarding resources indefinitely and forces strategic building.

### 4. Victory Conditions
- **Settlements:** Worth 1 Victory Point.
- **Cities:** Worth 2 Victory Points.
- **Winning:** The simulation terminates immediately when any agent reaches 10 Victory Points or the 8,192 round limit is hit.


## Phase 3 Evolutions
The system has been extended to meet the following new requirements:

### 1. Undo/Redo Functionality (R3.1) — Command Pattern
- **Encapsulation:** Every structural game action is encapsulated as a Command object with `execute()` and `undo()` methods.
- **CommandManager:** Maintains an execution history stack and a redo stack, allowing the human player to reverse or reapply any action taken during their turn.
- **Integrity:** `undo()` nullifies the map placement, refunds resources, and restores piece counts and victory points exactly.
- **Supported Commands:** `undo` reverses the last action taken this turn. `redo` reapplies the last undone action.

### 2. Rule-Based Machine Intelligence (R3.2 & R3.3) — Command Pattern Extended
- **Value Scoring:** The same Command infrastructure from Task 1 is reused to drive the computer agent's decision-making. Each action exposes `getValue()` for scoring, `execute()` for performing the action, and `getDescription()` for logging.

- **Constraint Resolution (R3.3):** Evaluated before value-based selection. If the agent holds more than 7 cards it is forced to spend them immediately. If two of the agent's road segments are separated by a single unoccupied edge, a connecting road is built. If any opponent's longest road is within one segment of the agent's, a defensive road is built.
- **Selection:** The `selectBest()` method scores all available commands uniformly and selects the highest, breaking ties randomly as required.

### 3. Polymorphic Agent Architecture — Template Method Pattern
- **Problem Solved:** `Game.runRound()` previously used `isComputer()` checks and illegal downcasts to `((HumanAgent) agent)`, violating both the Liskov Substitution Principle and the Open/Closed Principle.
- **Fix:** `Agent` now declares `takeTurn(GameMap, int, int)` as abstract. `ComputerAgent` implements it with constraint and value logic. `HumanAgent` implements it with its CLI loop.
- **Result:** `Game.runRound()` calls `agent.takeTurn(map, round, roll)` uniformly on every agent with no branching, no casting, and no type checking. Adding a new agent type requires only implementing the abstract methods — `Game` never needs to change.

### 4. Output Decoupling — Observer Pattern (Design Proposal)
- **Problem Solved:** `Game.java` previously handled console logging, JSON export, and victory point printing directly inside `runRound()`, violating the Single Responsibility Principle and the Open/Closed Principle.
- **Fix:** `Game` becomes the Subject, calling `notifyObservers()` on meaningful events. Three concrete observers handle output independently: `ConsoleLogger`, `JsonStateWriter`, and `VisualizerNotifier`.
- **Result:** All observers are registered once at startup in `Demonstrator.java`. `Game` never knows which outputs exist and never needs to change when a new output is added.


## How to Run the Simulator

### 1. Clone the Repository
```
git clone https://github.com/sharmaankita3387/team25_2AA4_Assignments_2026W.git
```

### 2. Open the Project
Open the project using an IDE such as IntelliJ IDEA or Eclipse.

### 3. Run the Program
Locate the directory: `Assignment3/src`

Open `Demonstrator.java` and click **Run Demonstrator.main()**.

### 4. Simulator Execution
Running the Demonstrator class will start the simulator and display the game in the terminal. You can interact with the game through command line prompts.

[![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=sharmaankita3387_team25_2AA4_Assignments_2026W) 
