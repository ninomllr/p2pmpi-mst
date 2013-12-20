import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main graph class
 * 
 * @author nino
 * 
 * @param <N>
 * @param <E>
 */
public class Graph<N extends Node, E extends Edge> {

	protected List<N> _nodes;

	protected List<E> _edges;

	// Index for fast access
	private Map<String, Neighbours<N,E>> _neighbours;

	/**
	 * Constructor of Class
	 */
	public Graph() {
		this._nodes = new ArrayList<N>();
		this._edges = new ArrayList<E>();
		this._neighbours = new HashMap<String, Neighbours<N,E>>();
	}

	/**
	 * Add Node to node list
	 * 
	 * @param node
	 */
	public void addNode(N node) {
		
		_neighbours.put(""+node.getId(), new Neighbours<N, E>(node));
		
		_nodes.add(node);
	}

	/**
	 * Add Edge to edge list
	 * 
	 * @param edge
	 */
	public void addEdge(E edge) {
	
		Neighbours<N,E> n = _neighbours.get(""+edge.getFrom().getId());
		n.addNeighbour(edge);
		
		_edges.add(edge);
	}

	/**
	 * Get Edge between two nodes
	 * 
	 * @param node
	 * @param neighbor
	 * @return
	 */
	public E getEdge(N node, N neighbor) {

		if (node == null || neighbor == null)
			return null;

		for (E edge : _edges) {
			if (edge.getFrom() == node && edge.getTo() == neighbor)
				return edge;
		}
		return null;
	}

	/**
	 * Remove edge from graph
	 * 
	 * @param edge1
	 */
	public void removeEdge(E edge1) {
		_edges.remove(edge1);
	}

	/**
	 * Get node from graph
	 * 
	 * @param firstId
	 * @return
	 */
	public N getNode(int firstId) {
		for (N node : _nodes) {
			if (node.getId()==firstId)
				return node;
		}

		return null;
	}

	/**
	 * Get list of nodes
	 * 
	 * @return
	 */
	public List<N> getNodes() {
		return _nodes;
	}

	/**
	 * Get list of edges
	 * 
	 * @return
	 */
	public List<E> getEdges() {
		return _edges;
	}

	/**
	 * Get Neighbour lookup map
	 * 
	 * @return
	 */
	public Map<String, Neighbours<N,E>> getNeighbours() {
		return _neighbours;
	}

	/**
	 * Gets neighbour edge with minimal weight
	 * @param key
	 * @return
	 */
	public Edge getMinEdgeForNode(String key) {
		return _neighbours.get(key).getMin();
	}

	/**
	 * Gets Number of neighbours
	 * @param key
	 * @return
	 */
	public int getNeighboursCount(String key) {
		return _neighbours.get(key).getNeighbours().size();
	}

	/**
	 * Gets an object of neighbours
	 * @param key
	 * @return
	 */
	public Neighbours<N, E> getNeighbours(int key) {
		return _neighbours.get(""+key);
	}

}