/**
 * Class to store nearest dropoff locations for a node
 * 
 * @author Sandeep Sasidharan
 *
 */
package Graph;

import java.util.List;
import java.util.Set;

public class DropoffStorage {
	
	private GraphNode node;
	private List<GraphNode> drivable_nodes; 
	
	public void setNode(GraphNode node){
		this.node = node;
	}
	
	public void setDrivable_nodes(List<GraphNode> nodes){
		this.drivable_nodes = nodes;
	}

}
