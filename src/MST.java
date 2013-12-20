import p2pmpi.mpi.MPI;
import p2pmpi.mpi.Request;

/**
 * P2P MPI implementation of a minimal spanning tree algorithm
 * @author nino
 *
 */
public class MST {

	// rank of processor and number of all
	static int rank, size;

	// waiting time for unsuccessful listening
	static int timeCap = 10;

	// nb. of processes (i.e nodes of the graph)
	static final int N = 12;

	// init of graph
	static Graph<Node, Edge> graph;
	
	// all neighbour nodes of the processor in the mst
	static Node[] nodesInTree;
	
	// outgoing edges of the node
	static Edge[] mst;

	// termination command
	static boolean wantsTerminate;

	public static void main(String[] args) {
		MPI.Init(args);

		// number of processors
		size = MPI.COMM_WORLD.Size();
		// current processor id
		rank = MPI.COMM_WORLD.Rank();

		// generate graph
		graph = GraphGenerator.getGraph();

		nodesInTree = new Node[N];
		mst = new Edge[N];

		if (size != N)
			System.out.println("run with -n " + N);
		else {

			// find first leader
			Edge leader = findLeader();

			// announce and go on until we find the solution
			while (leader != null) {

				addNewNeighbour(leader.getTo());
				informOppositeNeighbour(leader.getTo());
				leader = findLeader();
			}

			// we print our neighbors for results:
			StringBuffer sb = new StringBuffer("rank " + rank + " nb: ");
			for (int j = 0; j < mst.length; j++)
				if (mst[j] != null)
					sb.append(mst[j] + ", ");
			System.out.println(sb);

		}

		MPI.Finalize();

	}

	/**
	 * Converts the neighbour object to a simple node array
	 * @param neighbours
	 * @return
	 */
	private static Node[] ConvertNeighboursToArray(
			Neighbours<Node, Edge> neighbours) {
		int numberOfNeighbours = neighbours.getNeighbours().size();
		Node[] nodes = new Node[numberOfNeighbours];

		int i = -1;
		for (Edge e : neighbours.getNeighbours()) {
			i++;
			nodes[i] = e.getTo();
		}

		return nodes;
	}

	/**
	 * Informs all nodes that we now connected to them, so they can later add the edge to their
	 * external edges too
	 * @param to
	 */
	private static void informOppositeNeighbour(Node to) {

		Neighbours<Node, Edge> neighbours = graph.getNeighbours(rank);
		int numberOfNeighbours = neighbours.getNeighbours().size();

		Node[] nodes = ConvertNeighboursToArray(neighbours);

		int m = 0; // Successful reads
		int[][] recordBuffer = new int[numberOfNeighbours][1];
		boolean[] gotMessage = new boolean[numberOfNeighbours];

		// listen to all graph neigbhbours for an answer
		Request[] requests = Messenger.listenToNeighboursInt(recordBuffer,
				nodes, rank);

		// announce to all graph neighbours who we added
		for (Edge edge : neighbours.getNeighbours()) {
			Messenger
					.announceAsNeighbour(edge.getTo().getIsNeighbour() ? 1 : 0,
							edge.getTo().getId());
		}

		// wait for all answers
		do {
			for (int nb = 0; nb < numberOfNeighbours; nb++) {
				if (!gotMessage[nb]) {
					if (requests[nb].Test() != null) {
						gotMessage[nb] = true;
						m++;
						if (recordBuffer[nb][0] == 1) {
							addNewNeighbour(nodes[nb]);
						}
					}
				}
			}
			try {
				Thread.currentThread();
				Thread.sleep(timeCap);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (m < numberOfNeighbours);

	}

	/**
	 * Add an edge and corresponding node as a neighbour
	 * @param node
	 */
	private static void addNewNeighbour(Node node) {

		Edge mstEdge = graph.getEdge(graph.getNode(rank), node);

		// skip if edge is not directly connected to node
		if (mstEdge == null)
			return;

		boolean isAlreadyNeighbour = false;

		int i = -1;

		// loop through MST and add node if necessary
		for (Node n : nodesInTree) {
			i++;

			if (n == null) {
				break;
			} else {
				if (n.getId() == node.getId())
					isAlreadyNeighbour = true;

			}
		}

		if (!isAlreadyNeighbour) {
			node.setIsNeighbour(true);
			nodesInTree[i] = node;
			mst[i] = mstEdge;
			mst[i].setIsCandidate(false);
		}
	}

	/**
	 * Finds a leader for this cycle
	 * @return
	 */
	private static Edge findLeader() {

		// every node decides the personal favorite
		Edge currentLeader = graph.getMinEdgeForNode("" + rank);

		int numberOfNeighbours = 0;
		for (Node n : nodesInTree) {
			if (n != null)
				numberOfNeighbours++;
		}

		// we need requests for all our neighbors
		boolean[] gotMessage = new boolean[numberOfNeighbours];

		int m = 0; // Successful reads
		Message[][] recordBuffer = null;
		recordBuffer = new Message[numberOfNeighbours][1];

		Request[] requests = Messenger.listenToNeighboursObj(recordBuffer,
				nodesInTree, rank);

		// prepare information to send and add own information
		Node[] mstNodes = new Node[N];
		Edge[] mstEdges = new Edge[N];
		for (Node n : nodesInTree) {
			if (n == null)
				break;
			mstNodes = checkAndAddNodes(mstNodes, n);
		}
		mstEdges = checkAndAddEdge(mstEdges, currentLeader);

		// listen to everything except one
		do {
			for (int nb = 0; nb < numberOfNeighbours; nb++) {
				if (!gotMessage[nb]) {
					if (requests[nb].Test() != null) {
						gotMessage[nb] = true;
						m++;
					}
				}
			}
			try {
				Thread.currentThread();
				Thread.sleep(timeCap);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (m < numberOfNeighbours - 1);

		// everyone answered, we can find a leader now
		if (m == numberOfNeighbours) {

			mstEdges = completeEdgeList(recordBuffer, mstEdges);
			mstNodes = completeNodeList(recordBuffer, mstNodes);

			currentLeader = decideForLeader(mstNodes, mstEdges);
			Messenger.sendIsLeaderMessageToAll(currentLeader, nodesInTree);
		}
		// everyone except one answered, we send our information to the next one
		else {
			// we got a Message from all but one neighbour
			int nb = 0;
			while (gotMessage[nb]) {
				nb++;
			}

			// send our candidate info to the link which did not answer yet
			mstEdges = completeEdgeList(recordBuffer, mstEdges);
			mstNodes = completeNodeList(recordBuffer, mstNodes);
			Request rr = Messenger.sendCandidateMessage(mstNodes, mstEdges,
					nodesInTree[nb]);
			rr.Wait();

			// now we wait for the message on link 'i'
			do {
				if (requests[nb].Test() != null) {
					m = m + 1;
					if (recordBuffer[nb][0].type == MessageType.IS_CANDIDATE) {

						mstEdges = completeEdgeList(recordBuffer, mstEdges);
						mstNodes = completeNodeList(recordBuffer, mstNodes);

						// we got a candidate message, so we decide who is the
						// leader
						currentLeader = decideForLeader(mstNodes, mstEdges);

					} else {

						// we got a leader message, so we store the leader
						currentLeader = recordBuffer[nb][0].leader;
					}
					// send a leader message to the unanswered node
					for (int k = 0; k < numberOfNeighbours; k++) {
						if (k != nb) {
							rr = Messenger.sendIsLeaderMessage(currentLeader,
									nodesInTree[k]);
							rr.Wait();
						}
					}
				}
				try {
					Thread.currentThread();
					Thread.sleep(timeCap);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (m < numberOfNeighbours);

		}

		return currentLeader;
	}

	/**
	 * add the node information that the processor knows to the array, so it can be distributed
	 * @param recordBuffer
	 * @param mstNodes
	 * @return
	 */
	private static Node[] completeNodeList(final Message[][] recordBuffer,
			Node[] mstNodes) {
		for (int nb = 0; nb < recordBuffer.length; nb++) {

			if (recordBuffer[nb][0] == null)
				continue;

			// we add all to our local information
			for (Node n : recordBuffer[nb][0].nodes) {
				if (n == null)
					break;
				mstNodes = checkAndAddNodes(mstNodes, n);
			}
		}

		return mstNodes;
	}

	/**
	 * Add the edge information to the array so it can be distributed
	 * @param recordBuffer
	 * @param mstEdges
	 * @return
	 */
	private static Edge[] completeEdgeList(final Message[][] recordBuffer,
			Edge[] mstEdges) {
		for (int nb = 0; nb < recordBuffer.length; nb++) {

			if (recordBuffer[nb][0] == null)
				continue;

			for (Edge e : recordBuffer[nb][0].edges) {
				if (e == null)
					break;
				mstEdges = checkAndAddEdge(mstEdges, e);
			}
		}

		return mstEdges;
	}

	/**
	 * Decide for the leader if we have all information (meaning, we have all proposed candidate edges, and know
	 * all the members of the mst)
	 * @param mstNodes
	 * @param mstEdges
	 * @return
	 */
	private static Edge decideForLeader(Node[] mstNodes, Edge[] mstEdges) {

		Edge leader = null;

		for (Edge e : mstEdges) {
			if (e == null)
				break;

			if (HasForeignNode(e, mstNodes)) {
				if (leader == null) {
					leader = e;
				} else {
					if (leader.getWeight() > e.getWeight())
						leader = e;
					else if (leader.getWeight() == e.getWeight()) {
						// special case if we have the same weight, we choose the one with the smaller processor id
						if (e.getFrom().getId() < leader.getFrom().getId())
							leader = e;
					}
				}
			}
		}

		return leader;
	}

	/**
	 * Check if the edge connects the mst with another external tree, to avoid making connection between
	 * nodes in the same tree
	 * @param edge
	 * @param list
	 * @return
	 */
	private static boolean HasForeignNode(final Edge edge, final Node[] list) {

		boolean first = ContainsNode(edge.getFrom(), list);
		boolean second = ContainsNode(edge.getTo(), list);

		return !(first && second);
	}

	/**
	 * Check if node is contained in node array
	 * @param node
	 * @param list
	 * @return
	 */
	private static boolean ContainsNode(final Node node, final Node[] list) {
		for (Node n : list) {
			if (n == null)
				break;
			if (n.getId() == node.getId())
				return true;
		}

		return false;
	}

	/**
	 * Add node to array if it isn't already there
	 * @param mstNodes
	 * @param node
	 * @return
	 */
	private static Node[] checkAndAddNodes(Node[] mstNodes, Node node) {
		boolean doAdd = true;
		int pos = -1;
		for (Node n : mstNodes) {
			pos++;
			if (n == null)
				break;
			if (n.getId() == node.getId()) {
				doAdd = false;
				break;
			}
		}

		if (doAdd)
			mstNodes[pos] = node;
		return mstNodes;
	}

	/**
	 * Add edge to array if it isn't already there
	 * @param mstEdges
	 * @param currentLeader
	 * @return
	 */
	private static Edge[] checkAndAddEdge(Edge[] mstEdges, Edge currentLeader) {
		boolean doAdd = true;
		int pos = -1;
		for (Edge e : mstEdges) {
			pos++;
			if (e == null)
				break;
			if ((e.getTo() == currentLeader.getTo() && e.getFrom() == currentLeader
					.getFrom())
					|| (e.getTo() == currentLeader.getFrom() && e.getFrom() == currentLeader
							.getTo())) {
				doAdd = false;
				break;
			}
		}

		if (doAdd)
			mstEdges[pos] = currentLeader;

		return mstEdges;
	}

}
