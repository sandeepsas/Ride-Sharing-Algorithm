/*
 * This class checks if a pair of trips are mergeable or not
 * 
 * @Author: Sandeep Sasidharan
 */
package StartHere;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
import Trip.TripLoader;

public class CheckTripMergeable {

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintWriter merge_trips_writer = new PrintWriter(new File ("MergeableTrips_set1.txt"));
		merge_trips_writer.println("********** TRIPS MERGEABLE ********** ");
		merge_trips_writer.println("************************************* \n");
		
		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-03 07:41:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-01-03 07:42:00");
		List<TaxiTrip>  trips = loadTrips(startTime,endTime);
		Iterator<TaxiTrip> trip_itr1 = trips.iterator();
		// Generate possible trip combos and populate merge-able trips
		TripLoader tripLoader = new TripLoader();
		List<Pair<TaxiTrip,TaxiTrip>> mergeable_trips = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();
		int ctr = 0;
		int noTr = trips.size();
		while(trip_itr1.hasNext()){
			System.out.println(ctr+"->"+noTr);
			ctr++;
			TaxiTrip trip_A = trip_itr1.next();
			Iterator<TaxiTrip> trip_itr2 = trips.iterator();
			while (trip_itr2.hasNext()){
				TaxiTrip trip_B = trip_itr2.next();
				if(!trip_A.equals(trip_B)){
					if(checkMergeable(trip_A,trip_B,tripLoader,merge_trips_writer))
						mergeable_trips.add(new Pair<TaxiTrip,TaxiTrip>(trip_A,trip_B));
				}
			}
		}
		//Print Results
		merge_trips_writer.println("************* TRIP SUMMARY ********** ");
		merge_trips_writer.println("************************************* \n");
		merge_trips_writer.println("************* Time Interval ********* ");
		merge_trips_writer.println("************************************* ");
		merge_trips_writer.println("2013-01-01 07:50:00 and 2013-01-01 07:55:00");
		merge_trips_writer.println("************************************* ");
		merge_trips_writer.println("Total Number of Trips = "+trips.size());
		merge_trips_writer.println("************************************* ");
		Iterator <Pair<TaxiTrip,TaxiTrip>> merge_list_itr = mergeable_trips.iterator();
		while(merge_list_itr.hasNext()){
			Pair<TaxiTrip,TaxiTrip> merge_pair = merge_list_itr.next();
			merge_trips_writer.println("\n Trip# "+merge_pair.getL()+" and Trip# "+merge_pair.getR());
		}
		merge_trips_writer.println("************************************* ");
		merge_trips_writer.println("Number of Mergeable Pairs = "+mergeable_trips.size());
		merge_trips_writer.println("************************************* ");
		merge_trips_writer.close();
	}

	private  static boolean checkMergeable(TaxiTrip trip_A, 
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
					merge_trips_writer.println("MERGEABLE PAIR => Trip# "+trip_A+" can be dropped at "+drop_A.getId()+" (Original Destination - "+OSM_dest_A.linearID+" )"
							+ " and Trip # "+trip_B+" can be dropped at "+drop_B.getId()+" (Original Destination - "+OSM_dest_B.linearID+" )"
							);
				}else{
					result = false;
				}
			}
		}
		return result;
	}
	public static List<TaxiTrip> loadTrips(DateTime startTime, DateTime endTime) throws IOException {
		// TODO Auto-generated method stub
		List<TaxiTrip> trips = new ArrayList<TaxiTrip>();
		BufferedReader bf = new BufferedReader(new FileReader("TripData/TripData.csv"));
		String s = new String();
		s = bf.readLine();
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			DateTime trip_start_time =  Constants.dt_formatter.parseDateTime(split_readline[5]);

			TaxiTrip trip = new TaxiTrip();
			
			if(trip_start_time.compareTo(startTime)>0 &&
					trip_start_time.compareTo(endTime)<=0 	){
				trip = new TaxiTrip(split_readline[0],
						split_readline[5],
						split_readline[6],
						split_readline[7],
						split_readline[8],
						split_readline[9],
						split_readline[10],
						split_readline[11],
						split_readline[12],
						split_readline[13]);
				
				int paasenger_count = trip.getPassengerCount();
				if(paasenger_count==1)
					trips.add(trip);
			}

		}
		return trips;

	}

}


