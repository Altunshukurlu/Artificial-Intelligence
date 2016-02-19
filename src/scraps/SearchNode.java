package scraps;

import mapsearch.*;

public class SearchNode implements Comparable<SearchNode> {
	public SearchNode from;
	public StateNode to;
	private double g;
	private double h;
	private double f;

	public SearchNode(SearchNode from, StateNode to, double g, double h) {
		this.from = from;
		this.to = to;
		this.g = g;
		this.h = h;
		this.f = this.g + this.h;
	}

	public double getG() {
		return g;
	}

	public double getH() {
		return h;
	}

	public double getF() {
		return this.f;
	}

	public boolean doesHaveFromValue() {
		if (this.from != null) {
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(SearchNode o) {
		// TODO Auto-generated method stub
		if (this.f > o.f) {
			return 1;
		} else if (this.f < o.f) {
			return -1;
		} else {
			if (this.to.id < o.to.id)
				return 1;
			else
				return 0;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SearchNode) {
			SearchNode c = (SearchNode) o;
			if (this.to.id == c.to.id) {
				return true;

			}
			return false;
		}
		return false;
	}
}
