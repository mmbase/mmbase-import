package nl.eo.batchupload.connector;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JOptionPane;

import nanoxml.XMLElement;
import nl.eo.batchupload.Config;
import nl.eo.batchupload.gui.BatchUploadMain;
import nl.eo.batchupload.utils.Base64;

/**
 * Wrapper around the Dove Servlet from MMBase (altered version that
 * accepts base64 encoded binary values). It implements the
 * @see{ConnectorInterface} interface.
 * 
 * @author Johannes Verelst <johannes@verelst.net>
 * <b>TODO: escaping search strings</b>
 */
public class DoveWrapper implements ConnectorInterface {
    String doveLocation;
	private String username;
	private String password;
	private boolean isLoggedIn = false;

    public DoveWrapper() {
        doveLocation = Config.getValue("dovelocation");   
    }

	/**
	 * Validate user credentials against MMBase using the Dove Servlet
	 * @param username Username to validate
	 * @param password Password to validate
	 * @return empty string if successfull, error message otherwise
	 */   
   public String validate (String username, String password) {
   		XMLElement doveRequest = getAuthenticatedRequest(username, password);

		// Ugly hack, but apparently we need some kind of extra command, 
		// otherwise we cannot validate
		XMLElement dummyGetData = getNewXMLElement();
		dummyGetData.setName("getdata");
		XMLElement dummyObject = getNewXMLElement();
		dummyObject.setName("object");
		dummyObject.setAttribute("number", "2");
		dummyGetData.addChild(dummyObject);
		doveRequest.addChild(dummyGetData);

   		XMLElement response = issueRequest(doveRequest, false);

		boolean hasSecChild = false;
		Iterator children = response.getChildren().iterator();
   		while (children.hasNext()) {
   			XMLElement child = (XMLElement)children.next();
   			if (child.getName().equalsIgnoreCase("security")) {
				hasSecChild = true;
				Iterator secChildren = child.getChildren().iterator();
				while (secChildren.hasNext()) {
					XMLElement secChild = (XMLElement)secChildren.next();
	   				if (secChild.getName().equalsIgnoreCase("error")){
		   				return secChild.getContent();
					}
   				}
   			}
   		}
   		if (hasSecChild) {
	   		this.username = username;
	   		this.password = password;
	   		this.isLoggedIn = true;
			return "";
   		} else {
   			return "Cannot establish connection with database";
   		}
   }

    public int create(Hashtable newObject) {
        XMLElement request = getAuthenticatedRequest(username, password);

        XMLElement putImage = getNewXMLElement();
        putImage.setName("put");
        XMLElement original = getNewXMLElement();
        original.setName("original");
        putImage.addChild(original);
        
        XMLElement orignew = getNewXMLElement();
        orignew.setName("new");
        
        XMLElement object = getNewXMLElement();
        object.setName("object");
        object.setAttribute("number", "hoepsefloeps");
        object.setAttribute("type", newObject.get("_type"));
        object.setAttribute("status", "new");
        
        Enumeration e = newObject.keys();
        while (e.hasMoreElements()) {
            String fieldName = (String)e.nextElement();
            if (!fieldName.startsWith("_")) {
                XMLElement field = getNewXMLElement();
                field.setName("field");
                field.setAttribute("name", fieldName);
                Object data = newObject.get(fieldName);
                if (data instanceof byte[]) {
                    String encoded = Base64.encode((byte[])data);
                    field.setContent(encoded);
                    field.setAttribute("encoding", "base64");
                } else {
                    field.setContent((String)newObject.get(fieldName));
                }
                object.addChild(field);
            }
        }

        orignew.addChild(object);
        putImage.addChild(orignew);
        request.addChild(putImage);
 
        XMLElement response = issueRequest(request, true); 
        XMLElement needed = getFirstChild(response, "put");
        needed = getFirstChild(needed, "new");
        needed = getFirstChild(needed, "object");
        return Integer.parseInt((String)needed.getAttribute("number"));
    }

    public void relate(int source, int destination, String type) {
        XMLElement request = getAuthenticatedRequest(username, password);

        XMLElement putImage = getNewXMLElement();
        putImage.setName("put");
        XMLElement original = getNewXMLElement();
        original.setName("original");
        putImage.addChild(original);
        
        XMLElement orignew = getNewXMLElement();
        orignew.setName("new");
        
        XMLElement object = getNewXMLElement();
        object.setName("relation");
        object.setAttribute("number", "hoepsefloeps");
        object.setAttribute("status", "new");
        object.setAttribute("role", type);
        object.setAttribute("source", "" + source);
        object.setAttribute("destination", "" + destination);

        orignew.addChild(object);
        putImage.addChild(orignew);
        request.addChild(putImage);
        XMLElement response = issueRequest(request, true); 

    }

	/**
	 * Search MMBase for news objects matching the query string
	 * @param querystring The query to issue to MMBase
	 * @return Vector containing Hashtable's for the matching objects
	 */
	public Vector search(String querystring) {
		XMLElement request = getAuthenticatedRequest(username, password);
		XMLElement listRequest = getNewXMLElement();
		Vector results = new Vector();
		
		listRequest.setName("getlist");

		XMLElement query = getNewXMLElement();
		query.setName("query");
		query.setAttribute("xpath", "/*@imagegallery");
		query.setAttribute("where", querystring);

		listRequest.addChild(query);
		request.addChild(listRequest);
		
		XMLElement result = issueRequest(request, true);

		listRequest = getFirstChild(result, "getlist");
		query = getFirstChild(listRequest, "query");
		Iterator children = query.getChildren().iterator();
		while (children.hasNext()) {
			XMLElement child = (XMLElement)children.next();
			
			Hashtable h = new Hashtable();
			h.put("_number", child.getAttribute("number"));

			Iterator fields = child.getChildren().iterator();
			while (fields.hasNext()) {
				XMLElement field = (XMLElement)fields.next();
				h.put(field.getAttribute("name"), field.getContent());
			}
			results.add(h);
		}
		return results;
	}


    /**
     * Helper function that returns a dove request that contains the correct credentials
     * @param username Username of the user
     * @param password Password of the user
     */   
    private XMLElement getAuthenticatedRequest(String username, String password) {
        XMLElement doveRequest =getNewXMLElement();
        doveRequest.setName("request");
        
        XMLElement securityElement =getNewXMLElement();
        securityElement.setName("security");
        securityElement.setAttribute("name", username);
        securityElement.setAttribute("password", password);
        
        doveRequest.addChild(securityElement);
        return doveRequest;
   }

    /**
     * Issue a request on the Dove servlet. It will return the response from Dove
     * @param request The XML request to Dove
     * @return XMLElement containing the response from Dove
     */   
   private XMLElement issueRequest(XMLElement request, boolean mustSucceed) {
        XMLElement returnElement =getNewXMLElement();
        boolean finished = false;
        boolean firsttry = true;
        while (!finished) {
            try {
                URL url = new URL(doveLocation);
                URLConnection urlConn = url.openConnection();
                urlConn.setDoInput (true);
                urlConn.setDoOutput (true);
                urlConn.setUseCaches (false);
                urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String content = request.toString();
                PrintWriter printout = new PrintWriter(urlConn.getOutputStream(), true);
                content = URLEncoder.encode(content);
                
                printout.print("xml=" + content + "\r\n");
                printout.flush();
                printout.close();
    
                BufferedReader inreader = new BufferedReader(new InputStreamReader(urlConn.getInputStream ()));
    
                String data = "";
                String line;
                while ((line = inreader.readLine()) != null) {
                    data += line;
                }
        
                returnElement.parseString(data);
                inreader.close();   
                finished = true;
            } catch (Exception e) {
                if (firsttry) {
                    String message = "";
                    if (e instanceof ConnectException) {
                        message = "Cannot connect to host.\nErrormessage: " + e.getMessage();
                    } else if (e instanceof NoRouteToHostException) {
                        message = "No route to host.\nErrormessage: " + e.getMessage();
                    } else if (e instanceof SocketException) {
                        message = "Socket exception.\nErrormessage: " + e.getMessage();
                    } else if (e instanceof UnknownHostException) {
                        message = "Cannot resolve host.\nErrormessage: " + e.getMessage();
                    } else {
                        message = "Unknown exception: " + e.toString() + "\n";
                        ByteArrayOutputStream byos = new ByteArrayOutputStream();
                        e.printStackTrace(new PrintStream(byos));
                        message += byos.toString();

                    JOptionPane.showMessageDialog(BatchUploadMain.getDesktop(),
                        message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    }
                }
                firsttry = false;                
                if (!mustSucceed) {
                    return returnElement;
                }
                BatchUploadMain.setStatus(e.getMessage());
                try {
                    Thread.currentThread().sleep(10 * 1000);
                } catch (Exception e2) {
                    e2.printStackTrace();   
                }
                //e.printStackTrace();
            }  
        }
        return returnElement;
    }

	/**
	 * Small helper function that will return the first child with name 'type' 
	 * from an XMLElement
	 * @param e XMLElement to search
	 * @param type Name of the element to find
	 */
	private XMLElement getFirstChild(XMLElement e, String type) {
		Iterator children = e.getChildren().iterator();
   		while (children.hasNext()) {
   			XMLElement child = (XMLElement)children.next();
   			if (child.getName().equalsIgnoreCase(type)) {
   				return child;
   			}
   		}
   		
   		return getNewXMLElement();
	}

	/**
	 * Return a new case sensitive XMLElement
	 */
	private XMLElement getNewXMLElement() {
		return new XMLElement(new Hashtable(),  false, false);
	}
}
