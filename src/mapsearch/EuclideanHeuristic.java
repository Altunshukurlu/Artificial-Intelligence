package mapsearch;

public class EuclideanHeuristic {

	// Sources: search for "Haversine" on
	// http://www.faqs.org/faqs/geography/infosystems-faq/
	// that page was also recommended by
	// http://mathforum.org/library/drmath/view/51879.html

	public static double eval(StateNode currentState, StateNode goalState) {
		double R = 6371.0; // km

		double dLat = (goalState.lat - currentState.lat) * Math.PI / 180;
		double dLon = (goalState.lon - currentState.lon) * Math.PI / 180;

		double c = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(currentState.lat * Math.PI / 180)
				* Math.cos(goalState.lat * Math.PI / 180) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);

		double d = 2 * Math.atan2(Math.sqrt(c), Math.sqrt(1 - c));

		return R * d;
	}
}
