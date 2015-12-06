package StartHere;
/**
 *  ALGORITHM - I to check merge Prototype
 * 
 * @author Sandeep Sasidharan
 *
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Graph.GraphNode;
import Trip.KdTree;
import Trip.Trip;
import Trip.TripDataHandler;

public class CheckMerge {

	/* Input trip A and Trip B
	 * 
	 * Parameters
	 * 
	 * 1. Destination (lat, long)
	 * 2. Passenger Count 
	 * trip_A ->2013000006,2013000006,VTS,1,,2013-01-01 00:00:00,2013-01-01 00:17:00,6,1020,9.77,-73.866135,40.771091,-73.961334,40.764912
	 * trip_B ->2013000011,2013000011,VTS,1,,2013-01-01 00:00:00,2013-01-01 00:15:00,1,900,7.67,-73.870834,40.773769,-73.792358,40.771759 
	 * Trip(String id, double lat, double lon, int pass_count, float delay, float walk_time)
	 * 
	 *  (assume that max walk-time is Gaussian, with an avg of 5 mins; and max delay is Guassian with an avg of 10% of shortest path to destination).
	 *  
	 * */

/*	public static void main(String[]  args) throws NumberFormatException, IOException{
		TripDataHandler tHandler = new TripDataHandler();

		Trip trip_A = new Trip("A", 40.764912,-73.961334, 10, 10, 5);
		Trip trip_B = new Trip("B", 40.771759 ,-73.792358, 10, 10, 5);

		KdTree.XYZPoint dest_A = new KdTree.XYZPoint("A",40.764912, 40.771759,0);
		KdTree.XYZPoint OSM_dest_A = tHandler.getNNNode(dest_A);

		if(OSM_dest_A.equals(dest_A)){
			System.out.println("No nearest neighbor found on Road network for A");
		}
		
		KdTree.XYZPoint dest_B = new KdTree.XYZPoint("B", 40.771759 ,-73.792358,0);
		KdTree.XYZPoint OSM_dest_B = tHandler.getNNNode(dest_B);

		if(OSM_dest_B.equals(dest_B)){
			System.out.println("No nearest neighbor found on Road network for B");
		}
		
		List <GraphNode> d_A = get_dropoffs(dest_A, 10); // dropoffs for time
		List <GraphNode> d_B = get_dropoffs(dest_B, 5);
		
		Walking time is assumed to be 5 mins. This need to be given as an input
		 *  at the time of graph generation and storing. 
		
		float driving_time_to_dest_A = get_driving_time(dest_A);
		float max_delay_trip_A = trip_A.delay_mins;//10% of driving_time_to_dest_A
		Iterator<GraphNode> d_A_itr = d_A.iterator();
		
		List <GraphNode> possible_dropoffs_for_Trip_A = new ArrayList<GraphNode>();
		
		while(d_A_itr.hasNext()){
			
			GraphNode dropoff = d_A_itr.next();
			float driving_time_to_dropoff = get_driving_time(dropoff);
			float walking_time_to_dest_A = trip_A.walking_time_mins;
			
			float lhs = driving_time_to_dropoff + walking_time_to_dest_A - driving_time_to_dest_A;
			if (lhs<= max_delay_trip_A){
				possible_dropoffs_for_Trip_A.add(dropoff);
				
			}
			
		}
		
		// Setp b Algo
		float driving_time_to_dest_B = get_driving_time(dest_B);
		float max_delay_trip_B = trip_B.delay_mins;
		Iterator<GraphNode> d_B_itr = d_B.iterator();
		
		List <GraphNode> possible_dropoffs_for_Trip_B = new ArrayList<GraphNode>();
		
		while(d_B_itr.hasNext()){
			
			GraphNode dropoff_B = d_B_itr.next();
			float driving_time_to_dropoff = get_driving_time(dropoff);
			float walking_time_to_dest_B = trip_A.walking_time_mins;
			
			// Iterate through posssible ddrop offs for trip A
			Iterator<GraphNode> pdropoff_dest_A = possible_dropoffs_for_Trip_A.iterator();
			while(pdropoff_dest_A.hasNext()){
				GraphNode dropoff_A = pdropoff_dest_A.next();
				float driving_time_from_dropoff_B_to_dropoff_A = get_driving_time(dropoff_A,dropoff_B);
				float walking_time_dropoffB_to_dest_B = trip_B.walking_time_mins; // to be changed wrt code - walking time to destination
				float lhs = driving_time_from_dropoff_B_to_dropoff_A+walking_time_dropoffB_to_dest_B-driving_time_to_dest_B;
				if(lhs<max_delay_trip_B){
					System.out.println("MERGEABLE");
				}
			}
		}
	}
*/

}
