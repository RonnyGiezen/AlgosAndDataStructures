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
        if (newEdge.getFrom().getEdges().stream().anyMatch(e -> e.equals(newEdge))) {
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

        public void setEdges(LinkedList<E> edges) {
            this.edges = edges;
        }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the start vertex to the target vertex in the graph
     * The path.totalWeight should indicate the number of edges in the result path
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startId  the id of the start Vertex
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
        for (E edge : current.getEdges()) {
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
     * @param startId  start node to get path from
     * @param targetId end node to get path to
     * @return the path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath breadthFirstSearch(String startId, String targetId) {
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

        // list of nodes to visit next
        Queue<V> nextToVisit = new LinkedList<>();
        // map of each nodes and where they are visited from
        Map<V, V> visitedFrom = new HashMap<>();
        // each node and from wich edge it has been visited
        Map<V, E> visitedEdge = new HashMap<>();
        // add the start node to visit next (to start)
        nextToVisit.offer(start);
        // visited from is null since its the first
        visitedFrom.put(start, null);
        // set visitedEdge
        visitedEdge.put(start, null);


        // while there are nodes to visit do something
        while (!nextToVisit.isEmpty()) {
            // get current node from to visit
            V current = nextToVisit.poll();

            // if current is target
            if (current == target) {
                // build path
                while (current != null) {
                    if (visitedEdge.get(current) != null) {
                        path.getEdges().addFirst(visitedEdge.get(current));
                    }
                    current = visitedFrom.get(current);

                }
                return path;
            }
            // for each next node do something
            for (E e : current.getEdges()) {
                // add this edge to visited

                // get neighbour
                V neighbour = e.getTo();
                // add to visited list
                path.visited.add(neighbour);
                // if the neighbour is not visited
                if (!visitedFrom.containsKey(neighbour)) {
                    // put it in the map for visited from
                    visitedFrom.put(neighbour, current);
                    // put in the map for visited from edge
                    visitedEdge.put(neighbour, e);
                    // add to next to visit
                    nextToVisit.offer(neighbour);
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
        public double weightSumTo = Double.MAX_VALUE;   // sum of weights of current shortest path to this node's vertex
        public DSPNode predecessor = null; // the predecessor of this node
        public double cost = this.weightSumTo; // set the cost for this node so we can compare on cost (helps for Astar)

        public DSPNode(V vertex) {
            this.vertex = vertex;
        }

        // comparable interface helps to find a node with the shortest current path, so far
        @Override
        public int compareTo(DSPNode dspv) {
            return Double.compare(this.cost, dspv.cost);
        }
    }

    /**
     * Calculates the edge-weighted shortest path from start to target
     * Uses a minimum distance heuristic from any vertex to the target
     * in order to reduce the number of visited vertices during the search
     *
     * @param startId      node to start with
     * @param targetId     node for when to stop and find the path for
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

        // if target or start is null we can't calculate shortest path
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
        // add start node to the queue
        pqueue.add(nextDspNode);

        // while the queue is not empty we can calculate the path
        while (!pqueue.isEmpty()) {
            // get the first node from the queue
            DSPNode dspNode = pqueue.poll();
            // get the Vertex from that queue
            V vertex = dspNode.vertex;
            // add the Vertex tot he "shortest path" for now
            shortestPathFound.add(vertex);
            // add to visited for stats
            path.getVisited().add(vertex);
            // if we have the target we can stop
            if (vertex.equals(target)) {
                // set the edges (path) in the path helper class with builder method
                path.setEdges(buildPath(dspNode));
                // set the total weight
                path.setTotalWeight(dspNode.weightSumTo);
                return path;
            }

            // iterate over neighbors
            // heretofore we need all the edges of the vertex to process them
            Set<E> neighbors = vertex.getEdges();
            // for each edge that points to a next node
            for (E edge : neighbors) {
                // if the vertex that the edge points to is already in shortestPath we dont have
                // to do it again.
                if (shortestPathFound.contains(edge.getTo())) {
                    continue;
                }
                // get the distance of the edge
                double distance = weightMapper.apply(edge);
                // get the total distance from the distance to this node + the distance of this edge
                double totalDistance = dspNode.weightSumTo + distance;

                // neighbor not discovered/progressed yet?
                DSPNode dspNext = progressData.get(edge.getTo());
                if (dspNext == null) {
                    // create a new node for the neighbor
                    dspNext = new DSPNode(edge.getTo());
                    // set the weight to this node
                    dspNext.weightSumTo = totalDistance;
                    // set the edge this node came from
                    dspNext.fromEdge = edge;
                    // set previous node
                    dspNext.predecessor = dspNode;
                    // add to progressed
                    progressData.put(edge.getTo(), dspNext);
                    // add to the queue
                    pqueue.add(dspNext);
                }
                // otherwise we check if the weight to is shorter/smaller
                else if (totalDistance < dspNext.weightSumTo) {
                    // set new weight
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

    /**
     * Build a path to the param node
     *
     * @param dspNode the final node where we want to build the path to
     * @return the path to this node
     */
    private LinkedList<E> buildPath(DSPNode dspNode) {
        // create a new linked list for this path
        LinkedList<E> path = new LinkedList<>();
        // while the node is not null we trace back to start
        // while adding the node to the list
        while (dspNode != null) {
            if (dspNode.predecessor != null) {
                path.addFirst(dspNode.fromEdge);
            }
            dspNode = dspNode.predecessor;
        }
        // when all back tracking is done we can return the path
        return path;
    }

    // helper class to register the state of a vertex in A* shortest path algorithm
    private class ASNode extends DSPNode {
        // add and handle information for the minimumWeightEstimator
        private double estimatedCostToTarget; // the total estimated cost from this to the target

        // enhance this constructor as required
        public ASNode(V vertex) {
            super(vertex);
            this.estimatedCostToTarget = Double.POSITIVE_INFINITY;
            calculateCostSum();
        }

        // calculate total cost with the heuristic
        // cost for a start is the cost is had plus the estimated cost left to the target
        private void calculateCostSum() {
            this.cost = this.weightSumTo + this.estimatedCostToTarget;
        }

        // override the compareTo
        @Override
        public int compareTo(DSPNode dspv) {
            return super.compareTo(dspv);
        }
    }


    /**
     * Calculates the edge-weighted shortest path from start to target
     * Uses a minimum distance heuristic from any vertex to the target
     * in order to reduce the number of visited vertices during the search
     *
     * @param startId                id of the node tot start with
     * @param targetId               if of the target we are trying to find the path for
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
        Map<V, ASNode> progressData = new HashMap<>(); // hashmap for stats
        PriorityQueue<ASNode> pqueue = new PriorityQueue<>(); // que that sorts the nodes on cost
        Set<V> shortestASPathFound = new HashSet<>(); // set to keep track of the current shortest path

        // if the start of target are null we cant calculate path
        if (start == null || target == null) return null;
        // set start node
        DGPath path = new DGPath();
        path.start = start;
        path.visited.add(start);


        // add start to the queue
        // initialise the progress of the start node
        ASNode nextASNode = new ASNode(start);
        nextASNode.weightSumTo = 0.0;
        // set the heuristic
        nextASNode.estimatedCostToTarget = minimumWeightEstimator.apply(start, target);
        progressData.put(start, nextASNode);
        // add start node to the queue
        pqueue.add(nextASNode);

        // easy target
        if (start == target) return path;

        // while the queue is not empty we can calculate the path
        while (!pqueue.isEmpty()) {
            // get the first node from the queue
            ASNode asNode = pqueue.poll();
            // get the Vertex from that queue
            V vertex = asNode.vertex;
            // add the Vertex tot he "shortest path" for now
            shortestASPathFound.add(vertex);
            // add to visited for stats
            path.getVisited().add(vertex);
            // if we have the target we can stop
            if (vertex.equals(target)) {
                // set the edges (path) in the path helper with builder method
                path.setEdges(buildPath(asNode));
                // set the total weight
                path.setTotalWeight(asNode.weightSumTo);
                return path;
            }

            // iterate over neighbors
            // heretofore we need all the edges of the vertex
            Set<E> neighbors = vertex.getEdges();
            for (E edge : neighbors) {
                // if the vertex that the edge points to is already in shortestPath we dont have
                // to do it again.
                if (shortestASPathFound.contains(edge.getTo())) {
                    continue;
                }
                // get the distance of the edge
                double distance = weightMapper.apply(edge);
                // get the total distance from the distance to this node + the distance of this edge
                double totalDistance = asNode.weightSumTo + distance;

                // neighbor not discovered/progressed yet?
                ASNode asNext = progressData.get(edge.getTo());
                if (asNext == null) {
                    // create a new node for the neighbor
                    asNext = new ASNode(edge.getTo());
                    // set the weigth to this node
                    asNext.weightSumTo = totalDistance;
                    // set the edge this node came from
                    asNext.fromEdge = edge;
                    // set previous node
                    asNext.predecessor = asNode;
                    // set heuristic
                    asNext.estimatedCostToTarget = minimumWeightEstimator.apply(edge.getTo(), target);
                    // add to progressed
                    progressData.put(edge.getTo(), asNext);
                    // add to the queue
                    pqueue.add(asNext);
                }
                // otherwise we check if the cost is shorter/smaller
                else if (totalDistance < asNext.weightSumTo) {
                    // set new cost
                    asNext.weightSumTo = totalDistance;
                    asNext.predecessor = asNode;

                    // update queue
                    pqueue.remove(asNext);
                    pqueue.add(asNext);
                }
            }
        }
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
                // a minimumWeightEstimator that makes A* run like regular Dijkstra
                (v1, v2) -> depthFirstSearch(v1.getId(), v2.getId()).getTotalWeight()
        );
    }

    @Override
    public String toString() {
        return this.getVertices().stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n  ", "{ ", "\n}"));
    }
}
