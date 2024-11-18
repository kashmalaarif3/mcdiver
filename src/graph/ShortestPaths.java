package graph;

import cms.util.maybe.Maybe;
import cms.util.maybe.NoMaybeValue;
import datastructures.PQueue;
import datastructures.SlowPQueue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** This object computes and remembers shortest paths through a
 *  weighted, directed graph. Once shortest paths are computed from
 *  a specified source vertex, it allows querying the distance to
 *  arbitrary vertices and the best paths to arbitrary destination
 *  vertices.
 *<p>
 *  Types Vertex and Edge are parameters, so their operations are
 *  supplied by a model object supplied to the constructor.
 */
public class ShortestPaths<Vertex, Edge> {

    /** The model for treating types Vertex and Edge as forming
     * a weighted directed graph.
     */
    private final WeightedDigraph<Vertex, Edge> graph;

    /** The distance to each vertex from the source.
     */
    private Maybe<Map<Vertex, Double>> distances;

    /** The incoming edge for the best path to each vertex from the
     *  source vertex.
     */
    private Maybe<Map<Vertex, Edge>> bestEdges;

    /** Creates: a single-source shortest-path finder for a weighted graph.
     *
     * @param graph The model that supplies all graph operations.
     */
    public ShortestPaths(WeightedDigraph<Vertex, Edge> graph) {
        this.graph = graph;
    }

    /** Effect: Computes the best paths from a given source vertex, which
     *  can then be queried using bestPath().
     */
    public void singleSourceDistances(Vertex source) {
        // Implementation: uses Dijkstra's single-source shortest paths
        //   algorithm.
        PQueue<Vertex> frontier = new SlowPQueue<>();
        Map<Vertex, Double> distances = new HashMap<>();
        Map<Vertex, Edge> bestEdges = new HashMap<>();
        Map<Vertex,Double> blacks = new HashMap<>();
        Map<Vertex,Double> grays = new HashMap<>();
        // DONE: Complete computation of distances and best-path edges

        double priora = 0;
        frontier.add(source,priora);
        grays.put(source,2.0);
        distances.put(source,0.0);
        boolean BEfound = true;
        while(BEfound){
            Vertex explorer = frontier.extractMin();
            //System.out.println("explorer is now: "+explorer);
            blacks.put(explorer,2.0);
            for(Edge e : graph.outgoingEdges(explorer)){
                Vertex noder = graph.dest(e);
                if(!blacks.containsKey(noder) && !grays.containsKey(noder)){
                    priora++;
                    frontier.add(noder,priora);
                    grays.put(noder,2.0);
                }

                    if(distances.get(noder)==null){
                        distances.put(noder,distances.get(explorer)+graph.weight(e));
                        bestEdges.put(noder,e);
                        try{
                            priora++;
                            frontier.add(noder,priora);
                        } catch (Exception ignored) {}
                    } else{
                        if(distances.get(noder)>distances.get(explorer)+graph.weight(e)){
                            distances.put(noder,distances.get(explorer)+graph.weight(e));
                            bestEdges.put(noder,e);
                            try{
                                priora++;
                                frontier.add(noder,priora);
                            } catch (Exception ignored) {}
                        }
                    }
                //System.out.println(distances.get(noder)+noder.toString());
            }
            if(frontier.isEmpty()) BEfound = false;
        }

        this.bestEdges = Maybe.some(bestEdges);
        this.distances = Maybe.some(distances);
    }


    /** Returns: the distance from the source vertex to the given vertex.
     *  Checks: distances have been computed from a source vertex,
     *    and vertex v is reachable from that vertex.
     */
    public double getDistance(Vertex v) {
        try {
            Double d = distances.get().get(v);
            assert d != null : "Implementation incomplete";
            return d;
        } catch (NoMaybeValue exc) {
            throw new Error("Distances not computed yet");
        }
    }

    /**
     * Returns: the best path from the source vertex to a given target
     * vertex. The path is represented as a list of edges.
     * Requires: singleSourceDistances() has already been used to compute
     * best paths.
     */
    public List<Edge> bestPath(Vertex target) {
        LinkedList<Edge> path = new LinkedList<>();
        Map<Vertex, Edge> bestEdges = this.bestEdges.orElseGet(() -> {
            throw new Error("best distances not computed yet");
        });
        Vertex v = target;
        while (true) {
            Edge e = bestEdges.get(v);
            if (e == null) break; // must be the source vertex
            path.addFirst(e);
            v = graph.source(e);
        }
        return path;
    }
}
