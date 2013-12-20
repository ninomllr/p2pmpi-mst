import java.io.Serializable;

/**
 * The edge connects two nodes
 * 
 * @author nino
 * 
 */
public class Edge implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Node _from;
	protected Node _to;
	protected int _weight;
	boolean _isCandidate;
	
	/**
	 * Gets if the Edge is still a candidate
	 * @return
	 */
	public boolean getIsCandidate() {
		return _isCandidate;
	}


	/**
	 * Sets if the Edge is still a candidate
	 * @param isCandidate
	 */
	public void setIsCandidate(boolean isCandidate) {
		this._isCandidate = isCandidate;
	}


	/**
	 * Constructor
	 * @param from
	 * @param to
	 * @param weight
	 */
	public Edge(Node from, Node to, int weight) {
		_from = from;
		_to = to;
		_weight = weight;
		_isCandidate = true;
	}
	

	/**
	 * Gets the weight of the edge
	 * @return
	 */
	public int getWeight() {
		return _weight;
	}

	/**
	 * Sets the weight of the edge
	 * @param weight
	 */
	public void setWeight(int weight) {
		this._weight = weight;
	}

	/**
	 * Get start node
	 * 
	 * @return
	 */
	public Node getFrom() {
		return _from;
	}

	/**
	 * Set start node
	 * 
	 * @param from
	 */
	public void setFrom(Node from) {
		this._from = from;
	}

	/**
	 * Get destination node
	 * 
	 * @return
	 */
	public Node getTo() {
		return _to;
	}

	/**
	 * Set destination node
	 * 
	 * @param to
	 */
	public void setTo(Node to) {
		this._to = to;
	}
	
	/**
	 * Displays the edge as string
	 */
	public String toString() {
		return "Edge: " + _from.getId() + "->" + _to.getId()+" ("+_weight+")";
	}
}