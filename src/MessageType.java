import java.io.Serializable;

/**
 * Message type to distinct between candidates and leader messages
 * @author nino
 *
 */
public enum MessageType implements Serializable {
	IS_CANDIDATE,
	IS_LEADER
}
