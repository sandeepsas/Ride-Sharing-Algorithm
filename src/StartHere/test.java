package StartHere;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import Graph.GraphNode;
import Graph.Pair;
import Trip.KdTree;
import Trip.Trip;
import Trip.TripDataHandler;
import Trip.TripLoader;

public class test {
	//Find Driving and Walking distance 

	public static void main (String[] args0) throws IOException{
		//Initializations
		//Initialize Drop-offs
		//TripDataHandler tHandler = new TripDataHandler();
		TripLoader tripLoader = new TripLoader();
		Map<String, List<Pair<String, String>>> dropOffMap = tripLoader.getDropOffMap();
		Map<String, Pair<Double,Double>> vertexMap = tripLoader.getVertexMap();
		Map<String,String> dtMap = tripLoader.getDTMap();
		DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> graph =  tripLoader.getGraph();

		// Define a trip
		//String id, double lat, double lon, int pass_count, float delay, float walk_time
		Trip trip_A = new Trip("A", 40.764912,-73.961334, 1, 10, 5);
		Trip trip_B = new Trip("B", 40.771759 ,-73.792358, 10, 10, 5);
		//Fetch possible drop-off points for trip A
		//Find the GraphNode on Road Network
		KdTree.XYZPoint dest_A = new KdTree.XYZPoint("A",40.764912, 40.771759,0);
		KdTree.XYZPoint OSM_dest_A = tripLoader.getNNNode(dest_A);

		float driving_time_to_dest_A = Float.parseFloat(dtMap.get(OSM_dest_A.linearID));

		List<Pair<String, String>> dropOffPoints_A = dropOffMap.get(OSM_dest_A.linearID);

		Iterator<Pair<String, String>> d_A_itr = dropOffPoints_A.iterator();
		
		List<Pair<String, String>> possible_dropoffs_A = 
				new ArrayList<Pair<String, String>>();

		while(d_A_itr.hasNext()){

			Pair<String, String> dropoff_pair = d_A_itr.next();
			String dropoff_node_id = dropoff_pair.getL();
			String walk_time = dropoff_pair.getR();
			float driving_time_to_dropoff = Float.parseFloat(dtMap.get(dropoff_node_id));
			float walking_time_to_dest_A = Float.parseFloat(walk_time);

			float lhs = driving_time_to_dropoff + walking_time_to_dest_A - driving_time_to_dest_A;

			float max_delay_trip_A = (float) ((driving_time_to_dest_A)*0.1); //10% of SP
			if (lhs<= max_delay_trip_A){
				possible_dropoffs_A.add(dropoff_pair);

			}

		}
		//Constraints for Trip B
		

		//Fetch possible drop-off points for trip A
		//Find the GraphNode on Road Network
		KdTree.XYZPoint dest_B = new KdTree.XYZPoint("B", 40.771759 ,-73.792358,0);
		KdTree.XYZPoint OSM_dest_B = tripLoader.getNNNode(dest_B);

		float driving_time_to_dest_B = Float.parseFloat(dtMap.get(OSM_dest_B.linearID));
		float max_delay_trip_B = (float) (driving_time_to_dest_B*0.01);
		List<Pair<String, String>> dropOffPoints_B = dropOffMap.get(OSM_dest_B.linearID);
		Iterator<Pair<String, String>> d_B_itr = dropOffPoints_B.iterator();
		
		List<Pair<String, String>> possible_dropoffs_B = 
				new ArrayList<Pair<String, String>>();

		while(d_B_itr.hasNext()){

			Pair<String, String> dropoff_B_pair = d_B_itr.next();
			
			String dropoff_node_id = dropoff_B_pair.getL();
			String walk_time = dropoff_B_pair.getR();
			float driving_time_to_dropoff = Float.parseFloat(dtMap.get(dropoff_node_id));
			float walking_time_to_dest_B = Float.parseFloat(walk_time);

			// Iterate through posssible ddrop offs for trip A
			Iterator< Pair <String, String> > pdropoff_dest_A = possible_dropoffs_A.iterator();
			while(pdropoff_dest_A.hasNext()){
				Pair<String, String> dropoff_A_pair = pdropoff_dest_A.next();
				
				/*DijkstraShortestPath<GraphNode, DefaultWeightedEdge> dsp = new
						DijkstraShortestPath<GraphNode, DefaultWeightedEdge>(graph,dropoff_A_pair,dropoff_B_pair);*/
				float driving_time_from_dropoff_B_to_dropoff_A = (float) dsp.getPathLength();
				float lhs = driving_time_from_dropoff_B_to_dropoff_A+walking_time_to_dest_B-driving_time_to_dest_B;
				if(lhs<max_delay_trip_B){
					System.out.println("MERGEABLE");
				}
			}
		}

	}
}


