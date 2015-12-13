package Generator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ListIterator;

import org.joda.time.LocalDateTime;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import Graph.GraphNode;
import Graph.RoadGraph;

public class AllNodeExtractor {
	
	public static void main(String[] args) throws FileNotFoundException, IOException, XmlPullParserException{
		System.out.println("Run started at"+ LocalDateTime.now() );
		PrintWriter intrMap = new PrintWriter("ObjectWarehouse/IntersectionMap_v1.csv");
		RoadGraph g = new RoadGraph();

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput ( new FileReader ("OSMData/NYCIntersections.osm"));

		//xpp.setInput ( new FileReader ("OSMData/NYC_sample.osm"));
		 
		LinkedList<GraphNode> intersections =g.osmIntersectionParser(xpp);
		
		ListIterator<GraphNode> nodeIterator_t = intersections.listIterator();
		//Adding vertices
		while (nodeIterator_t.hasNext()) {
			GraphNode single_node= nodeIterator_t.next();
			intrMap.println(single_node.getId()+","+single_node.getLat()+","+single_node.getLon());
		}
		intrMap.close();
	}

}
