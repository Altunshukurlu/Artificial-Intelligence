package mapsearch;

public class StateNode {
	public StateNode(long nodeID, double latitude, double longitude) {
		id = nodeID;
		lat = latitude;
		lon = longitude;

		isClosed = false;
		bestCostSoFar = Double.POSITIVE_INFINITY;
	}

	public final long id;
	public final double lat;
	public final double lon;

	public boolean isClosed;
	public double bestCostSoFar; // is Double.POSITIVE_INFINITY if not in the
									// queue
};