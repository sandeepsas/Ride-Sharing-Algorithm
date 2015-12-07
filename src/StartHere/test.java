package StartHere;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.joda.time.DateTime;

import Graph.GraphNode;
import Graph.Pair;
import Trip.Constants;
import Trip.KdTree;
import Trip.TaxiTrip;
import Trip.Trip;
import Trip.TripDataHandler;
import Trip.TripLoader;

public class test {
	//Find Driving and Walking distance 

	public static void main (String[] args0) throws IOException, ClassNotFoundException{
		//Initializations
		double PERCENTAGE_TRIP_DELAY = 0.1;
		double MAX_WALK_TIME = 5;
		//Initialize Drop-offs
		//TripDataHandler tHandler = new TripDataHandler();
		TripLoader tripLoader = new TripLoader();
		Map<String, List<Pair<String, String>>> dropOffMap = tripLoader.getDropOffMap();
		Map<String, Pair<Double,Double>> vertexMap = tripLoader.getVertexMap();
		Map<String,String> dtMap = tripLoader.getDTMap();

		// Define a trip
		//String id, double lat, double lon, int pass_count, float delay, float walk_time
		// Read Trip between 2013-01-01 07:50:00 and 2013-01-01 07:55:00
		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-01 08:50:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-01-01 08:52:00");
		List<TaxiTrip>  trips = tripLoader.loadTrips(startTime,endTime);
		Iterator<TaxiTrip> trip_itr = trips.iterator();
		TaxiTrip trip_A = trip_itr.next();
		TaxiTrip trip_B = trip_itr.next();
		//Fetch possible drop-off points for trip A
		//Find the GraphNode on Road Network
		KdTree.XYZPoint dest_A = new KdTree.XYZPoint("A",trip_A.getLat(), trip_A.getLon(),0);
		KdTree.XYZPoint dest_B = new KdTree.XYZPoint("B", trip_B.getLat() ,trip_B.getLon(),0);
		KdTree.XYZPoint OSM_dest_A = tripLoader.getNNNode(dest_A);

		String driving_time_to_dest_A_str = dtMap.get(OSM_dest_A.linearID.trim());
		float driving_time_to_dest_A = Float.parseFloat(driving_time_to_dest_A_str);

		List<Pair<String, String>> dropOffPoints_A = dropOffMap.get(OSM_dest_A.linearID.trim());

		Iterator<Pair<String, String>> d_A_itr = dropOffPoints_A.iterator();
		
		List<Pair<String, String>> possible_dropoffs_A = 
				new ArrayList<Pair<String, String>>();

		while(d_A_itr.hasNext()){

			Pair<String, String> dropoff_pair = d_A_itr.next();
			String dropoff_node_id = dropoff_pair.getL();
			String walk_time = dropoff_pair.getR();
			float driving_time_to_dropoff = Float.parseFloat(dtMap.get(dropoff_node_id.trim()));
			float walking_time_to_dest_A = Float.parseFloat(walk_time);

			float lhs = driving_time_to_dropoff + walking_time_to_dest_A - driving_time_to_dest_A;

			float max_delay_trip_A = (float) ((driving_time_to_dest_A)*PERCENTAGE_TRIP_DELAY); //10% of SP
			if (lhs<= max_delay_trip_A){
				possible_dropoffs_A.add(dropoff_pair);

			}

		}
		//Constraints for Trip B
		

		//Fetch possible drop-off points for trip A
		//Find the GraphNode on Road Network
		
		KdTree.XYZPoint OSM_dest_B = tripLoader.getNNNode(dest_B);

		String driving_time_to_dest_B_str = dtMap.get(OSM_dest_B.linearID.trim());
		float driving_time_to_dest_B = Float.parseFloat(driving_time_to_dest_B_str);
		float max_delay_trip_B = (float) (driving_time_to_dest_B*PERCENTAGE_TRIP_DELAY);
		List<Pair<String, String>> dropOffPoints_B = dropOffMap.get(OSM_dest_B.linearID.trim());
		Iterator<Pair<String, String>> d_B_itr = dropOffPoints_B.iterator();
		
		List<Pair<String, String>> possible_dropoffs_B = 
				new ArrayList<Pair<String, String>>();

		while(d_B_itr.hasNext()){

			Pair<String, String> dropoff_B_pair = d_B_itr.next();
			
			String dropoff_node_id = dropoff_B_pair.getL();
			String walk_time = dropoff_B_pair.getR();
			float driving_time_to_dropoff = Float.parseFloat(dtMap.get(dropoff_node_id.trim()));
			float walking_time_to_dest_B = Float.parseFloat(walk_time);

			// Iterate through posssible ddrop offs for trip A
			Iterator< Pair <String, String> > pdropoff_dest_A = possible_dropoffs_A.iterator();
			while(pdropoff_dest_A.hasNext()){
				Pair<String, String> dropoff_A_pair = pdropoff_dest_A.next();
				
				GraphNode drop_A = tripLoader.nodeIDtoGraphNode(dropoff_A_pair.getL());
				GraphNode drop_B = tripLoader.nodeIDtoGraphNode(dropoff_B_pair.getL());
				System.out.println("MERGEABLE");
				/*DijkstraShortestPath<GraphNode, DefaultWeightedEdge> dsp = new
						DijkstraShortestPath<GraphNode, DefaultWeightedEdge>(graph,dropoff_A_pair,dropoff_B_pair);*/
				/*float driving_time_from_dropoff_B_to_dropoff_A = (float) dsp.getPathLength();
				float lhs = driving_time_from_dropoff_B_to_dropoff_A+walking_time_to_dest_B-driving_time_to_dest_B;
				if(lhs<max_delay_trip_B){
					System.out.println("MERGEABLE");
				}*/
			}
		}

	}

}


