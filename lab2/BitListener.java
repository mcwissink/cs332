/**
 * An interface used by a BitDisplay that a BitHandler can send a bitstring to
 */
public interface BitListener {
    void bitsReceived(BitHandler handler, String bits);
}