import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSON_IO {

    static int createJSON(String path, Graph g){
        BufferedWriter bw=null;
        try {
            bw = new BufferedWriter(new FileWriter(path));
        }
        catch (IOException e){
            e.printStackTrace();
            return -1;
        }

        //initial generation of nodes
        List<Node> nodes;
        nodes=g.generateNodes(g.N_NODES);
        g.generateEdges(nodes, g.N_NODES, (int)(g.N_NODES*2.5));


        StringBuffer content=new StringBuffer();

        //building the json entries
        content.append("[\n");

        for(Node n: nodes){
            for (Map.Entry<Node, Integer> e: n.getAdj().entrySet()){
                content.append(" {\n");
                content.append("  \"ID1\": "+n.getID()+",\n");
                content.append("  \"ID2\": "+e.getKey().getID()+",\n");
                content.append("  \"DISTANCE\": "+e.getValue()+"\n");
                content.append(" },\n");
            }
        }
        content.deleteCharAt(content.length()-2);   //delete last comma
        content.append(" ]\n");

        try {
            bw.write(content.toString());   //write the entire JSON
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return 1;
    }

    static List<Node> readJSON(String path, int n_nodes){

        List<Node> nodes=new ArrayList<>();
        for(int i=0; i<n_nodes;i++){
            Node n= new Node(i);
            nodes.add(n);
        }

        //using json-simple library

        JSONParser parser=new JSONParser();
        JSONArray a=null;
        try {
            a=(JSONArray) parser.parse(new FileReader(path));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        for(Object o: a){
            JSONObject edge=(JSONObject)o;

            //getting values
            int ID1= ((Long)(edge.get("ID1"))).intValue();
            int ID2= ((Long)(edge.get("ID2"))).intValue();
            int distance= ((Long)(edge.get("DISTANCE"))).intValue();

            Node n1=nodes.get(ID1);
            Node n2=nodes.get(ID2);

            //adding edge
            n1.addAdj(n2, distance);
            n2.addAdj(n1, distance);
        }
        return nodes;
    }
}

