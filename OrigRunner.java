// package OrigScan;

import java.util.ArrayList;

//import OrigScan.Graph.Edge;
//import OrigScan.Graph.Node;

public class OrigRunner {

	public static void main(String[] args) {
		Edge one = new Edge(new Node(null, true), false, 46);
		Edge two = new Edge(new Node(null, true), true, 44);
		Edge three = new Edge(new Node(null, true), false, 90);
		Edge four = new Edge(new Node(null, true), false, 46);
		Edge five = new Edge(new Node(null, true), true, 44);
		Edge six = new Edge(new Node(null, true), false, 90);
		ArrayList<Edge> x = new ArrayList<>();
		x.add(one);
		x.add(two);
		x.add(three);
		x.add(four);
		x.add(five);
		x.add(six);
		Node main = new Node(x, false);
		ArrayList<Node> gg = new ArrayList<>();
		gg.add(main);
		Graph pattern = new Graph(gg);
		if (pattern.generalCheck()) System.out.println("works");
	}

}
