// package OrigScan;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.lang.Math;

public class Graph {

	private static final int KAWASAKI_ANGLE = 180;

	private List<Node> vertices;

	public Graph(ArrayList<Node> n) {
		vertices = new ArrayList<>(n);
	}

	private void colorClear() {
		for (int i = 0; i < vertices.size(); ++i) {
			if (!vertices.get(i).boundary) {
				vertices.get(i).color = "";
			}
		}
	}

	/*
	 * BFS to assign colors
	 */
	private void colorAssignment() {
		colorClear();
		Queue<Node> q = new LinkedList<>();
		if (vertices.size() == 0) {
			System.out.println("Graph has 0 elements, cannot color its vertices");
			return;
		}
		q.add(vertices.get(0));
		vertices.get(0).color = "r";
		while (q.size() != 0) {
			Node temp = q.poll();
			for (int i = 0; i < temp.edges.size(); ++i) {
				Node examinee = temp.edges.get(i).end;
				if (!examinee.boundary) {
					if (examinee.color.equals("")) {
						examinee.color = temp.color.equals("r") ? "b" : "r";
						q.add(examinee);
					} else if (examinee.color.equals(temp.color)) {
						System.out.println("Pattern is not two colorable");
						return;
					}
				}
			}
		}
	}

	private boolean isTwoColorable() {
		for (int i = 0; i < vertices.size(); ++i) {
			if (!vertices.get(i).boundary) {
				for (int j = 0; j < vertices.get(i).edges.size(); ++j) {
					if (!vertices.get(i).edges.get(j).end.boundary
							&& vertices.get(i).color.equals(vertices.get(i).edges.get(j).end.color)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean checkMaekawaTheorem() {
		for (int i = 0; i < vertices.size(); ++i) {
			if (!vertices.get(i).boundary) {
				int mCount = 0;
				int vCount = 0;
				for (int j = 0; j < vertices.get(i).edges.size(); ++j) {
					if (vertices.get(i).edges.get(j).foldType) {
						++mCount;
					} else {
						++vCount;
					}

				}
				if (Math.abs(mCount - vCount) != 2) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkKawasakiTheorem() {
		for (int i = 0; i < vertices.size(); ++i) {
			if (!vertices.get(i).boundary) {
				if (vertices.get(i).edges.size() % 2 != 0) {
					return false;
				}
				int odd = 0;
				int even = 0;
				for (int j = 0; j < vertices.get(i).edges.size(); ++j) {
					if (j % 2 == 0) {
						even += vertices.get(i).edges.get(j).angle;
					} else {
						odd += vertices.get(i).edges.get(j).angle;
					}
				}
				if (odd != KAWASAKI_ANGLE || even != KAWASAKI_ANGLE) {
					return false;
				}
			}
		}
		return true;
	}

	// input angles in ccw for this to work
	private boolean checkBigLittleBigLemma() {
		for (int i = 0; i < vertices.size(); ++i) {
			if (!vertices.get(i).boundary) {
				// accommodate circular nature -> edge cases
				if (vertices.get(i).edges.size() > 2) {
					// j = 0 case
					if (!bigLittleBigLemmaHelper(i, 0, vertices.get(i).edges.size() - 1, 1))
						return false;
					// j = n - 1 case
					if (!bigLittleBigLemmaHelper(i, vertices.get(i).edges.size() - 1, vertices.get(i).edges.size() - 2,
							0))
						return false;
				}
				// general case
				for (int j = 1; j < vertices.get(i).edges.size() - 1; ++j) {
					if (!bigLittleBigLemmaHelper(i, j, j - 1, j + 1))
						return false;
				}
			}
		}
		return true;
	}

	private boolean bigLittleBigLemmaHelper(int gIdx, int curr, int before, int after) {
		if (vertices.get(gIdx).edges.get(curr).angle < vertices.get(gIdx).edges.get(after).angle
				&& vertices.get(gIdx).edges.get(curr).angle < vertices.get(gIdx).edges.get(before).angle) {
			if (vertices.get(gIdx).edges.get(curr).foldType == vertices.get(gIdx).edges.get(after).foldType) {
				return false;
			}
		}
		return true;
	}

	public boolean generalCheck() {
		colorAssignment();
		if (!isTwoColorable()) {
			System.out.println("Pattern is not two colorable");
			return false;
		}
		if (!checkMaekawaTheorem()) {
			System.out.println("Pattern does not uphold Maekawa's theorem");
			return false;
		}
		if (!checkKawasakiTheorem()) {
			System.out.println("Pattern does not uphold Kawasaki's theorem");
			return false;
		}
		if (!checkBigLittleBigLemma()) {
			System.out.println("A layer penetrates a fold in the pattern");
			return false;
		}
		return true;
	}

	public static class Node {
		private List<Edge> edges;
		private String color;
		private boolean boundary;

		public Node(List<Edge> e, boolean b) {
			if (e != null)
				edges = new ArrayList<>(e);
			else
				edges = null;
			boundary = b;
		}
	}

	public static class Edge {
		private Node end;
		private boolean foldType;
		private int angle;

		public Edge(Node e, boolean f, int a) {
			end = e;
			foldType = f;
			angle = a;
		}
	}
}
