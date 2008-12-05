package nl.teleacnot.mmbase.nebo;

import java.util.*;
import java.text.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.mmbase.util.xml.DocumentReader;

/**
 * Imports streams found in a xml to 't_stream' nodes in MMBase.
 *
 * @author Andr\U00e9 vanToly &lt;andre@toly.nl&gt;
 *
 */
public final class Streams {
    
    private static final Logger log = Logging.getLoggerInstance(Streams.class);
    
    /**
     * Does the actual importing of the xml in MMBase. 
     * Imports all streams where 'aflid' does not match field 'id' of 't_stream'.
     *
     * @param doc   the root element of the xml to import
     * @return 't_stream' nodes that were imported
     */
    public static NodeList import2MMBase(Document doc) {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null); // using class security
		NodeManager nm = cloud.getNodeManager("t_stream");
        NodeList t_streams = nm.createNodeList();
        
        org.w3c.dom.NodeList streams = doc.getElementsByTagName("stream");
	    log.debug("nr of streams: " + streams.getLength());
		
		for (int i = 0; i < streams.getLength(); i++) {
			Element stream = (Element) streams.item(i);
            
            String aflid = stream.getAttribute("aflid");
            Element titleElement = (Element) stream.getElementsByTagName("title").item(0);
            String title = DocumentReader.getNodeTextValue(titleElement);
            log.debug("Stream aflid: "+ aflid + ", title: " + title);
            
            if (!"".equals(aflid) && aflid != null) {
    			
    			NodeList aflidsCheck = nm.getList("id='" + aflid +"'", null, null);
    			if (aflidsCheck.size() == 0) {
        			Element introElement = (Element) stream.getElementsByTagName("intro").item(0);
        			Element bodyElement = (Element) stream.getElementsByTagName("body").item(0);
                    Element patternElement = (Element) stream.getElementsByTagName("pattern").item(0);
        			//Element neboidElement = (Element) stream.getElementsByTagName("neboid").item(0);
        			//Element fragmentElement = (Element) stream.getElementsByTagName("fragment").item(0);
        			//Element availabiltyElement = (Element) stream.getElementsByTagName("availabilty").item(0);
        			Element beginElement = (Element) stream.getElementsByTagName("begin").item(0);
        			Element endElement = (Element) stream.getElementsByTagName("end").item(0);
        			Element showitElement = (Element) stream.getElementsByTagName("showit").item(0);

                    String intro = DocumentReader.getNodeTextValue(introElement);
                    String body = DocumentReader.getNodeTextValue(bodyElement);
                    String pattern = DocumentReader.getNodeTextValue(patternElement);
                    String begin = DocumentReader.getNodeTextValue(beginElement);
                    String end = DocumentReader.getNodeTextValue(endElement);
                    String showit = DocumentReader.getNodeTextValue(showitElement);
                    
                    log.debug("New stream aflid: "+ aflid + ", title: " + title);
			
			        Node n = nm.createNode();
			        n.setStringValue("id", aflid);
			        n.setStringValue("title", title);
			        n.setStringValue("intro", intro);
			        n.setStringValue("body", body);
			        n.setStringValue("pattern", pattern);			        
			        n.setDateValue("begin", formatDate(begin));
			        if (!"".equals(end)) n.setDateValue("end", formatDate(end));    // anders default
			        if (!"".equals(showit)) n.setStringValue("showit", showit);     // anders default
			        
			        n.commit();
			        t_streams.add(n);
			        
			        // relate to episode(s) if found
			        NodeList l = relate2Episode(cloud, n);
			        
			        
    			}
                
            }
		}
		
		return t_streams;
    }
    
    /**
     * Finds a 't_episode' to relate 't_stream' to when both their NEBO id's match.
     *
     * @param cloud     MMBase cloud
     * @param stream    the stream that's imported and can be related
     * @return the episode node(s) the stream was related to
     */
    private static NodeList relate2Episode(Cloud cloud, Node stream) {
        NodeManager nm = cloud.getNodeManager("t_episode");
        RelationManager rm = cloud.getRelationManager("posrel");
        // NodeList episodes = nm.getList("neboid='" + stream.getStringValue("id") +"'", null, null);
        // TODO: create a 'neboid' field in t_episode, using 'station' for now.
        NodeList episodes = nm.getList("station='" + stream.getStringValue("id") +"'", null, null);
        NodeIterator ni = episodes.nodeIterator();
		while (ni.hasNext()) {
		    Node episode = ni.nextNode();
		    Relation posrel = rm.createRelation(episode, stream);
			posrel.setIntValue("pos", 1);
			posrel.commit();
    		if (log.isDebugEnabled()) 
    		    log.debug("related stream nr '" + stream.getNumber() + "' to episode nr '" + episode.getNumber() + "'");
		}
		return episodes;
    }
    
    /**
     * Parses a String and returns a Date. Returns the current datetime when parsing fails.
     *
     * @param  str String of format '2007-08-19 23:50:00'
     * @return the date present in the String or the current datetime if it fails
     */
    private static Date formatDate(String str) {
        log.debug("parsing: " + str);
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        try {
          date = df.parse(str); 
        }
        catch (ParseException dfe) { 
            log.error("Error parsing '" + str + "' : " + dfe);
        }
        return date;
    }

    
}
