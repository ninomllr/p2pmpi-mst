import java.io.Serializable;

/**
 * Message class to communicate between neighbours in the tree
 * @author nino
 *
 */
class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Candidate or Leader
	MessageType type;
	
	// as soon as one node gets all the edges it decides which one is the leader
	Edge leader;
	
	// every node adds the other nodes it knows in the mst
	Node[] nodes;
	
	// every node adds the own candidate edge
	Edge[] edges;
}