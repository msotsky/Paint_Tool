package ch09graph.weighted;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JOptionPane;

import ch09graph.weighted.WeightedGraph.Edge;
import ch09graph.weighted.WeightedGraph.Node;

public class WeightedGraphDemo {


	public static void main(String[] args) {
//		testBellmanFord_1();
//		testBellmanFord_2();
//		testPrim_1();
		testPrim_Paint();
	}
	
	public static void testBellmanFord_1() {
		WeightedGraph gd = new WeightedGraph();
		WeightedGraph.Node na = gd.addNode("a");
		WeightedGraph.Node nb = gd.addNode("b");
		WeightedGraph.Node nc = gd.addNode("c");
		WeightedGraph.Node nd = gd.addNode("d");
		WeightedGraph.Node ne = gd.addNode("e");
		
		gd.addEdge(na, nb, 6);
		gd.addEdge(na, nc, 7);
		
		gd.addEdge(nb, nd, 5);
		gd.addEdge(nb, ne, -4);
		gd.addEdge(nb, nc, 8);
		
		gd.addEdge(nc, nd, -3);
		gd.addEdge(nc, ne, 9);

		gd.addEdge(nd, nb, -2);

		gd.addEdge(ne, na, 2);
		gd.addEdge(ne, nd, 7);
		
		gd.print();

		boolean withoutNegativeCycle = gd.shortestPathsBellmanFord(na);
		
		System.out.println();
		System.out.println("Shortest path (Bellman-Ford)");
		
		gd.print();
		if (withoutNegativeCycle) {
			System.out.println("No cycle with negative weight");	
			System.out.println();

		}
		else {
			System.out.println("!!! cycle with negative weight exists !!!");
		}
	}


	public static void testBellmanFord_2() {
		WeightedGraph gd2 = new WeightedGraph();
		WeightedGraph.Node na = gd2.addNode("a");
		WeightedGraph.Node nb = gd2.addNode("b");
		WeightedGraph.Node nc = gd2.addNode("c");
		
		gd2.addEdge(na, nb, 3);		
		gd2.addEdge(nb, nc, 1);
		gd2.addEdge(nc, na, -5);

		boolean withoutNegativeCycle = gd2.shortestPathsBellmanFord(na);
		
		System.out.println();
		System.out.println("Shortest paht (Bellman-Ford)");
		
		gd2.print();
		if (withoutNegativeCycle) {
			System.out.println("No cycle with negative weight");	
			System.out.println();

		} else {
			System.out.println("!!! cycle with negative weight exists !!!");
		}
	}

	

	
	public static void testPrim_1() {
		WeightedGraph algo = new WeightedGraph(false);
		
		WeightedGraph.Node nA = algo.addNode("A");
		WeightedGraph.Node nB = algo.addNode("B");
		WeightedGraph.Node nC = algo.addNode("C");
		WeightedGraph.Node nD = algo.addNode("D");
		WeightedGraph.Node nE = algo.addNode("E");
		WeightedGraph.Node nF = algo.addNode("F");
		WeightedGraph.Node nG = algo.addNode("G");
		WeightedGraph.Node nH = algo.addNode("H");
		
		algo.addEdge(nA, nB, 310);
		algo.addEdge(nA, nC, 500);
		algo.addEdge(nA, nD, 700);
		algo.addEdge(nA, nH, 130);
		
		algo.addEdge(nB, nC, 110);
		algo.addEdge(nB, nD, 180);
		algo.addEdge(nB, nE, 200);
		algo.addEdge(nB, nG, 350);
		algo.addEdge(nB, nC, 110);
		algo.addEdge(nB, nH, 420);

		algo.addEdge(nC, nE, 460);
		algo.addEdge(nC, nG, 170);
		algo.addEdge(nC, nH, 90);
		
		algo.addEdge(nD, nE, 190);
		algo.addEdge(nD, nF, 230);
		
		algo.addEdge(nE, nF, 100);
		algo.addEdge(nE, nG, 80);
		
		algo.addEdge(nF, nG, 70);
		
		algo.addEdge(nG, nH, 380);


		Set<WeightedGraph.Edge> stree = algo.minimalSpanningTreePrim();
		
		System.out.println("Minimal Spanning Tree:");
		double sum = 0;
		for (WeightedGraph.Edge e : stree) {
			sum += e.getWeight();
			System.out.println(e);
		}
		System.out.println("total weight: " + sum);
	}
	
	
	//public static int NUM_OF_NODES = 200;
	public static int SIZE_X = 600;
	public static int SIZE_Y = 600;
	
	public static void testPrim_Paint() {
		String eingabe = JOptionPane.showInputDialog("Minimal spanning tree - Number of nodes:", 50);
		int numOfNodes = 100;
		if (eingabe != null) {
			numOfNodes = Integer.parseInt(eingabe);
		}

		WeightedGraph posGraph = generateCompleteRandomGraph(numOfNodes);
		//WeightedGraph posGraph = generateRandomGraph(NUM_OF_NODES);
		//Knoten zeichnen
		PaintTool pt = new PaintTool("Minimal Spanning Tree", SIZE_X, SIZE_Y); 
		
		drawGraph(pt, posGraph);
		
		
		//Spannbaum berechnen
		JOptionPane.showMessageDialog(null, "compute minimal spanning tree");
		Set<Edge> spantree = posGraph.minimalSpanningTreePrim();
			
		//Kanten des Spannbaums zeichnen
		for (Edge e : spantree) {
			drawEdge(pt, e, Color.RED);
		}
	}
	private final static int LIMIT = 4;
	
	/** Erzeugt einen zufälligen Graphen, der aus einer vorgegebenen Anzahl Knoten besteht
	 *  Als Knoten-Info werden zufällig gewählte Positionen (x,y) in der Ebene genommen 
	 *  (mit 0 <= x < SIZE_X und 0 <= y < SIZE_Y). Jeder Knoten wird mit LIMIT vielen zufällig gewählten 
	 *  Knoten durch eine Kante verbunden. Das Gewicht der Kante ist der euklidische Abstand zwischen den Knoten
	 * @param numOfNodes Anzahl der Knoten des Graphen
	 */
	private static WeightedGraph generateRandomGraph(int numOfNodes) {
		Random rand = new Random(42);
		WeightedGraph posGraph = new WeightedGraph(false);	
		
		for (int i = 0; i < numOfNodes; i++) {
			Position pos = new Position(rand.nextInt(SIZE_X), rand.nextInt(SIZE_Y));
			posGraph.addNode(pos);
		}

		List<WeightedGraph.Node> nodelist = posGraph.getNodes();
		for (Node n : nodelist) {
			Position nPos = (Position) n.getInfo();
			for (int i = 0; i < LIMIT; i++) {
				Node other = nodelist.get(rand.nextInt(numOfNodes));
				Position otherPos = (Position)other.getInfo();
				double weight = nPos.distance(otherPos);
				posGraph.addEdge(n, other, weight);
			}
		}
		
		return posGraph;
	}
	
	

	private static WeightedGraph generateCompleteRandomGraph(int numOfNodes) {
		//Graph zufällig aufbauen: Knoten sind zufällige Positionen (x,y) in der Fläche
		//
		Random rand = new Random(42);
		WeightedGraph posGraph = new WeightedGraph(false);	
		
		Node[] nodes = new Node[numOfNodes];
		for (int i = 0; i < numOfNodes; i++) {
			Position pos = new Position(rand.nextInt(SIZE_X), rand.nextInt(SIZE_Y));
			nodes[i] = posGraph.addNode(pos);
		}
		
		for (int i = 0; i < nodes.length; i++) {
			Node src = nodes[i];
			Position posi = (Position) src.getInfo();
			for (int j = i+1; j < nodes.length; j++) {
				WeightedGraph.Node dest = nodes[j];
				Position posj = (Position) dest.getInfo();
				double distance = posj.distance(posi);
				posGraph.addEdge(src, dest, distance);
			}
		}
		
		return posGraph;
	}
	
	/** Zeichet einen Graphen, dessen Knoten Positionsangaben als Info tragen.
	 *  Gerichtete und ungerichtete Kanten werden lediglich als Verbindung zwischen
	 *  den Knoten gezeichnet.
	 * @param pt PaintTool zum Zeichnen
	 * @param graph gewichteter Graph
	 */
	private static void drawGraph(PaintTool pt, WeightedGraph graph) {
		//Kanten zu Nachbarknoten zeichnen
		for (Node n : graph.getNodes()) {
			for (Edge e : n.adjList) {
				drawEdge(pt, e, Color.LIGHT_GRAY);
			}
		}
		//Knoten zeichnen
		for (Node n : graph.getNodes()) {
			drawNode(pt, n, Color.DARK_GRAY);
		}
	}
	
	
	private static final int NODE_RADIUS = 3;
	private static void drawNode(PaintTool pt, Node n, Color col) {
		pt.setColor(col);
		Position posN = (Position) n.getInfo();
		Position circlePos = new Position(posN.getX() - NODE_RADIUS, posN.getY() - NODE_RADIUS);
		pt.fillOval(circlePos, 2*NODE_RADIUS, 2*NODE_RADIUS);
	}
	
	private static void drawEdge(PaintTool pt, Edge e, Color col) {
		drawEdge(pt, e.getSrc(), e.getDest(), col);
	}
	
	private static void drawEdge(PaintTool pt, Node n1, Node n2, Color col) {
		pt.setColor(col);
		Position posSrc = (Position) n1.getInfo();
		Position posDest = (Position) n2.getInfo();
		pt.drawLine(posSrc, posDest);
	}
}
