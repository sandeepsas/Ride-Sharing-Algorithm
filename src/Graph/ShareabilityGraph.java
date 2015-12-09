package Graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.EdmondsBlossomShrinking;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import MaximumMatching.EdmondsMatching;
import MaximumMatching.MUndirectedGraph;
import StartHere.CheckTripMergeable;
import Trip.KdTree;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class ShareabilityGraph {
	List<DefaultWeightedEdge> shareable_graph_edges;

	MUndirectedGraph uNshareGraph;

	public ShareabilityGraph (){
		shareable_graph_edges = new ArrayList<DefaultWeightedEdge>();
		uNshareGraph = new MUndirectedGraph();
	}

	public  boolean checkMergeable(TaxiTrip trip_A, 
			TaxiTrip trip_B, TripLoader tripLoader,PrintWriter merge_trips_writer) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		boolean result = false;
		//Initializations
		double PERCENTAGE_TRIP_DELAY = 0.1;
		double MAX_WALK_TIME = 5; //The Graph is populated for 5 mins 
		//Initialize Drop-offs

		//TripDataHandler tHandler = new TripDataHandler();

		Map<String, List<Pair<String, String>>> dropOffMap = tripLoader.getDropOffMap();
		Map<String, Pair<Double,Double>> vertexMap = tripLoader.getVertexMap();
		Map<String,String> dtMap = tripLoader.getDTMap();
		//Fetch possible drop-off points for trip A
		//Find the GraphNode on Road Network
		KdTree.XYZPoint dest_A = new KdTree.XYZPoint(trip_A.getMedallion(),trip_A.getLat(), trip_A.getLon(),0);
		KdTree.XYZPoint dest_B = new KdTree.XYZPoint(trip_B.getMedallion(), trip_B.getLat() ,trip_B.getLon(),0);
		KdTree.XYZPoint OSM_dest_A = tripLoader.getNNNode(dest_A);
		// Travel Time Correction Ratio Calculation
		//travel time to dest of A from LGA from Trip
		KdTree.XYZPoint OSM_dest_B = tripLoader.getNNNode(dest_B);

		String driving_time_to_dest_A_str = dtMap.get(OSM_dest_A.linearID.trim());
		float driving_time_to_dest_A = Float.parseFloat(driving_time_to_dest_A_str);
		String driving_time_to_dest_B_str = dtMap.get(OSM_dest_B.linearID.trim());
		float driving_time_to_dest_B = Float.parseFloat(driving_time_to_dest_B_str);

		float travel_time_to_A_from_trip = (float) trip_A.getTravelTime();
		float travel_time_to_B_from_trip = (float) trip_B.getTravelTime();
		float travel_time_correction_ratio_A = travel_time_to_A_from_trip/driving_time_to_dest_A;
		float travel_time_correction_ratio_B = travel_time_to_B_from_trip/driving_time_to_dest_B;
		float travel_time_correction_ratio = (travel_time_correction_ratio_A+travel_time_correction_ratio_B)/2;

		//Update the driving times to destinations
		driving_time_to_dest_A = driving_time_to_dest_A*travel_time_correction_ratio;
		driving_time_to_dest_B = driving_time_to_dest_B*travel_time_correction_ratio;

		//Drop-off points compuatations
		List<Pair<String, String>> dropOffPoints_A = dropOffMap.get(OSM_dest_A.linearID.trim());
		CheckTripMergeable.LOGGER.info("No of dropoff point for trip A = "+dropOffPoints_A.size());
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
		DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> gr_t = tripLoader.getGraph();

		float max_delay_trip_B = (float) (driving_time_to_dest_B*PERCENTAGE_TRIP_DELAY);
		List<Pair<String, String>> dropOffPoints_B = dropOffMap.get(OSM_dest_B.linearID.trim());
		Iterator<Pair<String, String>> d_B_itr = dropOffPoints_B.iterator();
		CheckTripMergeable.LOGGER.info("No of dropoff point for trip B = "+dropOffPoints_B.size());
		List<Pair<String, String>> possible_dropoffs_B = 
				new ArrayList<Pair<String, String>>();
		while(d_B_itr.hasNext()){
			Pair<String, String> dropoff_B_pair = d_B_itr.next();
			String dropoff_node_id = dropoff_B_pair.getL();
			String walk_time = dropoff_B_pair.getR();
			//float driving_time_to_dropoff = Float.parseFloat(dtMap.get(dropoff_node_id.trim()));
			float walking_time_to_dest_B = Float.parseFloat(walk_time);
			// Iterate through posssible ddrop offs for trip A
			Iterator< Pair <String, String> > pdropoff_dest_A = possible_dropoffs_A.iterator();
			while(pdropoff_dest_A.hasNext()){
				Pair<String, String> dropoff_A_pair = pdropoff_dest_A.next();

				GraphNode drop_A = tripLoader.nodeIDtoGraphNode(dropoff_A_pair.getL());
				GraphNode drop_B = tripLoader.nodeIDtoGraphNode(dropoff_B_pair.getL());
				DijkstraShortestPath<GraphNode, DefaultWeightedEdge> dsp = new
						DijkstraShortestPath<GraphNode, DefaultWeightedEdge>(gr_t,drop_A,drop_B);
				float driving_time_from_dropoff_B_to_dropoff_A = (float) dsp.getPathLength()*travel_time_correction_ratio;

				float lhs = driving_time_from_dropoff_B_to_dropoff_A+walking_time_to_dest_B-driving_time_to_dest_B;
				if(lhs<max_delay_trip_B){
					result = true;
					merge_trips_writer.println("MERGEABLE PAIR => "+trip_A+" can be dropped at "+drop_A.getId()+" (Destination - "+OSM_dest_A.linearID+" )"
							+ " and "+trip_B+" can be dropped at "+drop_B.getId()+" ( Destination - "+OSM_dest_B.linearID+" )"
							);

					result = true;
					break;
				}
			}
			if(result)
				break;
		}
		return result;
	}

	public void findMaxMatch(PrintWriter merge_trips_writer) {
		// TODO Auto-generated method stub

		MUndirectedGraph<TaxiTrip> max_match_graph = EdmondsMatching.maximumMatching(this.uNshareGraph);
		merge_trips_writer.println("Total Matches found = "+max_match_graph.size()+"\n");
		merge_trips_writer.println(max_match_graph);
	}

	public void constructShareabilityGraph(List<Pair<TaxiTrip,TaxiTrip>> mergeable_pair_list) {
		// TODO Auto-generated method stub
		Iterator<Pair<TaxiTrip,TaxiTrip>> obj_list_itr = 
				mergeable_pair_list.iterator();

		while(obj_list_itr.hasNext()){
			Pair<TaxiTrip,TaxiTrip> trip_pair = obj_list_itr.next();

			TaxiTrip trip_A = trip_pair.getL();
			TaxiTrip trip_B = trip_pair.getR();

			this.uNshareGraph.addNode(trip_A);
			this.uNshareGraph.addNode(trip_B);
			this.uNshareGraph.addEdge(trip_A, trip_B);
		}

	}

	public void useJgraphBlossom(List<Pair<TaxiTrip, TaxiTrip>> mergeable_trips, PrintWriter merge_trips_writer) {
		// TODO Auto-generated method stub
		UndirectedGraph<TaxiTrip, DefaultEdge> shareJGraph =
	            new SimpleGraph<TaxiTrip, DefaultEdge>(DefaultEdge.class);
		Iterator<Pair<TaxiTrip,TaxiTrip>> obj_list_itr = 
				mergeable_trips.iterator();

		while(obj_list_itr.hasNext()){
			Pair<TaxiTrip,TaxiTrip> trip_pair = obj_list_itr.next();

			TaxiTrip trip_A = trip_pair.getL();
			TaxiTrip trip_B = trip_pair.getR();

			shareJGraph.addVertex(trip_A);
			shareJGraph.addVertex(trip_B);
			shareJGraph.addEdge(trip_A, trip_B);
		}
		//Find MM
		EdmondsBlossomShrinking<TaxiTrip, DefaultEdge> emMMx = new EdmondsBlossomShrinking<TaxiTrip, DefaultEdge>(shareJGraph);
		Set<DefaultEdge> matched_edge_list = emMMx.getMatching();
		Iterator<DefaultEdge> matched_edge_list_itr = matched_edge_list.iterator();
		merge_trips_writer.println("\n \n ********************************************");
		merge_trips_writer.println("Total Matches found = "+matched_edge_list.size()+"\n");
		while(matched_edge_list_itr.hasNext()){
			
			merge_trips_writer.println(matched_edge_list_itr.next());
		}
		
	}
}
