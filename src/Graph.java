import java.util.*;

import static java.lang.System.currentTimeMillis;

public class Graph {

    final boolean VERBOSE = false;
    final int N_NODES = 15000;
    final double N_EDGES = 2.5 * N_NODES;
    final int MAX_VALUE = 30;
    final int STD_VALUE = MAX_VALUE / 2;

    int n = 0;
    List<Node> nodes;
    Random random = new Random();

    public static void main(String args[]) {
        Graph graph = new Graph();
    }

    public Graph() {
        
        n = 0;

        long tic, toc;
        nodes = generateNodes();

        generateEdges();

        tic = currentTimeMillis();

        dijkstra(random.nextInt(N_NODES), random.nextInt(N_NODES));

        toc = currentTimeMillis();

        testEdges();
        System.out.println("Elapsed time: " + (int) (toc - tic));
    }


    public List<Node> generateNodes() {

        List<Node> nodes = new ArrayList<>();
        int treeHeight = (int) Math.floor(Math.log(N_NODES) / Math.log(2));

        Node n = new Node(0);

        nodes.add(n);

        _generateNodes(nodes, n, 1, treeHeight);

        return nodes;
    }

    private void _generateNodes(List<Node> nodes, Node current, int h, int treeHeight) {
        //left
        if (n < N_NODES - 1 && h <= treeHeight) {
            n++;

            Node newNode = new Node(n);

            nodes.add(newNode);

            current.addAdj(newNode, STD_VALUE);
            newNode.addAdj(current, STD_VALUE);

            if (VERBOSE) {
                for (int i = 0; i < h; i++) {
                    System.out.print("|\t");
                }
                System.out.print("left son of " + current.getID() + " created (ID=" + n + ")\n");
            }
            _generateNodes(nodes, newNode, h + 1, treeHeight);
        }
        //right
        if (n < N_NODES - 1 && h <= treeHeight) {
            n++;

            Node newNode = new Node(n);

            nodes.add(newNode);

            current.addAdj(newNode, STD_VALUE);
            newNode.addAdj(current, STD_VALUE);

            if (VERBOSE) {
                for (int i = 0; i < h; i++) {
                    System.out.print("|\t");
                }
                System.out.print("right son of " + current.getID() + " created (ID=" + n + ")\n");
            }
            _generateNodes(nodes, newNode, h + 1, treeHeight);
        }
    }

    private void generateEdges() {

        for (int i = N_NODES; i < N_EDGES; i++) {
            int ID1 = (random.nextInt(N_NODES));
            int ID2;
            while ((ID2 = (random.nextInt(N_NODES))) == ID1) ;
            int value = random.nextInt(MAX_VALUE);

            Node n1 = nodes.get(ID1);
            Node n2 = nodes.get(ID2);

            n1.addAdj(n2, value);
            n2.addAdj(n1, value);
        }
    }

    private void dijkstra(int startID, int endID) {

        PriorityQueue<Node> PQ = new PriorityQueue<>(N_NODES, Comparator.comparing(Node::getDistance));

        Node v;

        if (startID < 0 || startID >= N_NODES) return;
        if (endID < 0 || endID >= N_NODES) return;

        PQ.addAll(nodes);

        int[] st = new int[N_NODES];
        for (int i = 0; i < N_NODES; i++) {
            st[i] = -1;
        }

        nodes.get(startID).setDistance(0);
        st[startID] = startID;

        PQchange(PQ, nodes.get(startID));

        while (!PQ.isEmpty()) {
            if ((v = PQ.poll()).getDistance() != Integer.MAX_VALUE) {
                for (Map.Entry<Node, Integer> set : v.getAdj().entrySet()) {
                    Node nodeTo = set.getKey();
                    int nodeToDist = set.getValue();

                    if (v.getDistance() + nodeToDist < nodeTo.getDistance()) {
                        nodeTo.setDistance(v.getDistance() + nodeToDist);
                        PQchange(PQ, nodeTo);
                        st[nodeTo.getID()] = v.getID();
                    }
                }
            }
        }

        if (VERBOSE) {
            for (int i = 0; i < N_NODES; i++) {
                System.out.println("parent of " + i + " id " + st[i]);
            }
            System.out.println("min distances from" + startID);
            for (int i = 0; i < N_NODES; i++) {
                System.out.println(i + " : " + nodes.get(i).getDistance() + " metres");
            }

            System.out.println("\n\nPath from " + startID + " to " + endID + ":");
            int j = endID;
            while (st[j] != j) {
                System.out.print(j + "->");
                j = st[j];
            }
            System.out.print(startID);
            System.out.println("\nWeight=" + nodes.get(endID).getDistance());
        }

    }

    private void PQchange(PriorityQueue<Node> pq, Node n) {
        pq.remove(n);
        pq.add(n);
    }

    private void testEdges() {

        int ne = 0;

        for (Node n : nodes) {
            if (VERBOSE)
                System.out.print("\nNode " + n.getID() + " connected to: ");

            for (Map.Entry<Node, Integer> e : n.getAdj().entrySet()) {
                if (VERBOSE)
                    System.out.print(" " + e.getKey().getID());
                ne++;
            }
        }

        System.out.println("\n\nNumber of nodes: " + N_NODES);
        System.out.println("Number of edges: " + ne / 2);
        System.out.println("Average edges per node: " + (ne / 2) / (float) N_NODES);

    }

}
