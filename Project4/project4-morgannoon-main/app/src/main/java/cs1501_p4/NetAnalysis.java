/** NetAnalysis class utilizes NetAnalysis_Inter and MinPQ
 * @author    Morgan Noonan
 */
package cs1501_p4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class NetAnalysis implements NetAnalysis_Inter {
    private static final int COPPER_SPEED = 230000000;
    private static final int OPTICAL_SPEED = 200000000;
    private ArrayList<ArrayList<Edge>> adjacencyList; // Adjacency list representation of the graph
    private int numVertices;

    public NetAnalysis(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            numVertices = Integer.parseInt(br.readLine().trim());
            adjacencyList = new ArrayList<>(numVertices);
            for (int i = 0; i < numVertices; i++) {
                adjacencyList.add(new ArrayList<>());
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                int u = Integer.parseInt(tokens[0]);
                int v = Integer.parseInt(tokens[1]);
                String cableType = tokens[2];
                int bandwidth = Integer.parseInt(tokens[3]);
                int length = Integer.parseInt(tokens[4]);
                // Add edges to the adjacency list (undirected)
                adjacencyList.get(u).add(new Edge(v, u, cableType, bandwidth, length));
                adjacencyList.get(v).add(new Edge(u, v, cableType, bandwidth, length));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Integer> lowestLatencyPath(int u, int w) {
        double[] time = new double[numVertices];
        boolean[] visited = new boolean[numVertices];
        int[] prev = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            time[i] = Double.MAX_VALUE;
            visited[i] = false;
            prev[i] = -1;
        }
        time[u] = 0;
        for (int i = 0; i < numVertices; i++) {
            int minVertex = -1;
            double minTime = Double.MAX_VALUE;
            for (int j = 0; j < numVertices; j++) {
                if (!visited[j] && time[j] < minTime) {
                    minVertex = j;
                    minTime = time[j];
                }
            }
            visited[minVertex] = true;
            if (minVertex == w)
                break;
            ArrayList<Edge> edges = adjacencyList.get(minVertex);
            for (int k = 0; k < edges.size(); k++) {
                Edge edge = edges.get(k);
                int neighbor = edge.destination;
                if (!visited[neighbor]) {
                    double edgeTime = calculateTime(edge);
                    if (time[minVertex] + edgeTime < time[neighbor]) {
                        time[neighbor] = time[minVertex] + edgeTime;
                        prev[neighbor] = minVertex;
                    }
                }
            }
        }
        ArrayList<Integer> path = new ArrayList<>();
        for (int at = w; at != -1; at = prev[at]) {
            path.add(at);
        }
        // if path is empty or starting vertex is incorrect
        if (path.isEmpty() || path.get(path.size() - 1) != u) {
            return null;
        }
        // Reverse the path
        Collections.reverse(path);
        return path;
    }

    // calculate time based on cable type and length
    private double calculateTime(Edge edge) {
        double speed;
        if (edge.cableType.equals("copper")) {
            speed = COPPER_SPEED;
        } else {
            speed = OPTICAL_SPEED;
        }
        double time = edge.length / speed;
        return time;
    }

    @Override
    public int bandwidthAlongPath(ArrayList<Integer> p) throws IllegalArgumentException {
        // Check if the path is valid
        if (!isValidPath(p)) {
            throw new IllegalArgumentException("Specified path is not valid for the graph.");
        }
        int minBandwidth = Integer.MAX_VALUE;
        for (int i = 0; i < p.size() - 1; i++) {
            int u = p.get(i);
            int v = p.get(i + 1);
            int edgeBandwidth = getEdgeBandwidth(u, v);
            minBandwidth = Math.min(minBandwidth, edgeBandwidth);
        }
        return minBandwidth;
    }

    // if the path is valid for the graph
    private boolean isValidPath(ArrayList<Integer> p) {
        if (p == null || p.size() < 2) {
            return false;
        }
        for (int i = 0; i < p.size() - 1; i++) {
            int u = p.get(i);
            int v = p.get(i + 1);
            if (!isValidEdge(u, v)) {
                return false;
            }
        }
        return true;
    }

    // if the edge between vertices u and v exists
    private boolean isValidEdge(int u, int v) {
        ArrayList<Edge> edges = adjacencyList.get(u);
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            if (edge.destination == v) {
                return true;
            }
        }
        return false;
    }

    // get bandwidth of the edge between vertices u and v
    private int getEdgeBandwidth(int u, int v) {
        int b = 0;
        ArrayList<Edge> edges = adjacencyList.get(u);
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            if (edge.destination == v) {
                b = edge.bandwidth;
            }
        }
        return b;
    }

    @Override
    public boolean copperOnlyConnected() {
        // Find the index of the first vertex with copper links
        int startVertex = findCopperStartVertex();
        // If no vertex has copper links, return false
        if (startVertex == -1) {
            return false;
        }
        // dfs considering only copper links
        boolean[] visited = new boolean[numVertices];
        dfsCopperOnly(startVertex, visited);
        // if all vertices with copper links were visited
        for (int i = 0; i < numVertices; i++) {
            if (!visited[i] && hasCopperLinks(i)) {
                return false; // There's an unvisited vertex with copper links
            }
        }
        return true; // All vertices with copper links were visited
    }

    // dfs considering only copper links
    private void dfsCopperOnly(int vertex, boolean[] visited) {
        visited[vertex] = true;
        ArrayList<Edge> edges = adjacencyList.get(vertex);
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            if (edge.cableType.equals("copper") && !visited[edge.destination]) {
                dfsCopperOnly(edge.destination, visited);
            }
        }
    }

    // check if a vertex has copper links
    private boolean hasCopperLinks(int vertex) {
        ArrayList<Edge> edges = adjacencyList.get(vertex);
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            if (edge.cableType.equals("copper")) {
                return true;
            }
        }
        return false;
    }

    // find the index of the first vertex with copper links
    private int findCopperStartVertex() {
        for (int i = 0; i < numVertices; i++) {
            if (hasCopperLinks(i)) {
                return i; // Return the index of the first vertex with copper links
            }
        }
        return -1; // Return -1 if no vertex has copper links
    }

    @Override
    public boolean connectedTwoVertFail() {
        for (int i = 0; i < numVertices; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                // mark the vertices as failed
                boolean[] failedVertices = new boolean[numVertices];
                failedVertices[i] = true;
                failedVertices[j] = true;
                // Check if still connected
                if (!isGraphConnectedWithFailures(failedVertices)) {
                    return false; // disconnected after the vertices fail
                }
            }
        }
        return true; // connected for any two failed vertices
    }

    // check if the graph with failed vertices is connected
    private boolean isGraphConnectedWithFailures(boolean[] failedVertices) {
        boolean[] visited = new boolean[numVertices];
        for (int i = 0; i < numVertices; i++) {
            if (!failedVertices[i]) {
                dfs(i, visited, failedVertices);
                break;
            }
        }
        // Check if all non-failed vertices are visited
        for (int i = 0; i < numVertices; i++) {
            if (!failedVertices[i] && !visited[i]) {
                return false; // The graph is disconnected
            }
        }
        return true; // The graph is connected
    }

    private void dfs(int vertex, boolean[] visited, boolean[] failedVertices) {
        visited[vertex] = true;
        ArrayList<Edge> edges = adjacencyList.get(vertex);
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            int neighbor = edge.destination;
            if (!failedVertices[neighbor] && !visited[neighbor]) {
                dfs(neighbor, visited, failedVertices);
            }
        }
    }

    @Override
    public ArrayList<STE> lowestAvgLatST() {
        ArrayList<ArrayList<Edge>> minSpanningTree = new ArrayList<>();
        // Check if the graph is connected
        if (!connectedTwoVertFail()) {
            return null;
        }
        lazyPrim(minSpanningTree);
        ArrayList<STE> minSpanningTreeEdges = new ArrayList<>();
        for (int u = 0; u < minSpanningTree.size(); u++) {
            ArrayList<Edge> edges = minSpanningTree.get(u);
            for (Edge edge : edges) {
                int v = edge.destination;
                // Add the edge only if it doesn't already contain it in either direction (undirected)
                if (!containsEdge(minSpanningTreeEdges, u, v) && !containsEdge(minSpanningTreeEdges, v, u)) {
                    minSpanningTreeEdges.add(new STE(u, v));
                }
            }
        }
        return minSpanningTreeEdges;
    }

    private void lazyPrim(ArrayList<ArrayList<Edge>> minSpanningTree) {
        boolean[] inMST = new boolean[numVertices];
        MinPQ<Edge> pq = new MinPQ<>(new Comparator<Edge>() {
            public int compare(Edge e1, Edge e2) {
                return Double.compare(calculateTime(e1), calculateTime(e2));
            }
        });
        // Start from 0
        visit(0, inMST, pq);
        while (!pq.isEmpty()) {
            Edge edge = pq.delMin(); // Get the minimum weight edge
            int u = edge.source;
            int v = edge.destination;
            // Skip this edge if both vertices are already in MST
            if (inMST[u] && inMST[v])
                continue;
            addToMST(minSpanningTree, edge, u);
            if (!inMST[u])
                visit(u, inMST, pq);
            if (!inMST[v])
                visit(v, inMST, pq);
        }
    }

    private void addToMST(ArrayList<ArrayList<Edge>> minSpanningTree, Edge edge, int vertex) {
        if (vertex >= minSpanningTree.size()) {
            while (vertex >= minSpanningTree.size()) {
                minSpanningTree.add(new ArrayList<>());
            }
        }
        minSpanningTree.get(vertex).add(edge);
    }

    public void getMinimumSpanningTree(ArrayList<ArrayList<Edge>> minSpanningTree) {
        lazyPrim(minSpanningTree);
    }

    private void visit(int vertex, boolean[] inMST, MinPQ<Edge> pq) {
        inMST[vertex] = true;
        ArrayList<Edge> edges = adjacencyList.get(vertex);
        for (Edge edge : edges) {
            if (!inMST[edge.destination]) {
                pq.insert(edge);
            }
        }
    }

    private boolean containsEdge(ArrayList<STE> edges, int u, int v) {
        for (STE edge : edges) {
            // if the edge contains vertices u and v in either direction
            if ((edge.u == u && edge.w == v) || (edge.u == v && edge.w == u)) {
                return true; // Edge already exists
            }
        }
        return false; // Edge does not exist
    }

    /************************************************************ */
    private class Edge {
        private String cableType;
        private int bandwidth;
        private int length;
        private int destination;
        private int source;

        private Edge(int destination, int source, String cableType, int bandwidth, int length) {
            this.cableType = cableType;
            this.bandwidth = bandwidth;
            this.length = length;
            this.destination = destination;
            this.source = source;
        }
    }

    /************************************************************* */
    public class MinPQ<Key> implements Iterable<Key> {
        private Key[] pq; // store items at indices 1 to n
        private int n; // number of items on priority queue
        private Comparator<Key> comparator; // optional comparator

        /**
         * Initializes an empty priority queue with the given initial capacity.
         *
         * @param initCapacity the initial capacity of this priority queue
         */
        @SuppressWarnings("unchecked")
        public MinPQ(int initCapacity) {
            pq = (Key[]) new Object[initCapacity + 1];
            n = 0;
        }

        /**
         * Initializes an empty priority queue.
         */
        public MinPQ() {
            this(1);
        }

        /**
         * Initializes an empty priority queue with the given initial capacity,
         * using the given comparator.
         *
         * @param initCapacity the initial capacity of this priority queue
         * @param comparator   the order in which to compare the keys
         */
        @SuppressWarnings("unchecked")
        public MinPQ(int initCapacity, Comparator<Key> comparator) {
            this.comparator = comparator;
            pq = (Key[]) new Object[initCapacity + 1];
            n = 0;
        }

        /**
         * Initializes an empty priority queue using the given comparator.
         *
         * @param comparator the order in which to compare the keys
         */
        public MinPQ(Comparator<Key> comparator) {
            this(1, comparator);
        }

        /**
         * Initializes a priority queue from the array of keys.
         * <p>
         * Takes time proportional to the number of keys, using sink-based heap
         * construction.
         *
         * @param keys the array of keys
         */
        @SuppressWarnings("unchecked")
        public MinPQ(Key[] keys) {
            n = keys.length;
            pq = (Key[]) new Object[keys.length + 1];
            for (int i = 0; i < n; i++)
                pq[i + 1] = keys[i];
            for (int k = n / 2; k >= 1; k--)
                sink(k);
            assert isMinHeap();
        }

        /**
         * Returns true if this priority queue is empty.
         *
         * @return {@code true} if this priority queue is empty;
         *         {@code false} otherwise
         */
        public boolean isEmpty() {
            return n == 0;
        }

        /**
         * Returns the number of keys on this priority queue.
         *
         * @return the number of keys on this priority queue
         */
        public int size() {
            return n;
        }

        /**
         * Returns a smallest key on this priority queue.
         *
         * @return a smallest key on this priority queue
         * @throws NoSuchElementException if this priority queue is empty
         */
        public Key min() {
            if (isEmpty())
                throw new NoSuchElementException("Priority queue underflow");
            return pq[1];
        }

        // resize the underlying array to have the given capacity
        private void resize(int capacity) {
            assert capacity > n;
            @SuppressWarnings("unchecked")
            Key[] temp = (Key[]) new Object[capacity];
            for (int i = 1; i <= n; i++) {
                temp[i] = pq[i];
            }
            pq = temp;
        }

        /**
         * Adds a new key to this priority queue.
         *
         * @param x the key to add to this priority queue
         */
        public void insert(Key x) {
            // double size of array if necessary
            if (n == pq.length - 1)
                resize(2 * pq.length);

            // add x, and percolate it up to maintain heap invariant
            pq[++n] = x;
            swim(n);
            assert isMinHeap();
        }

        /**
         * Removes and returns a smallest key on this priority queue.
         *
         * @return a smallest key on this priority queue
         * @throws NoSuchElementException if this priority queue is empty
         */
        public Key delMin() {
            if (isEmpty())
                throw new NoSuchElementException("Priority queue underflow");
            Key min = pq[1];
            exch(1, n--);
            sink(1);
            pq[n + 1] = null; // to avoid loitering and help with garbage collection
            if ((n > 0) && (n == (pq.length - 1) / 4))
                resize(pq.length / 2);
            assert isMinHeap();
            return min;
        }

        /***************************************************************************
         * Helper functions to restore the heap invariant.
         ***************************************************************************/

        private void swim(int k) {
            while (k > 1 && greater(k / 2, k)) {
                exch(k / 2, k);
                k = k / 2;
            }
        }

        private void sink(int k) {
            while (2 * k <= n) {
                int j = 2 * k;
                if (j < n && greater(j, j + 1))
                    j++;
                if (!greater(k, j))
                    break;
                exch(k, j);
                k = j;
            }
        }

        /***************************************************************************
         * Helper functions for compares and swaps.
         ***************************************************************************/
        @SuppressWarnings("unchecked")
        private boolean greater(int i, int j) {
            if (comparator == null) {
                return ((Comparable<Key>) pq[i]).compareTo(pq[j]) > 0;
            } else {
                return comparator.compare(pq[i], pq[j]) > 0;
            }
        }

        private void exch(int i, int j) {
            Key swap = pq[i];
            pq[i] = pq[j];
            pq[j] = swap;
        }

        // is pq[1..n] a min heap?
        private boolean isMinHeap() {
            for (int i = 1; i <= n; i++) {
                if (pq[i] == null)
                    return false;
            }
            for (int i = n + 1; i < pq.length; i++) {
                if (pq[i] != null)
                    return false;
            }
            if (pq[0] != null)
                return false;
            return isMinHeapOrdered(1);
        }

        // is subtree of pq[1..n] rooted at k a min heap?
        private boolean isMinHeapOrdered(int k) {
            if (k > n)
                return true;
            int left = 2 * k;
            int right = 2 * k + 1;
            if (left <= n && greater(k, left))
                return false;
            if (right <= n && greater(k, right))
                return false;
            return isMinHeapOrdered(left) && isMinHeapOrdered(right);
        }

        /**
         * Returns an iterator that iterates over the keys on this priority queue
         * in ascending order.
         * <p>
         * The iterator doesn't implement {@code remove()} since it's optional.
         *
         * @return an iterator that iterates over the keys in ascending order
         */
        public Iterator<Key> iterator() {
            return new HeapIterator();
        }

        private class HeapIterator implements Iterator<Key> {
            // create a new pq
            private MinPQ<Key> copy;

            // add all items to copy of heap
            // takes linear time since already in heap order so no keys move
            public HeapIterator() {
                if (comparator == null)
                    copy = new MinPQ<Key>(size());
                else
                    copy = new MinPQ<Key>(size(), comparator);
                for (int i = 1; i <= n; i++)
                    copy.insert(pq[i]);
            }

            public boolean hasNext() {
                return !copy.isEmpty();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public Key next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return copy.delMin();
            }
        }
    }
}