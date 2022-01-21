package ch09graph.weighted;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

import kap09graphenErweitert.weighted.WeightedGraph.Edge;
import kap09graphenErweitert.weighted.WeightedGraph.MyColor;
import kap09graphenErweitert.weighted.WeightedGraph.Node;

/** Implementierung für gerichtete oder ungerichtete Graphen mittels Adjazenzlisten
 * 
 * @author G. Schied, Hochschule Ulm
 */
public class WeightedGraph {

	/** Beschreibt einen Eintrag in der Adjazenzliste eines Knotens.
	 *  Ein Eintrag steht für eine gewichtete Kante zu einem Nachbarknoten */
	public class Edge implements Comparable<Edge> {
		private Node src;
		private Node dest;
		private double weight;

		public Edge(Node src, Node dest, double w) {
			this.src = src;
			this.dest = dest;
			this.weight = w;
		};

		public Node getDest() {
			return dest;
		}
		
		public Node getSrc() {
			return src;
		}

		public double getWeight() {
			return weight;
		}

		public String toString() {
			return src + " --(" + weight + ")--> " + dest;
		}

		@Override
		public int compareTo(Edge o2) {
			if (weight < o2.weight)
				return -1;
			else if (weight > o2.weight) {
				return 1;
			}
			else
				return 0;
		}
	}

	/** Farbe für die Knotenfärbung */
	public enum MyColor {
		WHITE, GREY, BLACK
	};

	/** Knoten mit Knoteninfo und Adjazenzliste */
	public class Node implements Comparable<Node> {
		Object info;
		List<Edge> adjList = new ArrayList<Edge>(); // Adjazenzliste
		MyColor color;
		double distance = Double.POSITIVE_INFINITY;  //Distanz zum Startknoten
		Node predecessor = null;
		
		public Node(Object info) {
			this.info = info;
			this.color = MyColor.WHITE;
		};

		public Object getInfo() {
			return info;
		}
		
		public double getDistance() {
			return distance;
		}
		
		public void setColor(MyColor c) {
			color = c;
		}

		public String toString() {
			return "Node " + info + "(" + distance + ")";
		}

		public void print() {
			System.out.println("  Node " + info + ", " + color + ", distance " + distance + ", pred " + predecessor);
			for (Edge e : adjList) {
				System.out.println("\t" + e.toString());
			}
		}

		@Override
		public int compareTo(Node o2) {
		if (this.distance < o2.distance)
			return -1;
		else if (this.distance > o2.distance) {
			return 1;
		}
		else
			return 0;
		}
	}

	private boolean isDirected;
	private int nodecount = 0;
	private int edgecount = 0;
	private List<Node> nodes = new ArrayList<Node>();

	public WeightedGraph() {
		this.isDirected = true;
	}

	public WeightedGraph(boolean isDirected) {
		this.isDirected = isDirected;
	}

	/** Erzeugt einen neuen Knoten mit der angegebenen Info und fügt ihn dem Graphen hinzu
	 * 
	 * @param info	Wert des Knoten
	 * @return 		neu erzeugter Knoten
	 */
	public Node addNode(Object info) {
		Node newNode = new Node(info);
		nodes.add(newNode);
		nodecount++;
		return newNode;
	}

	/** Fügt eine neue Kante in den Graph ein
	 * @param src	Ausgangsknoten der Kante
	 * @param dest	Zielknoten der kante
	 * @param weight Kantengewicht
	 */
	public void addEdge(Node src, Node dest, double weight) {
		Edge e = new Edge(src, dest, weight);
		src.adjList.add(e);
		if (!isDirected) {
			// Gegenkante in Adjazenzliste aufnehmen
			Edge eReverse = new Edge(dest, src, weight);
			dest.adjList.add(eReverse);
		}
		edgecount++;
	}
	
	public List<Node> getNodes() {
		return nodes;
	}

	/**
	 * Ausgabe des Graphen mit allen Knoten und Kanten (zu Testzwecken)
	 */
	public void print() {
		if (isDirected) {
			System.out.println("Directed graph: ");
		} else {
			System.out.println("Undirected graph: ");
		}
		System.out.println("  # nodes:    " + nodecount);
		System.out.println("  # edges:    " + edgecount);
		for (Node n : nodes) {
			n.print();
		}
	}

	/** Hilfsfunktion zur Vorbereitung der verschiedenen Suchverfahren
	 *  Für alle Knoten werden die Werte auf den Ausgangswert zurückgesetzt
	 */
	private void initialize() {
		for (Node n : nodes) {
			n.color = MyColor.WHITE;
			n.distance = Double.POSITIVE_INFINITY;
			n.predecessor = null;
		}
	}

	/** Berechnet die kürzesten Pfad vom Knoten start zu allen anderen Knoten 
	 *  mit dem Algorithmus von Bellman-Ford. Negative Kantengewichte sind erlaubt
	 *  
	 * @param start Startknoten (mit Distanz 0)
	 * @return true, falls kein Zyklus mit negativem Gewicht existiert
	 */
	public boolean shortestPathsBellmanFord(Node start) {
		initialize();
		start.distance = 0.0;
		
		for (int i = 0; i < nodes.size() - 1; i++) {
			for (Node n : nodes) {
				for (Edge e : n.adjList) {
					relaxation(e);
				}
			}
		}
		
		for (Node src : nodes) {
			for (Edge e : src.adjList) {
				Node dest = e.dest;
				if (dest.distance > src.distance + e.weight) {
					//System.out.println("cycle test: " + e);
					return false; //Zyklus mit negativem Gewicht existiert!
				}
			}
		}
		return true;
	}

	private void relaxation(Edge e) {
		Node src = e.getSrc();
		Node dest = e.getDest();
		if (src.distance + e.weight < dest.distance) {
			dest.distance = src.distance + e.weight;
			dest.predecessor = src;
		}
	}
	
	
	/** Berechnet einen minimalen Spannbaum mit dem Algorithmus von Prim
	 * 
	 */


	public Set<Edge> minimalSpanningTreePrim() {
		initialize();
		
		Set<Edge> spanningTree = new HashSet<Edge>();
		
		PriorityQueue<Node> neighbourNodes = new PriorityQueue<Node>();
		
		Node start = nodes.get(0);
		start.color = MyColor.GREY;
		
		neighbourNodes.add(start);
		while (! neighbourNodes.isEmpty()) {
			Node nextNode = neighbourNodes.remove();
			nextNode.setColor(MyColor.BLACK);
			if (nextNode != start) {
				Edge minEdge = new Edge(nextNode.predecessor, nextNode, nextNode.distance);
				
				System.out.println("Add edge: " + minEdge);
				spanningTree.add(minEdge);
			}
			
			for (Edge edge : nextNode.adjList) {
				Node neighbour = edge.dest;
				double weight = edge.getWeight();
			
				if (neighbour.color == MyColor.WHITE) {
					neighbour.color = MyColor.GREY;
					neighbour.distance = weight;
					neighbour.predecessor = nextNode;
					neighbourNodes.add(neighbour);
				} else if (neighbour.color == MyColor.GREY) {
					if (weight < neighbour.distance) {
						neighbour.distance = weight;
						neighbour.predecessor = nextNode;
						//Workaround für neighbourNodes.decreaseKey(neighbour)
						neighbourNodes.remove(neighbour);
						neighbourNodes.add(neighbour);
					}
				}
			}
		}
		
		return spanningTree;

	
	}
		
	
}
