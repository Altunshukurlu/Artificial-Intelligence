package scraps;

import java.io.*;
import java.util.*;

import mapsearch.*;

public class Searcher {

	public static void main(String[] args) throws IOException {

		if (args.length < 6) {
			System.err
					.println("USAGE: java Searcher nodesFile linksFile startNodeID endNodeID resultFile useGraphSearch(0 or 1)");
			return;
		}

		// create the RoadNetwork
		RoadNetwork theRoads = null;
		try {
			theRoads = new RoadNetwork(args[0], args[1]);
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}

		// set up the means to write the OSM file
		PrintWriter osmOut = null;
		try {
			final PipedWriter osmOutPipe = new PipedWriter();
			final PipedReader in = new PipedReader(osmOutPipe);

			Runnable runOSM = new Runnable() {
				public void run() {
					CreateOSM cosm = new CreateOSM();
					cosm.writeOSMFile("results.osm", new BufferedReader(in), 20);
				}
			};

			Thread osmWritingThread = new Thread(runOSM, "OSM Writing Thread");
			osmWritingThread.start();

			osmOut = new PrintWriter(new BufferedWriter(osmOutPipe));
		} catch (IOException x) {
			x.printStackTrace();
		}

		final boolean useGraphSearch = (Integer.parseInt(args[5]) != 0);
		// TODO: actually do the search
		// Initial setup
		long enqueued = 0;
		long dequeued = 0;
		StateNode startNode = theRoads.getNode(Long.parseLong(args[2]));
		StateNode endNode = theRoads.getNode(Long.parseLong(args[3]));
		if (startNode == null || endNode == null) {
			System.out.println("Usage error!");
			System.exit(0);
		}
		Queue<SearchNode> openList = new PriorityQueue<SearchNode>(1);
		SearchNode start = new SearchNode(null, startNode, 0.0,
				EuclideanHeuristic.eval(startNode, endNode));
		SearchNode currentSearchNode;
		openList.add(start);
		start.to.bestCostSoFar = 0;
		enqueued++;
		currentSearchNode = null;
		boolean solutionFound;

		if (!useGraphSearch) {
			solutionFound = false;
			while (!openList.isEmpty()) {
				currentSearchNode = openList.poll(); // select next search node
				osmOut.println(currentSearchNode.to.id + " "
						+ currentSearchNode.to.lat + " "
						+ currentSearchNode.to.lon);
				dequeued++;
				if (currentSearchNode.to.id == endNode.id) {// check for
					solutionFound = true; // solution
					break;
				}
				// get neighbors of current node to add to the list
				Set<StateGraphEdge> edges = theRoads
						.getOutgoingEdges(currentSearchNode.to.id);
				for (StateGraphEdge edge : edges) {
					StateNode neighbor = theRoads.getNode(edge.id2);
					// if (!currentSearchNode.doesHaveFromValue() || neighbor.id
					// != currentSearchNode.from.to.id) {
					enqueued++;
					osmOut.println(edge.id1 + " " + edge.id2);
					openList.add(new SearchNode(currentSearchNode, neighbor,
							currentSearchNode.getG() + edge.distance,
							EuclideanHeuristic.eval(neighbor, endNode)));

					// }
				}
				currentSearchNode = null;

			}
		} else {
			Queue<SearchNode> closedList = new PriorityQueue<SearchNode>(1);

			System.out.println("Doing Graph Search");
			solutionFound = false;

			while (!openList.isEmpty() && !solutionFound ) {

				currentSearchNode = openList.poll(); // select next search node
				osmOut.println(currentSearchNode.to.id + " "
						+ currentSearchNode.to.lat + " "
						+ currentSearchNode.to.lon);
				dequeued++;
				closedList.add(currentSearchNode);
				if (currentSearchNode.to.id == endNode.id) {// check for
					solutionFound = true; // solution
					break;
				}
				// get neighbors of current node to add to the list
				Set<StateGraphEdge> edges = theRoads
						.getOutgoingEdges(currentSearchNode.to.id);
				for (StateGraphEdge edge : edges) {
					StateNode neighbor = theRoads.getNode(edge.id2);
					
					SearchNode neighborSearchNode = new SearchNode(
							currentSearchNode, neighbor,
							currentSearchNode.getG() + edge.distance,
							EuclideanHeuristic.eval(neighbor, endNode));
					if(closedList.contains(neighborSearchNode)){
						continue; 
					}
				//	if(neighbor.id == endNode.id){
					//	currentSearchNode = new SearchNode(
						//		currentSearchNode, endNode,
							//	currentSearchNode.getG() + edge.distance,
								//EuclideanHeuristic.eval(neighbor, endNode));; 
					//	solutionFound = true; 
					//	break; 
					//}
					if (openList.contains(neighborSearchNode)) {
						if (neighborSearchNode.getG() < neighborSearchNode.to.bestCostSoFar) {
							openList.remove(neighborSearchNode);
							openList.add(neighborSearchNode);
							neighborSearchNode.to.bestCostSoFar = neighborSearchNode
									.getG();
			
						}
					} else {
						enqueued++;
						openList.add(neighborSearchNode);
						neighborSearchNode.to.bestCostSoFar = neighborSearchNode
								.getG();
					}
					osmOut.println(edge.id1 + " " + edge.id2);

				}
				//if(!solutionFound)
				currentSearchNode = null;

			}
		}
		// TODO: During the search, write to osmOut:
		// for each node searched: id latitude longitude
		// osmOut.println(newSearchNode.state.id + " " + newSearchNode.state.lat
		// + " " + newSearchNode.state.lon);
		//
		// for each edge searched: startID endID
		// osmOut.println(e.id1 + " " + e.id2);

		osmOut.close();

		BufferedWriter output = new BufferedWriter(new FileWriter(args[4]));
		output.write("Number of nodes enqueued: " + enqueued + "\n");
		output.write("Number of nodes dequeued: " + dequeued + "\n");
		output.write("Was solution found? " + (solutionFound ? "yes" : "no")
				+ "\n");

		System.out.print("Number of nodes enqueued: " + enqueued + "\n");
		System.out.print("Number of nodes dequeued: " + dequeued + "\n");
		System.out.print("Was solution found? "
				+ (solutionFound ? "yes" : "no") + "\n");

		if (solutionFound) {
			Stack<StateNode> path = new Stack<StateNode>();
			SearchNode holder = currentSearchNode;
			// add all to stack, this will correct order to start to goal
			while (holder.doesHaveFromValue()) {
				path.add(holder.to);
				holder = holder.from;
			}// last ad parent
			path.add(holder.to);

			output.write("Solution distance: " + currentSearchNode.getG()
					+ "\n");
			output.write("Number of steps in solution: " + (path.size() - 1)
					+ "\n");
			System.out.print("Solution distance: " + currentSearchNode.getG()
					+ "\n");
			System.out.print("Number of steps in solution: "
					+ (path.size() - 1) + "\n");
			while (!path.isEmpty()) {
				long id = path.pop().id;
				output.write(id + "\n");
				System.out.print(id + "\n");
			}
		}
		output.flush();
		output.close();

		/*
		 * TODO: write the output file (which is in addition to the OSM file)
		 * 
		 * Here is an example:
		 * 
		 * try{ BufferedWriter output = new BufferedWriter(new
		 * FileWriter(args[4])); output.write("Number of nodes enqueued: " +
		 * result.numNodesEnqueued + "\n");
		 * output.write("Number of nodes dequeued: " + result.numNodesDequeued +
		 * "\n"); output.write("Was solution found? " + (result.solutionWasFound
		 * ? "yes" : "no") + "\n"); if(result.solutionWasFound){
		 * output.write("Solution distance: " + result.solutionDistance + "\n");
		 * output.write("Number of steps in solution: " +
		 * (result.solutionPath.size()-1) + "\n"); for(int i=0;
		 * i<result.solutionPath.size(); i++){
		 * output.write(result.solutionPath.get(i).toString() + "\n"); } }
		 * output.flush(); output.close(); } catch(IOException ioe){
		 * System.err.println(ioe.getMessage()); }
		 */

	}
}