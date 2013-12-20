import p2pmpi.mpi.MPI;
import p2pmpi.mpi.Request;

/**
 * Messenger handles all communication between the processors
 * @author nino
 *
 */
public class Messenger {

	/**
	 * Sends a candiate message to the defined receiver
	 * @param nodesInSpanning
	 * @param edgesInSpanning
	 * @param receiver
	 * @return
	 */
	public static Request sendCandidateMessage(Node[] nodesInSpanning,
			Edge[] edgesInSpanning, Node receiver) {
		Message msg = new Message();
		msg.edges = edgesInSpanning;
		msg.nodes = nodesInSpanning;
		msg.type = MessageType.IS_CANDIDATE;

		Message[] bfr = new Message[1];
		bfr[0] = msg;
		Request rr = MPI.COMM_WORLD.Isend(bfr, 0, 1, MPI.OBJECT,
				receiver.getId(), 0);
		return rr;
	}

	/**
	 * Sends a leader message to the defined node
	 * @param leader
	 * @param neighbour
	 * @return
	 */
	public static Request sendIsLeaderMessage(Edge leader, Node neighbour) {

		Message msg = new Message();
		msg.type = MessageType.IS_LEADER;
		msg.leader = leader;

		Message[] bfr = new Message[1];
		bfr[0] = msg;

		Request rr = MPI.COMM_WORLD.Isend(bfr, 0, 1, MPI.OBJECT,
				neighbour.getId(), 0);
		return rr;
	}

	/**
	 * Sends a leader message to all neighbours
	 * @param leader
	 * @param neighboursOfSender
	 */
	public static void sendIsLeaderMessageToAll(Edge leader,
			Node[] neighboursOfSender) {

		int numberOfNeighbours = 0;
		for (Node n : neighboursOfSender) {
			if (n != null)
				numberOfNeighbours++;
		}

		for (int nb = 0; nb < numberOfNeighbours; nb++) {
			Message msg = new Message();
			msg.type = MessageType.IS_LEADER;
			msg.leader = leader;

			Message[] bfr = new Message[1];
			bfr[0] = msg;

			MPI.COMM_WORLD.Isend(bfr, 0, 1, MPI.OBJECT,
					neighboursOfSender[nb].getId(), 0);
		}
	}

	/**
	 * Listens to all neighbours to get an object
	 * @param recordBuffer
	 * @param connectedNodes
	 * @param rank
	 * @return
	 */
	public static Request[] listenToNeighboursObj(Message[][] recordBuffer,
			Node[] connectedNodes, int rank) {
		int numberOfNeighbours = 0;
		for (Node n : connectedNodes) {
			if (n != null)
				numberOfNeighbours++;
		}

		Request[] requests = new Request[numberOfNeighbours];

		int nb = 0;
		for (Node n : connectedNodes) {
			if (n == null)
				break;
			requests[nb] = MPI.COMM_WORLD.Irecv(recordBuffer[nb], 0, 1,
					MPI.OBJECT, n.getId(), 0);
			nb++;
		}

		return requests;
	}

	/**
	 * Listen to all neighbours for an integer
	 * @param recordBuffer
	 * @param connectedNodes
	 * @param rank
	 * @return
	 */
	public static Request[] listenToNeighboursInt(int[][] recordBuffer,
			Node[] connectedNodes, int rank) {
		int numberOfNeighbours = 0;
		for (Node n : connectedNodes) {
			if (n != null)
				numberOfNeighbours++;
		}

		Request[] requests = new Request[numberOfNeighbours];

		int nb = 0;
		for (Node n : connectedNodes) {

			requests[nb] = MPI.COMM_WORLD.Irecv(recordBuffer[nb], 0, 2,
					MPI.INT, n.getId(), 1);
			nb++;
		}

		return requests;
	}

	/**
	 * Announce to all neighbours if node connected to the other node in this cycle
	 * @param isNeighbour
	 * @param neighbour
	 */
	public static void announceAsNeighbour(int isNeighbour, int neighbour) {
		int[] SEND_BUFFER = new int[1];
		SEND_BUFFER[0] = isNeighbour;
		MPI.COMM_WORLD.Isend(SEND_BUFFER, 0, SEND_BUFFER.length, MPI.INT,
				neighbour, 1);
	}

}
