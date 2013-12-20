
/**
 * Helper class to generate the graph for the exercise
 * @author nino
 *
 */
public class GraphGenerator {
	
	/*
	 * Sets up the graph provided for the example
	 */
	public static Graph<Node, Edge> getGraph() {
		Graph<Node, Edge> graph = new Graph<Node, Edge>();
		
		// generate nodes
		Node node0 = new Node(0);
		Node node1 = new Node(1);
		Node node2 = new Node(2);
		Node node3 = new Node(3);
		Node node4 = new Node(4);
		Node node5 = new Node(5);
		Node node6 = new Node(6);
		Node node7 = new Node(7);
		Node node8 = new Node(8);
		Node node9 = new Node(9);
		Node node10 = new Node(10);
		Node node11 = new Node(11);
		graph.addNode(node0);
		graph.addNode(node1);
		graph.addNode(node2);
		graph.addNode(node3);
		graph.addNode(node4);
		graph.addNode(node5);
		graph.addNode(node6);
		graph.addNode(node7);
		graph.addNode(node8);
		graph.addNode(node9);
		graph.addNode(node10);
		graph.addNode(node11);
		
		// create edges
		graph.addEdge(new Edge(node0, node3, 7));
		graph.addEdge(new Edge(node0, node4, 16));
		
		graph.addEdge(new Edge(node1, node4, 2));
		graph.addEdge(new Edge(node1, node6, 10));
		
		graph.addEdge(new Edge(node2, node6, 13));
		graph.addEdge(new Edge(node2, node7, 9));
		
		graph.addEdge(new Edge(node3, node0, 7));
		graph.addEdge(new Edge(node3, node4, 3));
		graph.addEdge(new Edge(node3, node8, 1));
		
		graph.addEdge(new Edge(node4, node3, 3));
		graph.addEdge(new Edge(node4, node0, 16));
		graph.addEdge(new Edge(node4, node1, 2));
		graph.addEdge(new Edge(node4, node5, 5));
		graph.addEdge(new Edge(node4, node9, 6));
		graph.addEdge(new Edge(node4, node8, 15));
		
		graph.addEdge(new Edge(node5, node4, 5));
		graph.addEdge(new Edge(node5, node6, 18));
		graph.addEdge(new Edge(node5, node9, 17));
		
		graph.addEdge(new Edge(node6, node5, 18));
		graph.addEdge(new Edge(node6, node1, 10));
		graph.addEdge(new Edge(node6, node2, 13));
		graph.addEdge(new Edge(node6, node7, 12));
		graph.addEdge(new Edge(node6, node10, 19));
		graph.addEdge(new Edge(node6, node9, 4));
		
		graph.addEdge(new Edge(node7, node6, 12));
		graph.addEdge(new Edge(node7, node2, 9));
		graph.addEdge(new Edge(node7, node11, 14));
		
		graph.addEdge(new Edge(node8, node3, 1));
		graph.addEdge(new Edge(node8, node4, 15));
		graph.addEdge(new Edge(node8, node9, 13));
		
		graph.addEdge(new Edge(node9, node8, 13));
		graph.addEdge(new Edge(node9, node4, 6));
		graph.addEdge(new Edge(node9, node5, 17));
		graph.addEdge(new Edge(node9, node6, 4));
		graph.addEdge(new Edge(node9, node10, 8));
		
		graph.addEdge(new Edge(node10, node9, 8));
		graph.addEdge(new Edge(node10, node6, 19));
		graph.addEdge(new Edge(node10, node11, 11));
		
		graph.addEdge(new Edge(node11, node10, 11));
		graph.addEdge(new Edge(node11, node7, 14));
		
		return graph;
	}
}
