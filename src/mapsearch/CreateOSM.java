/*
 * Britton Wolfe
 * 1/14/2010
 * CS 572 Heuristic Problem Solving
 *
 * This class reads data from standard input and creates an OSM file
 *   that can be loaded into Kosmos to highlight part of the map
 */

package mapsearch;

import java.io.*;
import java.util.*;

public class CreateOSM {

	/**
	 * takes two command-line arguments: the file name to create and the buffer
	 * size
	 */
	public static void main(String[] args) {
		InputStreamReader inp = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(inp);

		CreateOSM cosm = new CreateOSM();

		int refreshFreq = Integer.parseInt(args[1]);
		cosm.writeOSMFile(args[0], br, refreshFreq);
	}

	public void writeOSMFile(String outputFile, BufferedReader input,
			int refreshFrequency) {
		String line;
		StringTokenizer parser;

		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(
					outputFile));
			output.write(new String("<osm version=\"0.6\">\n"));
			output.write(osmFooter);
			output.flush();
			output.close();

			Queue<OSMEdge> edgeQ = new LinkedList<OSMEdge>();
			Queue<OSMNode> nodeQ = new LinkedList<OSMNode>();
			Set<Long> nodeIDs = new HashSet<Long>();

			while ((line = input.readLine()) != null) {
				parser = new StringTokenizer(line);

				int numTokens = parser.countTokens();

				if (numTokens == 2) {
					// an edge
					long startID = Long.parseLong(parser.nextToken());
					long endID = Long.parseLong(parser.nextToken());

					edgeQ.offer(new OSMEdge(startID, endID));

					if (edgeQ.size() >= refreshFrequency) {
						// dump the nodes and edges to the end of the file
						dumpToFile(outputFile, nodeQ, edgeQ);
					}
				} else if (numTokens == 3) {
					// a node
					long id = Long.parseLong(parser.nextToken());

					if (!nodeIDs.contains(id)) {
						double lat = Double.parseDouble(parser.nextToken());
						double lon = Double.parseDouble(parser.nextToken());

						nodeQ.offer(new OSMNode(id, lat, lon));

						nodeIDs.add(id);
					}

				} else {
					System.err.println("ERROR: unexpected input line \"" + line
							+ "\"");
					System.exit(-1);
				}
			}

			// dump whatever is left in the queues
			dumpToFile(outputFile, nodeQ, edgeQ);
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
			return;
		}

	}

	private void dumpToFile(String outputFile, Queue<OSMNode> nodeQ,
			Queue<OSMEdge> edgeQ) {
		try {
			File file = new File(outputFile);
			RandomAccessFile raf = new RandomAccessFile(file, "rw");

			raf.seek(file.length() - osmFooterLength);

			while (!nodeQ.isEmpty()) {
				OSMNode node = nodeQ.poll();

				String line = new String("  <node id=\""
						+ Long.toString(node.id) + "\" lat=\""
						+ Double.toString(node.lat) + "\" lon=\""
						+ Double.toString(node.lon) + "\" visible=\"true\"/>\n");

				raf.write(line.getBytes());
			}

			while (!edgeQ.isEmpty()) {
				OSMEdge edge = edgeQ.poll();

				raf.write(new String("  <way id=\"" + Long.toString(nextWayID)
						+ "\" visible=\"true\">\n").getBytes());
				raf.write(new String("    <nd ref=\""
						+ Long.toString(edge.startID) + "\"/>\n").getBytes());
				raf.write(new String("    <nd ref=\""
						+ Long.toString(edge.endID) + "\"/>\n").getBytes());
				raf.write(new String("    <tag k=\"highlight\" v=\"2\"/>\n")
						.getBytes());
				raf.write(new String("  </way>\n").getBytes());

				nextWayID++;
			}

			raf.write(osmFooter.getBytes());
			raf.close();
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}

	private long nextWayID = 1572000000;

	private static final String osmFooter = "</osm>";
	private static final int osmFooterLength = osmFooter.length();
}

class OSMNode {
	public OSMNode(long id, double lat, double lon) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
	}

	public final long id;
	public final double lat;
	public final double lon;
}

class OSMEdge {
	public OSMEdge(long startID, long endID) {
		this.startID = startID;
		this.endID = endID;
	}

	public final long startID;
	public final long endID;
}