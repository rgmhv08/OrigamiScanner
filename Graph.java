// package OrigScan;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.lang.Math;

/*
 * Models a crease pattern using an undirected graph, with nodes representing intersections between
 * creases and edges representing these creases. Acts as an immutable object.
 */
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
	 * Runs a BFS to systematically assign colors to nodes in the graph.
	 */
	private void colorAssignment() {
		colorClear();
		Queue<Node> q = new LinkedList<>();
		// edge case
		if (vertices.size() == 0) {
			System.out.println("Graph has 0 elements, cannot color its vertices");
			return;
		}
		// initialization	
		q.add(vertices.get(0));
		vertices.get(0).color = "r";
		while (q.size() != 0) {
			Node temp = q.poll();
			for (int i = 0; i < temp.edges.size(); ++i) {
				Node examinee = temp.edges.get(i).end;
				// ensures examinee is not on the paper boundary, since those intersections
				// do not count as nodes
				if (!examinee.boundary) {
					if (examinee.color.equals("")) {
						// alternating color assignment
						examinee.color = temp.color.equals("r") ? "b" : "r";
						q.add(examinee);
					} else if (examinee.color.equals(temp.color)) {
						// return early if a node shares the same color as a connected node
						System.out.println("Pattern is not two colorable");
						return;
					}
				}
			}
		}
	}
	
	/*
	 * Traverses each node and its edges to test for two-colorability.
	 */
	private boolean isTwoColorable() {
		for (int i = 0; i < vertices.size(); ++i) {
			if (!vertices.get(i).boundary) {
				for (int j = 0; j < vertices.get(i).edges.size(); ++j) {
					// return early if a node is found to have the same color as a connected neighbor
					if (!vertices.get(i).edges.get(j).end.boundary
							&& vertices.get(i).color.equals(vertices.get(i).edges.get(j).end.color)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/*
	 * Verifies that the Maekawa Theorem holds for the given crease pattern
	 * by counting the number of mountain and valley folds at each vertex.
	 * 
	 * https://courses.csail.mit.edu/6.849/fall10/lectures/L20_images.pdf
	 */
	private boolean checkMaekawaTheorem() {
		for (int i = 0; i < vertices.size(); ++i) {
			// excludes vertices on the boundary of the paper
			if (!vertices.get(i).boundary) {
				int mCount = 0;
				int vCount = 0;
				for (int j = 0; j < vertices.get(i).edges.size(); ++j) {
					// true represents a mountain fold and false signifies
					// a valley fold
					if (vertices.get(i).edges.get(j).foldType) {
						++mCount;
					} else {
						++vCount;
					}

				}
				// return false if the number of mountain and valley folds does not 
				// differ by two at any given vertex
				if (Math.abs(mCount - vCount) != 2) {
					return false;
				}
			}
		}
		return true;
	}
	
	/*
	 * Verifies that the Kawasaki Theorem holds for the given crease pattern
	 * by alternately summing angle measures at each vertex in a counter-clockwise manner.
	 * 
	 * https://courses.csail.mit.edu/6.849/fall10/lectures/L20_images.pdf
	 */
	private boolean checkKawasakiTheorem() {
		for (int i = 0; i < vertices.size(); ++i) {
			// excludes vertices on the boundary of the paper
			if (!vertices.get(i).boundary) {
				// each vertex MUST have an even number of angles and therefore edges
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
				// The sum of odd angles must equal the sum of the even angles.
				// Since each vertex sums to 360 degrees, the odd and even measurements must equal
				// 180 degrees each.
				if (odd != KAWASAKI_ANGLE || even != KAWASAKI_ANGLE) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * Verifies that the big-little-big lemma holds for the given crease pattern
	 * by examining angle measures with respect to their neighbors at each vertex 
	 * in a counter-clockwise manner.
	 * 
	 * https://en.wikipedia.org/wiki/Big-little-big_lemma
	 */
	private boolean checkBigLittleBigLemma() {
		for (int i = 0; i < vertices.size(); ++i) {
			if (!vertices.get(i).boundary) {
				// accommodate circular nature of a vertex -> edge cases
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
	
	/*
	 * Helper method to check the big-little-big lemma that verifies if the given angle
	 * is a local minimum.
	 */
	private boolean bigLittleBigLemmaHelper(int gIdx, int curr, int before, int after) {
		if (vertices.get(gIdx).edges.get(curr).angle < vertices.get(gIdx).edges.get(after).angle
				&& vertices.get(gIdx).edges.get(curr).angle < vertices.get(gIdx).edges.get(before).angle) {
			if (vertices.get(gIdx).edges.get(curr).foldType == vertices.get(gIdx).edges.get(after).foldType) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Checks if the given crease pattern is flat-foldable.
	 * 
	 *  Details regarding these laws can be found in the "Pure Origami" section: 
	 *  https://en.wikipedia.org/wiki/Mathematics_of_paper_folding#Pure_origami
	 */
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
	
	/*
	 * Models nodes as intersections between creases. 
	 */
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

	/*
	 * Models edges as crease lines in between two nodes. 
	 */
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
