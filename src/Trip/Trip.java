package Trip;
/**
 * Trip class for Algorithm 1 interface
 * 
 * @author Sandeep Sasidharan
 *
 */

public class Trip {

	String id;
	double dest_long;
	double dest_lat;
	int passenger_count;
	float delay_mins;
	float walking_time_mins;

	Trip(){
		id = null;
		dest_long = 0.0;
		dest_lat=0.0;
		passenger_count = 0;
		delay_mins = 10;
		walking_time_mins = 5;
	}

	public Trip(String id, double lat, double lon, int pass_count, float delay, float walk_time){
		this.id = id;
		this.dest_long = lon;
		this.dest_lat=lat;
		this.passenger_count = pass_count;
		this.delay_mins = delay;
		this.walking_time_mins = walk_time;
	}


}
class TripDest {

	double lon;
	double lat;

	TripDest(double lat, double lon){

		this.lat = lat;
		this.lon = lon;

	}


}
