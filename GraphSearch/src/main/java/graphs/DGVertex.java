package graphs;

import java.util.Set;

public interface DGVertex<E> {
    String getId();
    Set<E> getEdges();
}
