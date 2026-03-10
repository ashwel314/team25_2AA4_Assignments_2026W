# Catan Simulation Engine
**team25_2AA4_Assignments-2026W** </br>
**Course:** SFWRENG 2AA4  </br>
**Project:** Assignment 1 - System Architecture & Implementation </br>

## Project Overview
This project is a discrete-event simulator for the board game **Settlers of Catan**. The system models a 19-tile hexagonal grid, four autonomous agents, and a resource distribution engine. The simulation runs for a maximum of 8,192 rounds or until an agent achieves 10 victory points.

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

### Resource Production & Distribution
The simulation uses a "Tile-to-Node" navigation strategy:
1. The `Dice` roll a value between 2 and 12.
2. `GamePlay` iterates through the `Board` to find all `Tiles` matching that roll value.
3. Each active `Tile` queries its `AdjacentNodes()` to find surrounding intersections.
4. The `Board` checks if those nodes contain a `Building`. If so, it identifies the `Agent` (owner) and distributes the appropriate resource type.

---

## How to Run
1. Ensure all `.java` files are located within the `Assignment1` package directory.
2. Compile the project:
   ```bash
   javac Assignment1/*.java
   
[![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=sharmaankita3387_team25_2AA4_Assignments_2026W) 
