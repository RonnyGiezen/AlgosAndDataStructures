package graphs;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DirectedGraph<V extends DGVertex<E>, E extends DGEdge<V>> {

    private Map<String, V> vertices = new HashMap<>();

    /**
     * representation invariants:
     * 1.  all vertices in the graph are unique by their implementation of the getId() method
     * 2.  all edges in the graph reference vertices from and to which are true members of the vertices map
     * (i.e. by true object instance equality == and not just by identity equality from the getId() method)
     * 3.  all edges of a vertex are outgoing edges, i.e. FOR ALL e in v.edges: e.from == v
     **/

    public DirectedGraph() {
    }

    public Collection<V> getVertices() {
        return this.vertices.values();
    }

    /**
     * finds the vertex in the graph identified by the given id
     *
     * @param id the id of the vertex is set as the key in the map
     * @return the vertex that matches the given id
     * return null if none of the vertices matches the id
     */
    public V getVertexById(String id) {
        return this.vertices.get(id);
    }


    /**
     * Adds newVertex to the graph, if not yet present and in a way that maintains the representation invariants.
     * If (a duplicate of) newVertex (with the same id) already exists in the graph,
     * nothing will be added, and the existing duplicate will be kept and returned.
     *
     * @param newVertex the vertex to add or return
     * @return the duplicate of newVertex with the same id that already existed in the graph,
     * or newVertex itself if it has been added.
     */
    public V addOrGetVertex(V newVertex) {
        // add and return the newVertex, or return the existing duplicate vertex
        if (this.getVertexById(newVertex.getId()) == null) {
            this.vertices.put(newVertex.getId(), newVertex);
        }
        // a proper vertex shall be returned at all times
        return this.getVertexById(newVertex.getId());
    }

    /**
     * Adds all newVertices to the graph, which are not present yet and and in a way that maintains the representation invariants.
     *
     * @param newVertices an array of vertices to be added, provided as variable length argument list
     * @return the number of vertices that actually have been added.
     */
    public int addVertices(V... newVertices) {
        int count = 0;
        for (V v : newVertices) {
            if (v == this.addOrGetVertex(v)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Adds newEdge to the graph, if not yet present and in a way that maintains the representation invariants:
     * If any of the newEdge.from or newEdge.to vertices does not yet exist in the graph, it is added now.
     * If newEdge does not exist yet in the edges list of the newEdge.from vertex, it is added now,
     * otherwise no change is made to that list.
     *
     * @param newEdge the new edge to be added in the edges list of newEdge.from
     * @return the duplicate of newEdge that already existed in the graph
     * or newEdge selves if it just has been added.
     * @throws IllegalArgumentException if newEdge.from or newEdge.to are duplicate vertices that have not
     *                                  been added to the graph yet have the same id as another vertex in the graph
     */
    public E addOrGetEdge(E newEdge) {
        // add and return the newEdge, or return the existing duplicate edge or throw an exception
        if (newEdge.getFrom() != null && vertices.containsKey(newEdge.getFrom().getId()) && getVertexById(newEdge.getFrom().getId()) != newEdge.getFrom()
        ) {
            throw new IllegalArgumentException();
        }
        if (newEdge.getTo() != null && vertices.containsKey(newEdge.getTo().getId()) && getVertexById(newEdge.getTo().getId()) != newEdge.getTo()
        ) {
            throw new IllegalArgumentException();
        }
        if (newEdge.getFrom().getEdges().stream().anyMatch(e -> e.equals(newEdge))){
            return newEdge.getFrom().getEdges().stream().filter(e -> e.equals(newEdge)).findAny().get();
        }
        // add edges to vertex
        this.addOrGetVertex(newEdge.getFrom()).getEdges().add(newEdge);
        this.addOrGetVertex(newEdge.getTo());
        // a proper edge shall be returned at all times
        return newEdge;
    }

    /**
     * Adds all newEdges to the graph, which are not present yet and in a way that maintains the representation invariants.
     *
     * @param newEdges an array of vertices to be added, provides as variable length argument list
     * @return the number of edges that actually have been added.
     */
    public int addEdges(E... newEdges) {
        int count = 0;
        for (E e : newEdges) {
            if (e == this.addOrGetEdge(e)) {
                count++;
            }
        }

        return count;
    }

    /**
     * @return the total number of vertices in the graph
     */
    public int getNumVertices() {
        return this.vertices.size();
    }

    /**
     * @return the total number of edges in the graph
     */
    public int getNumEdges() {
        // calculate and return the total number of edges in the graph
        return this.vertices.values()
                .stream().mapToInt(v -> v.getEdges().size())
                .sum();
    }

    /**
     * Clean-up unconnected vertices in the graph
     */
    public void removeUnconnectedVertices() {
        Set<V> unconnected = new HashSet<>();
        this.getVertices().stream().filter(v -> v.getEdges().isEmpty()).forEach(unconnected::add);
        this.getVertices().stream().flatMap(v -> v.getEdges().stream().map(E::getTo)).forEach(unconnected::remove);
        unconnected.stream().map(V::getId).forEach(this.vertices::remove);
    }

    /**
     * represents a path of connected vertices and edges in the graph
     */
    public class DGPath {
        private V start = null;
        private LinkedList<E> edges = new LinkedList<>();
        private double totalWeight = 0.0;
        private Set<V> visited = new HashSet<>();

        /**
         * representation invariants:
         * 1. The edges are connected by vertices, i.e. FOR ALL i: 0 < i < edges.length: edges[i].from == edges[i-1].to
         * 2. The path begins at vertex == start
         * 3. if edges is empty, the path also ends at vertex == start
         * otherwise edges[0].from == start and the path continues along edges[i].to for all 0 <= i < edges.length
         **/

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(
                    String.format("Weight=%f Length=%d Visited=%d (",
                            this.totalWeight, 1 + this.edges.size(), this.visited.size()));
            sb.append(start.getId());
            for (E e : edges) {
                sb.append(", " + e.getTo().getId());
            }
            sb.append(")");
            return sb.toString();
        }

        public V getStart() {
            return start;
        }

        public LinkedList<E> getEdges() {
            return edges;
        }

        public double getTotalWeight() {
            return totalWeight;
        }

        public Set<V> getVisited() {
            return visited;
        }

        public void setTotalWeight(double add) {
            this.totalWeight += add;
        }

        public void setEdges(LinkedList<E> edges) { this.edges = edges; }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the start vertex to the target vertex in the graph
     * The path.totalWeight should indicate the number of edges in the result path
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startId the id of the start Vertex
     * @param targetId the id of the end Vertex
     * @return the DGpath for the path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath depthFirstSearch(String startId, String targetId) {
        // get node/vertex on ID
        V start = this.getVertexById(startId);
        V target = this.getVertexById(targetId);
        // if they dont exist return null
        if (start == null || target == null) return null;
        // create new path
        DGPath path = new DGPath();
        // set starting node/vertex
        path.start = start;

        // easy target
        if (start == target) {
            path.visited.add(start);
            return path;
        }

        // return the recursive method with starting point, target and the path
        return dfsRecursive(path.start, target, path);
    }

    private DGPath dfsRecursive(V current, V target, DGPath path) {
        // if node/vertex is already visited we have no path
        if (path.getVisited().contains(current)) {
            return null;
        }
        // add current node/vertex to visited set
        path.getVisited().add(current);
        // if we reach the destination we return the path
        if (current.equals(target)) {
            return path;
        }
        // else we go check all de edges connected to the vertex
        for (E edge : current.getEdges()){
            // if there is a new path we continue recursive until destination is reached
            if (dfsRecursive(edge.getTo(), target, path) != null) {
                // we add the edges as first in the set of edges to create the path
                path.getEdges().addFirst(edge);
                return path;
            }
        }
        return null;
    }



    /**
     * Uses a breadth-first search algorithm to find a path from the start vertex to the target vertex in the graph
     * The path.totalWeight should indicate the number of edges in the result path
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startId
     * @param targetId
     * @return the path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath breadthFirstSearch(String startId, String targetId) {
    // TODO add comments
        V start = this.getVertexById(startId);
        V target = this.getVertexById(targetId);
        if (start == null || target == null) return null;

        DGPath path = new DGPath();
        path.start = start;
        path.visited.add(start);

        // easy target
        if (start == target) return path;

        // calculate the path from start to target by breadth-first-search
        //  register all visited vertices while going, for statistical purposes
        //  if you hit the target: complete the path and bail out !!!
        Queue<V> fifoQueue = new LinkedList<>();
        Map<V, V> visitedFrom = new HashMap<>();
        Map<V, E> visitedEdge = new HashMap<>();

        fifoQueue.offer(start);
        visitedFrom.put(start, null);

        while (!fifoQueue.isEmpty()) {

            V current = fifoQueue.poll();
            for (E e : current.getEdges()) {

                visitedEdge.put(e.getFrom(), e);
                V neighbour = e.getTo();
                path.visited.add(neighbour);

                if (neighbour == target) {

                    while (current != null) {
                        path.getEdges().addFirst(visitedEdge.get(current));
                        current = visitedFrom.get(current);

                    }
                    return path;
                } else if (!visitedFrom.containsKey(neighbour)) {
                    visitedFrom.put(neighbour,current);
                    fifoQueue.offer(neighbour);
                }
            }
        }
        // no path found, graph was not connected ???
        return null;
    }

    // helper class to register the state of a vertex in dijkstra shortest path algorithm
    // your may change this class or delete it altogether follow a different approach in your implementation
    private class DSPNode implements Comparable<DSPNode> {
        public V vertex;                // the graph vertex that is concerned with this DSPNode
        public E fromEdge = null;        // the edge from the predecessor's vertex to this node's vertex
        public boolean marked = false;  // indicates DSP processing has been marked complete
        public double weightSumTo = Double.MAX_VALUE;   // sum of weights of current shortest path to this node's vertex
        public DSPNode predecessor = null;

        public DSPNode(V vertex) {
            this.vertex = vertex;
        }

        // comparable interface helps to find a node with the shortest current path, so far
        @Override
        public int compareTo(DSPNode dspv) {
            return Double.compare(this.weightSumTo, dspv.weightSumTo);
        }
    }

    /**
     * Calculates the edge-weighted shortest path from start to target
     * Uses a minimum distance heuristic from any vertex to the target
     * in order to reduce the number of visited vertices during the search
     *
     * @param startId
     * @param targetId
     * @param weightMapper provides a function, by which the weight of an edge can be retrieved or calculated
     * @return the shortest path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath dijkstraShortestPath(String startId, String targetId, Function<E, Double> weightMapper) {
        V start = this.getVertexById(startId);
        V target = this.getVertexById(targetId);
        Map<V, DSPNode> progressData = new HashMap<>();
        PriorityQueue<DSPNode> pqueue = new PriorityQueue<>();
        Set<V> shortestPathFound = new HashSet<>();


        if (start == null || target == null) return null;

        // initialise the result path of the search
        DGPath path = new DGPath();
        path.start = start;
        path.visited.add(start);

        // easy target
        if (start == target) return path;


        // initialise the progress of the start node
        DSPNode nextDspNode = new DSPNode(start);
        nextDspNode.weightSumTo = 0.0;
        progressData.put(start, nextDspNode);

        pqueue.add(nextDspNode);

        while (!pqueue.isEmpty()) {
            DSPNode dspNode = pqueue.poll();
            V node = dspNode.vertex;
            shortestPathFound.add(node);
            path.getVisited().add(node);

            if (node.equals(target)){
                path.setEdges(buildDijkstraPath(dspNode));
                path.setTotalWeight(dspNode.weightSumTo);
                System.out.println(path.getEdges());
                return path;
            }

            // iterate over neighbors
            Set<E> neighbors = node.getEdges();
            for (E edge : neighbors) {
                if (shortestPathFound.contains(edge.getTo())) {
                    continue;
                }

                double distance = weightMapper.apply(edge);
                double totalDistance = dspNode.weightSumTo + distance;

                // neighbor not discovered yet?
                DSPNode dspNext = progressData.get(edge.getTo());
                if (dspNext == null) {
                    dspNext = new DSPNode(edge.getTo());
                    dspNext.weightSumTo = totalDistance;
                    dspNext.fromEdge = edge;
                    dspNext.predecessor = dspNode;
                    progressData.put(edge.getTo(), dspNext);
                    pqueue.add(dspNext);
                }

                else if (totalDistance < dspNext.weightSumTo) {
                    dspNext.weightSumTo = totalDistance;
                    dspNext.predecessor = dspNode;

                    // update queue
                    pqueue.remove(dspNext);
                    pqueue.add(dspNext);
                }

            }
        }

        // no path found, graph was not connected ???
        return null;
    }

    private LinkedList<E> buildDijkstraPath(DSPNode dspNode) {
        LinkedList<E> path = new LinkedList<>();
        while (dspNode != null) {
            if (dspNode.predecessor != null) {
                path.addFirst(dspNode.fromEdge);
            }
            dspNode = dspNode.predecessor;
        }
        return path;
    }

    // helper class to register the state of a vertex in A* shortest path algorithm
    private class ASNode extends DSPNode {
        // TODO add and handle information for the minimumWeightEstimator

        // TODO enhance this constructor as required
        private ASNode(V vertex) {
            super(vertex);
        }

        // TODO override the compareTo
    }


    /**
     * Calculates the edge-weighted shortest path from start to target
     * Uses a minimum distance heuristic from any vertex to the target
     * in order to reduce the number of visited vertices during the search
     *
     * @param startId
     * @param targetId
     * @param weightMapper           provides a function, by which the weight of an edge can be retrieved or calculated
     * @param minimumWeightEstimator provides a function, by which a lower bound of the cumulative weight
     *                               between two vertices can be calculated.
     * @return the shortest path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath aStarShortestPath(String startId, String targetId,
                                    Function<E, Double> weightMapper,
                                    BiFunction<V, V, Double> minimumWeightEstimator) {

        V start = this.getVertexById(startId);
        V target = this.getVertexById(targetId);
        if (start == null || target == null) return null;

        DGPath path = new DGPath();
        path.start = start;
        path.visited.add(start);

        // easy target
        if (start == target) return path;

        // TODO apply the A* algorithm to find shortest path from start to target.
        //  take dijkstra's solution as the starting point and enhance with heuristic functionality
        //  register all visited vertices while going, for statistical purposes


        // TODO END
        // no path found, graph was not connected ???
        return null;
    }

    /**
     * Calculates the edge-weighted shortest path from start to target
     *
     * @param startId
     * @param targetId
     * @param weightMapper provides a function by which the weight of an edge can be retrieved or calculated
     * @return the shortest path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath dijkstraShortestPathByAStar(String startId, String targetId,
                                              Function<E, Double> weightMapper) {
        return aStarShortestPath(startId, targetId,
                weightMapper,
                // TODO provide a minimumWeightEstimator that makes A* run like regular Dijkstra
                null
        );
    }

    @Override
    public String toString() {
        return this.getVertices().stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n  ", "{ ", "\n}"));
    }
}
