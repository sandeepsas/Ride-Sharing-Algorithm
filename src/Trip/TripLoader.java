/*
 * Loads all the precomputed data for Algorithm1
 * 
 * @Author Sandeep Sasidharan
 */
package Trip;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.joda.time.DateTime;

import Graph.GraphNode;
import Graph.Pair;
import Trip.TaxiTrip;

public class TripLoader {

	private Map<String, List<Pair<String,String>>> dropOffMap; 
	private Map<String, Pair<Double,Double>> vertexMap; 
	private Map<String, String> DTMap; 

	private DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge> gr_t; 


	/*Database Loader*/
	public List<KdTree.XYZPoint> listTime;
	//public Collection <KdTree.XYZPoint> listTime1;
	//For kDTree
	public static KdTree<KdTree.XYZPoint> kdtree;

	public TripLoader() throws IOException, ClassNotFoundException{

		//OSM K-dTree Loader
		listTime = new ArrayList<KdTree.XYZPoint>();
		//For kDTree
		kdtree = new KdTree<KdTree.XYZPoint>();

		//Constructor - Loads Dropoffs as HashMaps
		dropOffMap = new HashMap<String, List<Pair<String,String>>>();

		BufferedReader bf = new BufferedReader(new FileReader("ObjectWarehouse/BFSMap.csv"));
		String s = new String();

		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			List<Pair<String,String>> drop_list = new ArrayList<Pair<String,String>>();
			for(int i=1; i<split_readline.length-1;i++){
				String[] sub_split = split_readline[i].split("->");
				drop_list.add(new Pair<String,String>(sub_split[0].trim(),sub_split[1].trim()));
			}
			String[] key = split_readline[0].split("->");
			dropOffMap.put(key[0].trim(), drop_list);
		}
		bf.close();
		//Load Vertex Map
		vertexMap = new HashMap<String, Pair<Double,Double>>();
		bf = new BufferedReader(new FileReader("ObjectWarehouse/VertexMap.csv"));
		s = new String();
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			String key = split_readline[0].trim();
			vertexMap.put(key, 
					new Pair<Double, Double>(Double.parseDouble(split_readline[1]),
							Double.parseDouble(split_readline[2])));
			listTime.add(new KdTree.XYZPoint(split_readline[0],Double.parseDouble(split_readline[1])
					,Double.parseDouble(split_readline[2]),0));
		}
		kdtree = new KdTree<KdTree.XYZPoint>(listTime);
		bf.close();
		// Load ShortestPath Map
		DTMap = new HashMap<String, String>();
		bf = new BufferedReader(new FileReader("ObjectWarehouse/NYCF_LGA_Node_SP.csv"));
		s = new String();
		bf.readLine();// header eliminate
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			String key = split_readline[2].trim();
			String val = split_readline[6].trim();
			DTMap.put(key,val );
		}

		bf.close();
		// Load the Graph
		//Construct Graph
		ObjectInputStream oos_graph_read = new ObjectInputStream(new FileInputStream("ObjectWarehouse/SpeedLimtGraph.obj"));
		gr_t = new  DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		gr_t =  (DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge>) oos_graph_read.readObject();
		oos_graph_read.close();
	}

	public Map<String, List<Pair<String, String>>>  getDropOffMap(){
		return this.dropOffMap;
	}
	public Map<String, Pair<Double, Double>>  getVertexMap(){
		return this.vertexMap;
	}
	public Map<String, String>  getDTMap(){
		return this.DTMap;
	}
	public DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge>  getGraph(){
		return this.gr_t;
	}
	public KdTree.XYZPoint getNNNode(KdTree.XYZPoint node){

		//Search for nearest vertex
		Collection<KdTree.XYZPoint> near_bys = kdtree.nearestNeighbourSearch(node,0.06);
		Iterator<KdTree.XYZPoint> near_bys_itr =
				near_bys.iterator();
		KdTree.XYZPoint elt = near_bys_itr.next();

		return elt;

	}

	public GraphNode nodeIDtoGraphNode(String nodeID) {
		// TODO Auto-generated method stub
		Pair<Double,Double> node_pos = vertexMap.get("nodeID");
		GraphNode node = new GraphNode();
		node.setId(Long.parseLong(nodeID));
		node.setLat(node_pos.getL());
		node.setLon(node_pos.getR());
		if(this.gr_t.containsVertex(node))
			System.out.println(node);
		return node;
	}

}
