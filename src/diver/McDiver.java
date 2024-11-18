package diver;

import static java.lang.Math.*;
import datastructures.PQueue;
import datastructures.SlowPQueue;
import game.*;
import graph.ShortestPaths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/** This is the place for your implementation of the {@code SewerDiver}.
 */
public class McDiver implements SewerDiver {


    /**
     * See {@code SewerDriver} for specification.
     */
    @Override
    public void seek(SeekState state) {
        // TODO : Look for the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method (it may be recursive) elsewhere, with a
        // good specification, and call it from this one.
        //
        // Working this way provides you with flexibility. For example, write
        // one basic method, which always works. Then, make a method that is a
        // copy of the first one and try to optimize in that second one.
        // If you don't succeed, you can always use the first one.
        //
        // Use this same process on the second method, scram.
        seek1(state);
        return;
    }

    /**
     * See {@code SewerDriver} for specification.
     */
    @Override
    public void scram(ScramState state) {
        // TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        // with a good specification, and call it from this one.
        scram1(state);
    }

    /** See {@code SewerDriver} for specification. */
    public void seek1(SeekState state) {
        //establish search maps and queues
        Map<Long,Integer> visited = new HashMap<>(); //we've been here
        PQueue<Long> branches = new SlowPQueue<>(); //we might want to revisit here
        PQueue<Long> joneses = new SlowPQueue<>(); //keep up with the neighbors
        double priora = 10.0;
        int distrav = 0;

        //loop guard and loop
        boolean Onring = false;
        while(!Onring){
            //this tracks whether we are stuck in a dead end
            boolean deadend = true;
            //add unvisited neighbors to queu,
            for(NodeStatus jones : state.neighbors()) {
                if (!(visited.containsKey(jones.getId()))) {
                    joneses.add(jones.getId(), jones.getDistanceToRing());
                }
            }
            //move to closer step from current neighbor list [set deadend to false],
            if (!joneses.isEmpty()){
                Long navi = joneses.extractMin();
                state.moveTo(navi);
                distrav++;
                visited.put(navi,distrav);
                deadend=false;
            }
            //add remaining neighbors to branches if they have not been visited
            while (!joneses.isEmpty()){
                Long navi=joneses.extractMin();
                if (!(visited.containsKey(navi))) {
                    try {
                        branches.add(navi, priora);
                        priora -= .001;
                    } catch (IllegalArgumentException e) {
                        branches.changePriority(navi, priora);
                        priora -= .001;
                    }
                }
            }
            //if deadend true, pops from branches and navigates there
            if (deadend){
                Long navi = branches.extractMin();
                //make sure this branch hasn't already been covered
                boolean tourist = false;
                while(!tourist){
                    if(visited.containsKey(navi)){
                        navi = branches.extractMin();
                    }else{
                        tourist=true;
                    }
                }
                RetToBranch(state,navi,visited); //backtracks to the spot where the branch can be accessed
                distrav=visited.get(state.currentLocation())+1;
                state.moveTo(navi);
                visited.put(navi,distrav);
                deadend=false;
            }
            if(state.distanceToRing()==0){
                Onring=true;
            }
        }
        return;
    }
    /** See {@code SewerDriver} for specification. */
    public void scram1(ScramState state) {
        PQueue<Node> pirate; //list of coin priorities
        Maze map;
        ShortestPaths<Node, Edge> shortpath;
        //set up a loop so we can keep finding coins
        boolean scrammer = false;
        while(!scrammer){
            //set up our maze knowledge
            pirate = new SlowPQueue<>();
            map = new Maze((Set<Node>) state.allNodes());
            shortpath = new ShortestPaths<>(map);
            //build the shortest path to the exit
            shortpath.singleSourceDistances(state.currentNode());
            List<Edge> edges= shortpath.bestPath(state.exit());
            //until we can't, explore around, then do shortest path to exit
            if(state.stepsToGo() == pathsteps(edges)) {
                state.moveTo(edges.get(0).destination());
                if(state.currentNode()==state.exit()) scrammer=true;
            } else {
                //move towards nearest, biggest coin
                for(Node n : state.allNodes()){
                    double coinp;
                    double x = pathsteps(shortpath.bestPath(n));
                    try{
                        coinp=(exp(sqrt(x))/n.getTile().coins());
                    } catch (Exception e) {
                        coinp=1e6;
                    }
                    pirate.add(n,coinp);
                }
                Node hunt = pirate.extractMin();
                List<Edge> piratedge= shortpath.bestPath(hunt);
                if(state.stepsToGo() >= 2*pathsteps(piratedge)+pathsteps(edges)) {
                    try{
                        state.moveTo(piratedge.get(0).destination());
                    } catch (Exception e) {
                        state.moveTo(edges.get(0).destination());
                        if(state.currentNode()==state.exit()) scrammer=true;
                    }
                }else{
                    state.moveTo(edges.get(0).destination());
                    if(state.currentNode()==state.exit()) scrammer=true;
                }
            }
        }
        return;
    }

    private int pathsteps(List<Edge> edgelist){
        int summer = 0;
        for(Edge e : edgelist){
            summer+=e.length;
        }
        return summer;
    }

    private void RetToBranch(SeekState state, Long target, Map<Long,Integer> visited) {
        PQueue<Long> cands = new SlowPQueue<>();

        while(true){
            for(NodeStatus can : state.neighbors()) {
                //if one of our neighbors is the target, stop
                if (can.getId() == target) {
                    return;
                }
                //cands.add(can.getId(),can.getDistanceToRing());
            }
            for(NodeStatus can : state.neighbors()){
                if(visited.get(can.getId()) == visited.get(state.currentLocation())-1.0){
                    //go to the neighbor who has the number immediately lower than ours
                    state.moveTo(can.getId());
                }
            }
        }
    }

}
