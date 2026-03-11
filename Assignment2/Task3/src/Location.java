/**
 * Interface for objects that occupy a position on the Catan board.
 * Implemented by both Road (positioned on an Edge) and
 * Building (positioned on a Node).
 *
 * Returns Object because Road returns an Edge and Building returns a Node.
 * Callers should cast to the appropriate type.
 */
public interface Location {
    /**
     * Returns the board location of this object.
     * Road returns Edge; Building returns Node.
     * @return the location object
     */
    public Object getLocation();
}