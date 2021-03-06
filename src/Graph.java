import java.util.*;

import static java.lang.System.currentTimeMillis;

public class Graph {

    final boolean VERBOSE = false;
    final int N_NODES = 15000;
    final double N_EDGES = 2.5 * N_NODES;
    final int MAX_VALUE = 30;
    final int STD_VALUE = MAX_VALUE / 2;

    int n = 0;
    private List<Node> nodes;

    //init of random function
    private Random random = new Random();

    public static void main(String[] args) {
        Graph graph = new Graph();
    }

    public Graph() {

        long tic, toc;


        //read-write JSON file

        //JSON_IO.createJSON("edges.json",this);
        //nodes=JSON_IO.readJSON("edges.json", N_NODES);
        //int startID=8191;
        //int endID=11548;
        //start and end are the same of cpp version


        //generation of nodes
        //for ensuring the complete connection of the graph, the nodes are a tree
        nodes = generateNodes(N_NODES);

        //random generation of edges
        generateEdges(nodes, N_NODES, (int)N_EDGES);

        tic = currentTimeMillis();

        int startID=random.nextInt(N_NODES);
        int endID=random.nextInt(N_NODES);

        //let's find the way!
        dijkstra(startID, endID);

        toc = currentTimeMillis();
        //diagnostics tests
        testEdges(nodes);
        System.out.println("Elapsed time: " + (int) (toc - tic));
    }


    List<Node> generateNodes(int n_nodes) {

        List<Node> nodes = new ArrayList<>();
        int treeHeight = (int) Math.floor(Math.log(n_nodes) / Math.log(2));

        Node n = new Node(0);

        nodes.add(n);

        _generateNodes(nodes, n_nodes, n, 1, treeHeight);

        return nodes;
    }

    private void _generateNodes(List<Node> nodes, int n_nodes, Node current, int h, int treeHeight) {
        //left
        if (n < n_nodes - 1 && h <= treeHeight) {
            n++;

            Node newNode = new Node(n);

            nodes.add(newNode);

            current.addAdj(newNode, STD_VALUE);
            newNode.addAdj(current, STD_VALUE);

            if (VERBOSE) {
                for (int i=0; i < h; i++) {
                    System.out.print("|\t");
                }
                System.out.print("left son of " + current.getID() + " created (ID=" + n + ")\n");
            }
            _generateNodes(nodes, n_nodes, newNode, h + 1, treeHeight);
        }
        //right
        if (n < n_nodes - 1 && h <= treeHeight) {
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
            _generateNodes(nodes, n_nodes, newNode, h + 1, treeHeight);
        }
    }

    void generateEdges(List<Node> nodes, int n_nodes, int n_edges) {

        //  |
        // \|/ I have to subtract the number of already existing edges (equals to number of nodes - 1)

        for (int i = n_nodes-1; i < n_edges; i++) {
            int ID1 = (random.nextInt(n_nodes));
            int ID2;
            while ((ID2 = (random.nextInt(n_nodes))) == ID1) ;
            int value = random.nextInt(MAX_VALUE);

            Node n1 = nodes.get(ID1);
            Node n2 = nodes.get(ID2);

            n1.addAdj(n2, value);
            n2.addAdj(n1, value);
        }
    }

    private void dijkstra(int startID, int endID) {

        //init heap-based priority queue
        PriorityQueue<Node> PQ = new PriorityQueue<>(N_NODES, Comparator.comparing(Node::getDistance));

        Node v;

        if (startID < 0 || startID >= N_NODES) return;
        if (endID < 0 || endID >= N_NODES) return;

        //initial insertion of nodes in PQ
        PQ.addAll(nodes);
        //initial value of distance is INT_MAX

        //init of parent array
        int[] st = new int[N_NODES];
        for (int i = 0; i < N_NODES; i++) {
            st[i] = -1;
        }

        //the distance from himself is 0
        nodes.get(startID).setDistance(0);
        //the father of the first node is himself
        st[startID] = startID;

        //update the priority queue with the distance value
        PQchange(PQ, nodes.get(startID));

        while (!PQ.isEmpty()) {
            if ((v = PQ.poll()).getDistance() != Integer.MAX_VALUE) {
                for (Map.Entry<Node, Integer> set : v.getAdj().entrySet()) { //for every edge of the node
                    Node nodeTo = set.getKey();
                    int nodeToDist = set.getValue();

                    if (v.getDistance() + nodeToDist < nodeTo.getDistance()) { //if the distance of the node + edge distance is less than distance of the connected node
                        nodeTo.setDistance(v.getDistance() + nodeToDist); //the distance of the connected node is updated
                        PQchange(PQ, nodeTo); //update of PQ
                        st[nodeTo.getID()] = v.getID(); //update the father array
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

    private void PQchange(PriorityQueue<Node> pq, Node n) {

        //shortcut function for priority queue
        pq.remove(n);
        pq.add(n);
    }

    private void testEdges(List<Node> nodes) {

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
