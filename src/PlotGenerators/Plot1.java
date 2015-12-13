/*
 * Reduction in the number of trips (X) as a function of the % of passengers willing 
 * to ride-share (assume that max walk-time is Gaussian, with an avg of 5 mins; 
 * and max delay is Guassian with an avg of 10% of shortest path to destination).
 * */
package PlotGenerators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import Graph.Pair;
import Graph.ShareabilityGraph;
import StartHere.CheckTripMergeable;
import Trip.Constants;
import Trip.TaxiTrip;
import Trip.TripLoader;

public class Plot1 {
	
	
	public static void main (String[] args0) throws IOException, ClassNotFoundException{

		PrintWriter merge_trips_writer = new PrintWriter(new File ("MergeableTrips_set_15_v2.txt"));
		merge_trips_writer.println("Run started at"+ LocalDateTime.now() );
		merge_trips_writer.println("\n********** TRIPS MERGEABLE ********** ");
		merge_trips_writer.println("************************************* \n");

		// Read Trip between 2013-01-01 08:50:00 and 2013-01-01 08:55:00
		DateTime startTime = Constants.dt_formatter.parseDateTime("2013-12-11 12:00:00");
		DateTime endTime = Constants.dt_formatter.parseDateTime("2013-12-11 12:05:00");
		List<TaxiTrip>  trips = loadTrips(startTime,endTime);
		CheckTripMergeable.LOGGER.info("Total No of trips in the pool = "+trips.size());
		
		TripLoader tripLoader = new TripLoader();
		merge_trips_writer.println("Precomputed files loading completed at "+ LocalDateTime.now() );
		// Generate possible trip combos and populate merge-able trips
		List<Pair<TaxiTrip,TaxiTrip>>  dispatchList = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();
		
		for(int i = 0 ; i < trips.size(); i ++){
			for(int j = i+1 ; j < trips.size(); j ++){
				dispatchList.add(new Pair(trips.get(i),trips.get(j)));
			}
		}
		ShareabilityGraph sG = new ShareabilityGraph();
		List<Pair<TaxiTrip,TaxiTrip>> mergeable_trips = new ArrayList<Pair<TaxiTrip,TaxiTrip>>();

		for(int j = 0 ; j < dispatchList.size(); j ++){
			TaxiTrip trip_A = dispatchList.get(j).getL();
			TaxiTrip trip_B = dispatchList.get(j).getR();
			CheckTripMergeable.LOGGER.info("Processing "+trip_A+"and "+trip_B);  
			if(sG.euclideanCheckSucess(trip_A,trip_B)) {
				if(sG.checkMergeable(trip_A,trip_B,tripLoader,merge_trips_writer)){
					mergeable_trips.add(new Pair<TaxiTrip,TaxiTrip>(trip_A,trip_B));
				}
			}
		}
		CheckTripMergeable.LOGGER.info("Summary Printing Started");  
		//Print Results
		merge_trips_writer.println("\n************************************* ");
		merge_trips_writer.println("************* TRIP SUMMARY ********** ");
		merge_trips_writer.println("************************************* \n");
		merge_trips_writer.println("************* Time Interval ********* ");
		merge_trips_writer.println("************************************* ");
		merge_trips_writer.println(startTime.toString("yyyy-MM-dd HH:mm:ss")+" and "+ endTime.toString("yyyy-MM-dd HH:mm:ss"));
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
		//sG.useJgraphBlossom(mergeable_trips,merge_trips_writer);

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
