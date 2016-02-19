package mapsearch;

import java.util.*;
import java.io.*;
import java.lang.management.*;

public class RoadNetwork {
	public static final int ROADTYPE_MOTORWAY = 70;
	public static final int ROADTYPE_MOTORWAYLINK = 71;
	public static final int ROADTYPE_TRUNK = 60;
	public static final int ROADTYPE_TRUNKLINK = 61;
	public static final int ROADTYPE_PRIMARY = 50;
	public static final int ROADTYPE_PRIMARYLINK = 51;
	public static final int ROADTYPE_SECONDARY = 40;
	public static final int ROADTYPE_SECONDARYLINK = 41;
	public static final int ROADTYPE_TERTIARY = 30;
	public static final int ROADTYPE_RESIDENTIAL = 20;
	public static final int ROADTYPE_OTHER = 10;

	public RoadNetwork(String nodesFilename, String linksFilename)
			throws IOException {

		theNodes = new HashMap<Long, StateNode>();
		adjList = new HashMap<Long, HashSet<StateGraphEdge>>();

		String inputLine;

		BufferedReader nodesFile = new BufferedReader(new FileReader(
				nodesFilename));

		while ((inputLine = nodesFile.readLine()) != null) {
			double lat, lon;
			long id;

			StringTokenizer parser = new StringTokenizer(inputLine);

			id = Long.parseLong(parser.nextToken());
			lat = Double.parseDouble(parser.nextToken());
			lon = Double.parseDouble(parser.nextToken());

			StateNode newNode = new StateNode(id, lat, lon);

			theNodes.put(id, newNode);
		}

		nodesFile.close();
		nodesFile = null;

		BufferedReader edgesFile = new BufferedReader(new FileReader(
				linksFilename));

		while ((inputLine = edgesFile.readLine()) != null) {
			long id1, id2;
			boolean isOneWay;
			double distance;
			int roadTypeInt;
			String roadTypeStr;

			StringTokenizer parser = new StringTokenizer(inputLine);

			id1 = Long.parseLong(parser.nextToken());
			id2 = Long.parseLong(parser.nextToken());
			isOneWay = (Integer.parseInt(parser.nextToken()) != 0);
			distance = Double.parseDouble(parser.nextToken());
			roadTypeStr = parser.nextToken();

			if (roadTypeStr.equals("motorway")) {
				roadTypeInt = ROADTYPE_MOTORWAY;
			} else if (roadTypeStr.equals("motorway_link")) {
				roadTypeInt = ROADTYPE_MOTORWAYLINK;
			} else if (roadTypeStr.equals("trunk")) {
				roadTypeInt = ROADTYPE_TRUNK;
			} else if (roadTypeStr.equals("trunk_link")) {
				roadTypeInt = ROADTYPE_TRUNKLINK;
			} else if (roadTypeStr.equals("primary")) {
				roadTypeInt = ROADTYPE_PRIMARY;
			} else if (roadTypeStr.equals("primary_link")) {
				roadTypeInt = ROADTYPE_PRIMARYLINK;
			} else if (roadTypeStr.equals("secondary")) {
				roadTypeInt = ROADTYPE_SECONDARY;
			} else if (roadTypeStr.equals("secondary_link")) {
				roadTypeInt = ROADTYPE_SECONDARYLINK;
			} else if (roadTypeStr.equals("tertiary")) {
				roadTypeInt = ROADTYPE_TERTIARY;
			} else if (roadTypeStr.equals("residential")) {
				roadTypeInt = ROADTYPE_RESIDENTIAL;
			} else {
				roadTypeInt = ROADTYPE_OTHER;
			}

			StateGraphEdge newEdge = new StateGraphEdge(id1, id2, isOneWay,
					distance, roadTypeInt);
			HashSet<StateGraphEdge> edgeSet;

			// from id1 to id2
			edgeSet = adjList.get(id1);
			if (edgeSet == null) {
				edgeSet = new HashSet<StateGraphEdge>();
				adjList.put(id1, edgeSet);
			}
			edgeSet.add(newEdge);

			// from id2 to id1
			if (!isOneWay) {
				edgeSet = adjList.get(id2);
				if (edgeSet == null) {
					edgeSet = new HashSet<StateGraphEdge>();
					adjList.put(id2, edgeSet);
				}
				edgeSet.add(new StateGraphEdge(id2, id1, isOneWay, distance,
						roadTypeInt));
			}
		}

		edgesFile.close();
	}

	// Returns the StateNode object for the given id
	public final StateNode getNode(long id) {
		return theNodes.get(id);
	}

	// Returns a set of edges that come from node "id"
	// The set should not be modified.
	public final Set<StateGraphEdge> getOutgoingEdges(long id) {
		return adjList.get(id);
	}

	public final Collection<StateNode> getNodes() {
		return theNodes.values();
	}

	private HashMap<Long, StateNode> theNodes;

	// the adjacency list for the graph
	// maps node id to the outgoing edges from that node
	private HashMap<Long, HashSet<StateGraphEdge>> adjList;
}
