package TestFunctions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.ClosestFirstIterator;

import Graph.GraphNode;
import NYCTripAnalyzer.FilterFunctions;

public class DriveTimeWalkTimeTest {
	
	
	public static void main(String[] args0) throws FileNotFoundException, IOException, ClassNotFoundException{

		PrintWriter DropOffPoints = new PrintWriter("Logs/DTvsWT_v1_5min_un_MANHATTAN.csv");
		PrintWriter statLogger = new PrintWriter("Logs/DTvsWT_logger_5_un_MANHATTAN.csv");

		System.out.println("De-Serialization started at"+ LocalDateTime.now() );

		ObjectInputStream oos_graph_read = new ObjectInputStream(new FileInputStream("ObjectWarehouse/UnDirectedWalkLimitGraphHashed_v1.obj"));

		//Construct Graph
		/*DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge> gr_t = new  
				DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge>(DefaultWeightedEdge.class);

		gr_t =  (DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge>) oos_graph_read.readObject();*/
		
		SimpleWeightedGraph <GraphNode,DefaultWeightedEdge> gr_t = new  
				SimpleWeightedGraph <GraphNode,DefaultWeightedEdge>(DefaultWeightedEdge.class);

		gr_t =  (SimpleWeightedGraph<GraphNode, DefaultWeightedEdge>) oos_graph_read.readObject();
		
		oos_graph_read.close();
		
		ObjectInputStream oos_graph_read_d = new ObjectInputStream(new FileInputStream("ObjectWarehouse/SpeedLimtGraphHashed_v1.obj"));

		//Construct Graph
		DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge> gr_t_d = new  
				DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge>(DefaultWeightedEdge.class);

		gr_t_d =  (DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge>) oos_graph_read_d.readObject();
		oos_graph_read_d.close();
		
		

		List<String>listIntersections = new ArrayList<String>();
		BufferedReader bfS = new BufferedReader(new FileReader("ObjectWarehouse/IntersectionMap_v1.csv"));
		String s = new String();
		while((s=bfS.readLine())!=null &&
				(s.length()!=0) ){
			String[] split_readline = s.split(",");
			listIntersections.add(split_readline[0].trim());
		}

		// Start BFS
		/*		GraphNode hub_node = new GraphNode();
		hub_node.setLat(40.7743819);
		hub_node.setLon(-73.8729252);
		hub_node.setId(-533655);*/
		int wt_greater_dt = 0;
		int dt_greater_wt = 0;
		int wt_greater_dt_2 = 0;
		int dt_greater_wt_2 = 0;
		int total_cases = 0;

		Set<GraphNode> vertex_set = gr_t.vertexSet();

		Iterator<GraphNode> vertex_itr = vertex_set.iterator();
		int ctr = 0;
		while(vertex_itr.hasNext()){

			StringBuilder tt = new StringBuilder();

			GraphNode vertex = vertex_itr.next();
			if(!FilterFunctions.inManhattanBoundingBox(vertex.getLat(), vertex.getLon())){
				continue;
			}
			ClosestFirstIterator<GraphNode, DefaultWeightedEdge> bfs= new 
					ClosestFirstIterator<GraphNode, DefaultWeightedEdge>(gr_t,vertex,5);

			GraphNode startNode = bfs.next();
			tt.append(startNode.getId()+"->"+bfs.getShortestPathLength(startNode)+"->0"+", ");
			while(bfs.hasNext()){
				GraphNode bfs_next_node = bfs.next();
				if(nodeIsIntersection(bfs_next_node,listIntersections)){
					double walk_dist = bfs.getShortestPathLength(bfs_next_node);
					//Calculate Drive Time
					DijkstraShortestPath<GraphNode, DefaultWeightedEdge> dsp_d = new DijkstraShortestPath<GraphNode, DefaultWeightedEdge>(
							gr_t_d, vertex, bfs_next_node);
					float driving_time_from_dropoff_B_to_dropoff_A = (float) dsp_d.getPathLength();
					
					tt.append(bfs_next_node.getId()+"->"+walk_dist+"->"+driving_time_from_dropoff_B_to_dropoff_A+", ");
					
					if(walk_dist>driving_time_from_dropoff_B_to_dropoff_A){
						wt_greater_dt++;
					}else{
						dt_greater_wt++;
					}
					
					if(walk_dist>2*driving_time_from_dropoff_B_to_dropoff_A){
						wt_greater_dt_2++;
					}else{
						dt_greater_wt_2++;
					}
					total_cases++;
					
				}
			}
			DropOffPoints.println(tt);
			System.out.println("Vertex Count -> "+ctr);
			ctr++;
		}
		statLogger.println("Cases where walking time is greater than driving time = "+wt_greater_dt);
		statLogger.println("Cases where walking time is lesser than driving time = "+dt_greater_wt);
		statLogger.println("Cases where walking time is greater than half driving time = "+wt_greater_dt_2);
		statLogger.println("Cases where walking time is lesser than driving time = "+dt_greater_wt_2);
		statLogger.println("Total cases = "+total_cases);

		DropOffPoints.close();
		statLogger.close();
	}

	private static boolean nodeIsIntersection(GraphNode bfs_next_node,
			List<String>listIntersections) {
		// TODO Auto-generated method stub
		if(listIntersections.contains(""+bfs_next_node.getId())){
			return true;
		}
		return false;
	}

}
