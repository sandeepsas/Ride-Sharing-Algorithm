package Trip;
/**
* Trip handler mines travel time info from NYC trip data
 * 
 * K-dTree based Knn search available
 * 
 * @author Sandeep Sasidharan
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.joda.time.Duration;

import Graph.GraphNode;


public class TripDataHandler {

	/*Database Loader*/
	public List<KdTree.XYZPoint> listTime;
	//public Collection <KdTree.XYZPoint> listTime1;
	//For kDTree
	public static KdTree<KdTree.XYZPoint> kdtree;

	public TripDataHandler() throws NumberFormatException, IOException {
		/*Database Loader*/
		listTime = new ArrayList<KdTree.XYZPoint>();
		//For kDTree
		kdtree = new KdTree<KdTree.XYZPoint>();

		File csv_files = new File("TripData/TripData.csv");
		BufferedReader file_reader = new BufferedReader(new FileReader(csv_files));
		String file_lines = new String();
		while((file_lines=file_reader.readLine())!=null 
				&& file_lines.length()!=0)
		{
			String[] file_line_split = file_lines.split(",");
			String medallion = file_line_split[0];
			String pickup_datetime = file_line_split[5];
			String dropoff_datetime = file_line_split[6];
			double longitudeP = Double.parseDouble(file_line_split[10]);
			double latitudeP = Double.parseDouble(file_line_split[11]);
			double longitudeD = Double.parseDouble(file_line_split[12]);
			double latitudeD = Double.parseDouble(file_line_split[13]);

			double dist_filter = FilterFns.inLaGDist(latitudeP, longitudeP);

			if(dist_filter<0.5){

				Duration trip_duration = FilterFns.CalculateTripDuration(pickup_datetime,dropoff_datetime);
				long trip_duration_mins = trip_duration.getStandardMinutes() ;

				listTime.add(new KdTree.XYZPoint(medallion,latitudeD,longitudeD,trip_duration_mins));
			}
		}
		kdtree = new KdTree<KdTree.XYZPoint>(listTime);
		file_reader.close();
		System.out.println("KD Tree Constructed");

	}
	public float travelTimeFromTrips(GraphNode node){
		float travel_time = Float.MAX_VALUE;

		KdTree.XYZPoint Vert = new KdTree.XYZPoint(null,node.getLat(),node.getLon(),0);
		//Search for nearest vertex
		Collection<KdTree.XYZPoint> near_bys = kdtree.nearestNeighbourSearch(Vert,0.06);
		Iterator<KdTree.XYZPoint> near_bys_itr =
				near_bys.iterator();
		while(near_bys_itr.hasNext()){
			KdTree.XYZPoint elt = near_bys_itr.next();
			if(elt.travel_time<travel_time)
				travel_time = elt.travel_time;
		}
		return travel_time;

	}

	public GraphNode getNNNode(KdTree.XYZPoint node){

		float travel_time = Float.MAX_VALUE;
		KdTree.XYZPoint neighbor = node;
		//Search for nearest vertex
		Collection<KdTree.XYZPoint> near_bys = kdtree.nearestNeighbourSearch(node,0.06);
		Iterator<KdTree.XYZPoint> near_bys_itr =
				near_bys.iterator();
		while(near_bys_itr.hasNext()){
			KdTree.XYZPoint elt = near_bys_itr.next();
			if(elt.travel_time<travel_time){
				travel_time = elt.travel_time;
				neighbor = elt;
			}
		}
		GraphNode nNode = new GraphNode(neighbor.x, neighbor.y, Long.parseLong(neighbor.linearID));
		return nNode;

	}

}
