import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.HashMap;

import net.datastructures.AdjacencyMapGraph;
import net.datastructures.Edge;
import net.datastructures.Graph;
import net.datastructures.GraphAlgorithms;
import net.datastructures.Map;
import net.datastructures.Vertex;
import net.datastructures.ProbeHashMap;
import net.datastructures.AdaptablePriorityQueue;
import net.datastructures.HeapAdaptablePriorityQueue;
import net.datastructures.Entry;

public class ParisMetro{

	Graph<String, Integer> graphM; //the graph of metro to be stored
 	Hashtable<String, Vertex> vertices = new Hashtable<String, Vertex>(); // hash table to store verticies

	public ParisMetro(String fileName)throws Exception, IOException{
		graphM = new AdjacencyMapGraph<String, Integer>(false);
		readMetro(fileName);
	}

	public void readMetro(String fileName) throws Exception, IOException{
		BufferedReader graphFile = new BufferedReader(new FileReader(fileName));

		// Read the edges and insert
		String line;
		while ((line = graphFile.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			if (st.countTokens() != 3)
				throw new IOException("Incorrect input file at line " + line);
			String source = st.nextToken();
			String dest = st.nextToken();
            Integer weight = new Integer(st.nextToken());
			Vertex<String> sv = vertices.get(source);
			if (sv == null) {
				// Source vertex not in graph -- insert
				sv = graphM.insertVertex(source);
				vertices.put(source, sv);
			}

			Vertex<String> dv = vertices.get(dest);
			if (dv == null) {
				// Destination vertex not in graph -- insert
				dv = graphM.insertVertex(dest);
				vertices.put(dest, dv);
			}

			// check if edge is already in graph
			if (graphM.getEdge(sv, dv) == null) {
				// edge not in graph -- add
				//e's element is now the distance between the vertices
				//Modified by Thais Bardini on November 19th, 2017 (tbard069@uottawa.ca)
				Edge<Integer> e = graphM.insertEdge(sv, dv, weight);
			}
		}
	}


	protected Vertex<String> getVertex(String vert) throws Exception {
		// Go through vertex list to find vertex -- why is this not a map
		for (Vertex<String> vs : graphM.vertices()) {
			if (vs.getElement().equals(vert)) {
				return vs;
			}
		}
		throw new Exception("Vertex not in graph: " + vert);
	}

	public void print() {
		System.out.println("Vertices: " + graphM.numVertices() + " Edges: " + graphM.numEdges());

		for (Vertex<String> vs : graphM.vertices()) {
			System.out.println(vs.getElement());
		}
		for (Edge<Integer> es : graphM.edges()) {
			System.out.println(es.getElement());
		}
		return;
	}

	public <V> Map<Vertex<String>, Integer> printAllShortestDistances(String vert) throws Exception {
        Vertex<String> vSource = getVertex( vert );

        // d.get(v) is upper bound on distance from vSource to v
        Map<Vertex<String>, Integer> d = new ProbeHashMap<>();
        // map reachable v to its d value
        Map<Vertex<String>, Integer> cloud = new ProbeHashMap<>();
        // pq will have vertices as elements, with d.get(v) as key
        AdaptablePriorityQueue<Integer, Vertex<String>> pq;
        pq = new HeapAdaptablePriorityQueue<>();
        // maps from vertex to its pq locator
        Map<Vertex<String>, Entry<Integer,Vertex<String>>> pqTokens;
        pqTokens = new ProbeHashMap<>();

        // for each vertex v of the graph, add an entry to the priority queue, with
         // the source having distance 0 and all others having infinite distance
         for (Vertex<String> v : graphM.vertices()) {
        if (v == vSource)
            d.put(v,0);
        else
            d.put(v, Integer.MAX_VALUE);
            pqTokens.put(v, pq.insert(d.get(v), v));       // save entry for future updates
        }
        // now begin adding reachable vertices to the cloud
        while (!pq.isEmpty()) {
            Entry<Integer, Vertex<String>> entry = pq.removeMin();
            int key = entry.getKey();
            Vertex<String> u = entry.getValue();
            if(key != -1){
                cloud.put(u, key);                             // this is actual distance to u
                pqTokens.remove(u);                            // u is no longer in pq
                for (Edge<Integer> e : graphM.outgoingEdges(u)) {
                    Vertex<String> v = graphM.opposite(u,e);
                    if (cloud.get(v) == null) {
                        // perform relaxation step on edge (u,v)
                        int wgt = e.getElement();
                        if (d.get(u) + wgt < d.get(v)) {              // better path to v?
                            d.put(v, d.get(u) + wgt);                   // update the distance
                            pq.replaceKey(pqTokens.get(v), d.get(v));   // update the pq entry
                        }
                    }
                 }
            }
        }

        return cloud;         // this only includes reachable vertices

     /**for(Vertex<String> v : graphM.vertices()){

            System.out.println(vSource.getElement()+"to"+g.getElement()+"="+r.get(v)));
        }
        return;**/

	}

    public void printAllShortestDistances(String vert1, String vert2) throws Exception {
        Vertex<String> vSource = getVertex( vert1 );

        // d.get(v) is upper bound on distance from vSource to v
        Map<Vertex<String>, Integer> d = new ProbeHashMap<>();
        // map reachable v to its d value
        Map<Vertex<String>, Integer> cloud = new ProbeHashMap<>();
        // pq will have vertices as elements, with d.get(v) as key
        AdaptablePriorityQueue<Integer, Vertex<String>> pq;
        pq = new HeapAdaptablePriorityQueue<>();
        // maps from vertex to its pq locator
        Map<Vertex<String>, Entry<Integer,Vertex<String>>> pqTokens;
        pqTokens = new ProbeHashMap<>();

        // for each vertex v of the graph, add an entry to the priority queue, with
        // the source having distance 0 and all others having infinite distance
        for (Vertex<String> v : graphM.vertices()) {
        if (v == vSource)
            d.put(v,0);
        else
            d.put(v, Integer.MAX_VALUE);
            pqTokens.put(v, pq.insert(d.get(v), v));       // save entry for future updates
        }
        // now begin adding reachable vertices to the cloud
        while (!pq.isEmpty()) {
            Entry<Integer, Vertex<String>> entry = pq.removeMin();
            int key = entry.getKey();
            Vertex<String> u = entry.getValue();
            if(key == -1){
                key = 90;
            }
            cloud.put(u, key);                             // this is actual distance to u
            pqTokens.remove(u);                            // u is no longer in pq
            for (Edge<Integer> e : graphM.outgoingEdges(u)) {
                Vertex<String> v = graphM.opposite(u,e);
                if (cloud.get(v) == null) {
                    // perform relaxation step on edge (u,v)
                    int wgt = e.getElement();
                    if (d.get(u) + wgt < d.get(v)) {              // better path to v?
                        d.put(v, d.get(u) + wgt);                   // update the distance
                        pq.replaceKey(pqTokens.get(v), d.get(v));   // update the pq entry
                    }
                }
            }
        }
        for(Vertex<String> v : graphM.vertices()){
            if(v.getElement()==vert2){
                System.out.println(vSource.getElement()+"to"+v.getElement()+"=" + cloud.get(v));
            }
        }
        return;

    }

    public <V> Map<Vertex<String>, Integer> printAllShortestDistances(String vert1, String vert2, String vert3) throws Exception {
        Vertex<String> vSource = getVertex( vert1 );

        // d.get(v) is upper bound on distance from vSource to v
        Map<Vertex<String>, Integer> d = new ProbeHashMap<>();
        // map reachable v to its d value
        Map<Vertex<String>, Integer> cloud = new ProbeHashMap<>();
        // pq will have vertices as elements, with d.get(v) as key
        AdaptablePriorityQueue<Integer, Vertex<String>> pq;
        pq = new HeapAdaptablePriorityQueue<>();
        // maps from vertex to its pq locator
        Map<Vertex<String>, Entry<Integer,Vertex<String>>> pqTokens;
        pqTokens = new ProbeHashMap<>();

         Map<Vertex<String>, Integer> nope = new ProbeHashMap<>();
         nope = printAllShortestDistances(vert3);

        // for each vertex v of the graph, add an entry to the priority queue, with
        // the source having distance 0 and all others having infinite distance
        for (Vertex<String> v : graphM.vertices()) {
        if (v == vSource)
            d.put(v,0);
        else
            d.put(v, Integer.MAX_VALUE);
            pqTokens.put(v, pq.insert(d.get(v), v));       // save entry for future updates
        }
        // now begin adding reachable vertices to the cloud
        while (!pq.isEmpty()) {
            Entry<Integer, Vertex<String>> entry = pq.removeMin();
            int key = entry.getKey();
            Vertex<String> u = entry.getValue();
            if(key == -1){
                key = 90;
            }
            if(Arrays.asList(nope).contains(u)){
                cloud.put(u, key);                             // this is actual distance to u
                pqTokens.remove(u);                            // u is no longer in pq
                for (Edge<Integer> e : graphM.outgoingEdges(u)) {
                    Vertex<String> v = graphM.opposite(u,e);
                    if (cloud.get(v) == null) {
                        // perform relaxation step on edge (u,v)
                        int wgt = e.getElement();
                        if (d.get(u) + wgt < d.get(v)) {              // better path to v?
                            d.put(v, d.get(u) + wgt);                   // update the distance
                            pq.replaceKey(pqTokens.get(v), d.get(v));   // update the pq entry
                        }
                    }
                }
            }
        }
        return cloud;         // this only includes reachable vertices

     /**for(Vertex<String> v : graphM.vertices()){

            System.out.println(vSource.getElement()+"to"+g.getElement()+"="+r.get(v)));
        }
        return;**/

    }

}
