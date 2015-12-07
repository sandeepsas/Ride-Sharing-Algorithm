package Graph;

import java.io.Serializable;
import java.util.Set;

/**
 * 
 * @author Sandeep Sasidharan
 *
 */
public class GraphNode implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -951013750518377697L;
	/**
	 * 
	 */

	private double lon;
	private double lat;
	private long id;
	

	public GraphNode() {
		this.lon = 0.0;
		this.lat = 0.0;
		this.id = 0;
	}
	

	public GraphNode(double lat, double lon, long id) {
		this.lon = lon;
		this.lat = lat;
		this.id = id;
	}
	

	public double getLon() {
		return lon;
	}
	
	public double getLat() {
		return lat;
	}
	
	
	public long getId() {
		return id;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
		
	public void setId(long l){
		this.id = l;
	}

	
	public boolean equals(GraphNode node){
		if(node == null)
			return false;
		return this.getId()==node.getId();
	}
	
	public String toString(){
		String rt_str;
		rt_str = this.id+", ("+this.lat+ "," + this.lon+")";
		return	rt_str;
	}
	
}
