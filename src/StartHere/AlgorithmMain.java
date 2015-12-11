package StartHere;

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

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import Graph.GraphNode;
import Graph.Pair;
import Trip.KdTree;

public class AlgorithmMain {

	public static void main(String[] args0) throws FileNotFoundException, IOException, ClassNotFoundException{

		PrintWriter DropOffPoints = new PrintWriter("ObjectWarehouse/IntrMap_v1_10min.csv");

		System.out.println("De-Serialization started at"+ LocalDateTime.now() );

		ObjectInputStream oos_graph_read = new ObjectInputStream(new FileInputStream("ObjectWarehouse/WalkLimitGraphHashed_v1.obj"));

		//Construct Graph
		DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge> gr_t = new  
				DefaultDirectedWeightedGraph <GraphNode,DefaultWeightedEdge>(DefaultWeightedEdge.class);

		gr_t =  (DefaultDirectedWeightedGraph<GraphNode, DefaultWeightedEdge>) oos_graph_read.readObject();
		oos_graph_read.close();

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

		Set<GraphNode> vertex_set = gr_t.vertexSet();

		Iterator<GraphNode> vertex_itr = vertex_set.iterator();
		int ctr = 0;
		while(vertex_itr.hasNext()){

			StringBuilder tt = new StringBuilder();

			GraphNode vertex = vertex_itr.next();
			ClosestFirstIterator<GraphNode, DefaultWeightedEdge> bfs= new 
					ClosestFirstIterator<GraphNode, DefaultWeightedEdge>(gr_t,vertex,10);

			GraphNode startNode = bfs.next();
			tt.append(startNode.getId()+"->"+bfs.getShortestPathLength(startNode)+", ");
			while(bfs.hasNext()){
				GraphNode bfs_next_node = bfs.next();
				if(nodeIsIntersection(bfs_next_node,listIntersections)){
					double walk_dist = bfs.getShortestPathLength(bfs_next_node);
					tt.append(bfs_next_node.getId()+"->"+walk_dist+", ");
				}
			}
			DropOffPoints.println(tt);
			System.out.println("Vertex Count -> "+ctr);
			ctr++;
		}

		DropOffPoints.close();
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

