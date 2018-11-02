import java.util.HashMap;
import java.util.Map;

public class Node implements Comparable{

    static boolean VERBOSE=false;
    private int ID;

    private Map<Node, Integer> adjMap = new HashMap<>();

    //TODO use map instead of class edge
    private int distance;

    public Node(int ID){
        this.ID=ID;
        this.distance=Integer.MAX_VALUE;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Map<Node, Integer> getAdj() {
        return adjMap;
    }

    public void addAdj(Node newAdj, int value) {

        for(Node n: adjMap.keySet()){
            if (n.compareTo(newAdj)==0){
                return;
            }
        }
        this.adjMap.put(newAdj, value);

        if(VERBOSE){
            System.out.println("Created edges between "+this.ID+" and "+newAdj.getID() +" : "+ value);
        }

    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(Object o) {
        Node node=(Node)o;

        return this.ID-node.getID();
    }
}
