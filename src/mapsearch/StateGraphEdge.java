package mapsearch;

public class StateGraphEdge{

    StateGraphEdge(long id1, long id2, boolean isOneWay,
		   double distance, int roadType){
	this.id1 = id1;
	this.id2 = id2;
	this.isOneWay = isOneWay;
	this.distance = distance;
	this.roadType = roadType;
    }

    // the ids of the start (id1) and end (id2) state nodes
    public final long id1;
    public final long id2;

    public final boolean isOneWay;
    public final double distance; // in km
    public final int roadType; // one of the static constants in RoadNetwork
};