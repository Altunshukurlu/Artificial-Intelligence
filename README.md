# CS380-Artificial-Intelligence

## Project Overview
This project implements an Artificial Intelligence-based map search system that can find optimal paths between locations using various search algorithms. The system processes OpenStreetMap (OSM) data to create a road network and provides pathfinding capabilities.

## Project Structure
- `src/mapsearch/`: Contains the core implementation files
  - `CreateOSM.java`: Handles OSM data processing and network creation
  - `RoadNetwork.java`: Implements the road network data structure
  - `StateNode.java` & `StateGraphEdge.java`: Define the graph structure for pathfinding
  - `EuclideanHeuristic.java`: Implements heuristic functions for informed search algorithms

- Data Files:
  - `nodesFile.txt`: Contains node data for the road network
  - `linksFile.txt`: Contains connection data between nodes
  - `results.osm`: Stores the processed OSM data
  - `resultFile.txt`: Contains search results and path information

## Features
- Road network construction from OSM data
- Pathfinding capabilities using AI search algorithms
- Euclidean distance-based heuristic for informed search
- Graph-based representation of the road network

## Usage
The project can be used to:
1. Process OpenStreetMap data into a usable road network
2. Find optimal paths between locations using various search algorithms
3. Visualize and analyze road networks and paths

## Dependencies
- Java Runtime Environment
- OpenStreetMap data processing libraries

## Note
This project was developed as part of CS380 - Artificial Intelligence course.