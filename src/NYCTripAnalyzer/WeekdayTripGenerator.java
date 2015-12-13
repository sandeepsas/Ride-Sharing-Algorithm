package NYCTripAnalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import Trip.Constants;

public class WeekdayTripGenerator {
	public static void main(String[] args) throws IOException {

		System.out.println("Run started at"+ LocalDateTime.now() );
		//PrintWriter manhattan = new PrintWriter("TripData/ManhattanTrips.csv");

		BufferedReader bf = new BufferedReader(new FileReader("TripData/TripData.csv"));
		String s = new String();
		s = bf.readLine();
		while((s=bf.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");

			DateTime trip_start_time =  Constants.dt_formatter.parseDateTime(split_readline[5]);
			DateTime startTime = Constants.dt_formatter.parseDateTime("10:00:00");
			DateTime endTime = Constants.dt_formatter.parseDateTime("10:05:00");
			
			if(trip_start_time.compareTo(startTime)>0 &&
					trip_start_time.compareTo(endTime)<=0 	){
				
			}

		}
		bf.close();
		//manhattan.close();
		//non_manhattan.close();
	}

}
