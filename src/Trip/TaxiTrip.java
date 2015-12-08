package Trip;

import org.joda.time.Duration;

public class TaxiTrip {

	String medallion;
	String pickup_datetime;
	String dropoff_datetime;
	String passenger_count;
	String trip_time_in_secs;
	String trip_distance;
	String pickup_longitude;
	String pickup_latitude;
	String dropoff_longitude;
	String dropoff_latitude;

	public TaxiTrip(String medallion,
			String pickup_datetime,
			String dropoff_datetime,
			String passenger_count,
			String trip_time_in_secs,
			String trip_distance,
			String pickup_longitude,
			String pickup_latitude,
			String dropoff_longitude,
			String dropoff_latitude){

		this.medallion = medallion;
		this.pickup_datetime = pickup_datetime;
		this.dropoff_datetime = dropoff_datetime;
		this.passenger_count = passenger_count;
		this.trip_time_in_secs = trip_time_in_secs;
		this.trip_distance = trip_distance;
		this.pickup_longitude = pickup_longitude;
		this.pickup_latitude = pickup_latitude;
		this.dropoff_longitude = dropoff_longitude;
		this.dropoff_latitude = dropoff_latitude;

	}
	
	public double getLat(){
		return Double.parseDouble(this.dropoff_latitude);
	}
	public double getLon(){
		return Double.parseDouble(this.dropoff_longitude);
	}
	public double getPickupTime(){
		return Double.parseDouble(this.dropoff_latitude);
	}
	public double getDropOff(){
		return Double.parseDouble(this.dropoff_longitude);
	}
	public int getPassengerCount(){
		return Integer.parseInt(this.passenger_count);
	}

	public TaxiTrip() {
		// TODO Auto-generated constructor stub
	}

	public long getTravelTime() {
		// TODO Auto-generated method stub
		Duration trip_duration = FilterFns.CalculateTripDuration(this.pickup_datetime,this.dropoff_datetime);
		long trip_duration_mins = trip_duration.getStandardMinutes() ;
		return trip_duration_mins;
	}

	public String getMedallion() {
		// TODO Auto-generated method stub
		return this.medallion;
	}
	public String toString(){
		return ("TRIP # "+this.medallion);
		
	}

}
