import java.io.Serializable;

/**
 * A node represents a vertex in the graph
 * @author nino
 *
 */
public class Node implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int _id;
    protected int _belongsTo;
    protected boolean _isNeighbour;

	public Node() {
    	
    }
    
    public Node(int id) 
    {
    	_id = id;
    	_belongsTo = id;
    }
    
    public boolean getIsNeighbour() {
		return _isNeighbour;
	}
    
    public void setIsNeighbour(boolean isNeighbour) {
		this._isNeighbour = isNeighbour;
	}

    
    public int getBelongsTo() {
		return _belongsTo;
	}

	public void setBelongsTo(int belongsTo) {
		this._belongsTo = belongsTo;
	}

    /**
     * Get ID of Node
     * @return
     */
	public int getId() {
		return _id;
	}

	/**
	 * Set ID of node
	 * @param id
	 */
	public void setId(int id) {
		this._id = id;
	}
	
	public String toString() {
		return ""+_id;
	}
}
