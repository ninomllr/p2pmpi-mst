import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to represent the neighbours of the current node.
 * @author nino
 *
 * @param <N>
 */
public class Neighbours<N extends Node, E extends Edge>{
    protected N _node;
    protected Set<E> _neighbours;
    
    /**
     * Constructor for Neighbours
     * @param node
     */
    public Neighbours(N node) {
		this._node=node;
		this._neighbours = new HashSet<E>();
	}
    
    /**
     * Get main node
     * @return
     */
	public N getNode() {
		return _node;
	}
	
	/**
	 * Set main node
	 * @param node
	 */
	public void setNode(N node) {
		this._node = node;
	}
	
	/**
	 * Get Neighbours of main node
	 * @return
	 */
	public Set<E> getNeighbours() {
		return _neighbours;
	}
	
	/**
	 * Set neighbours of main node
	 * @param neighbors
	 */
	public void setNeighbours(Set<E> neighbors) {
		this._neighbours = neighbors;
	}
	
	/**
	 * Add Neighbour of main node
	 * @param neighbor
	 */
	public void addNeighbour(E neighbor) {
		this._neighbours.add(neighbor);
	}
	
	/**
	 * Gets edge with minimum weight 
	 * @return
	 */
	public E getMin() {

		E e = null;
		for (E n : _neighbours) {
			if (n.getIsCandidate()) {
				if (e == null) 
					e=n;
				if (e.getWeight()>n.getWeight())
					e=n;
			}
		}
		return e;
	}

}
