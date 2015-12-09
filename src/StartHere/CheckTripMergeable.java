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
import java.util.logging.Logger;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import Graph.GraphNode;
import Graph.Pair;
import Graph.ShareabilityGraph;
import NYCTripAnalyzer.TripDataGenerator;
import Trip.Constants;
import Trip.KdTree;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class CheckTripMergeable {
	public static final Logger LOGGER = Logger.getLogger(CheckTripMergeable.class.getName());

	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintWriter merge_trips_writer = new PrintWriter(new File ("MergeableTrips_set9.txt"));
		merge_trips_writer.println("Run started at"+ LocalDateTime.now() );
		merge_trips_writer.println("\n********** TRIPS MERGEABLE ********** ");
		merge_trips_writer.println("************************************* \n");

		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-01-03 07:50:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-01-03 07:55:00");
		List<TaxiTrip>  trips = loadTrips(startTime,endTime);
		CheckTripMergeable.LOGGER.info("Total No of trips in the pool = "+trips.size());
		merge_trips_writer.println("Precomputed files loading completed at "+ LocalDateTime.now() );
		Iterator<TaxiTrip> trip_itr1 = trips.iterator();
		// Generate possible trip combos and populate merge-able trips
		TripLoader tripLoader = new TripLoader();
		List<Pair<TaxiTrip,TaxiTrip>> mergeable_trips = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();

		ShareabilityGraph sG = new ShareabilityGraph();

		while(trip_itr1.hasNext()){

			TaxiTrip trip_A = trip_itr1.next();
			Iterator<TaxiTrip> trip_itr2 = trips.iterator();
			while (trip_itr2.hasNext()){

				TaxiTrip trip_B = trip_itr2.next();
				if(!trip_A.equals(trip_B)){
					if(!mergeable_trips.contains(new Pair<TaxiTrip,TaxiTrip>(trip_B,trip_A))){
						if(sG.checkMergeable(trip_A,trip_B,tripLoader,merge_trips_writer)){
							CheckTripMergeable.LOGGER.info("Processing "+trip_A+"and "+trip_B);
							mergeable_trips.add(new Pair<TaxiTrip,TaxiTrip>(trip_A,trip_B));
						}
					}
				}
			}
		}
		//Print Results
		merge_trips_writer.println("\n************************************* ");
		merge_trips_writer.println("************* TRIP SUMMARY ********** ");
		merge_trips_writer.println("************************************* \n");
		merge_trips_writer.println("************* Time Interval ********* ");
		merge_trips_writer.println("************************************* ");
		merge_trips_writer.println("2013-01-01 07:50:00 and 2013-01-01 07:55:00");
		merge_trips_writer.println("************************************* ");
		merge_trips_writer.println("Total Number of Trips = "+trips.size());
		merge_trips_writer.println("************************************* ");
		merge_trips_writer.println("\n ************************************* ");
		merge_trips_writer.println("Number of Mergeable Pairs = "+mergeable_trips.size());
		merge_trips_writer.println("************************************* ");
		Iterator <Pair<TaxiTrip,TaxiTrip>> merge_list_itr = mergeable_trips.iterator();
		while(merge_list_itr.hasNext()){
			Pair<TaxiTrip,TaxiTrip> merge_pair = merge_list_itr.next();
			merge_trips_writer.println("\n"+merge_pair.getL()+" and "+merge_pair.getR());
		}




		/*Construct Shareability Graph*/
		sG.constructShareabilityGraph(mergeable_trips);
		merge_trips_writer.println("\n ************************************* ");
		merge_trips_writer.println(" MAXIMUM MATCH PAIRS");
		merge_trips_writer.println("************************************* ");
		sG.findMaxMatch(merge_trips_writer);
		merge_trips_writer.println("************************************* ");


		sG.useJgraphBlossom(mergeable_trips,merge_trips_writer);

		merge_trips_writer.println("Run ended at"+ LocalDateTime.now() );
		merge_trips_writer.close();
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
		bf.close();
		return trips;

	}

}


