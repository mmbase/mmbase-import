package nl.didactor.component.proactivemail;

/**
 * EmailTemplateToUsers class
 * 
 * @author Goran Kostadinov     (Levi9 Balkan Global Sourcing)
 *
 * @version $Id$
 */

import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.ContextProvider;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.w3c.dom.Document; 
import org.w3c.dom.Element; 
import org.w3c.dom.Node; 
import org.w3c.dom.NodeList; 
import nl.didactor.mail.ExtendedJMSendMail;

public class EmailTemplateToUsers {
    private static Logger log = Logging.getLoggerInstance(EmailTemplateToUsers.class);
    
    private String templateName = "";
    protected static String internalUrl = ""; 
    private String emailSubject = "";
    private String emailBody = "";
    private String emailFrom = "";
    private long startTime = System.currentTimeMillis()/1000;
    private long endTime = System.currentTimeMillis()/1000;
    private int messageCount = 0;
    
    public String getEmailSubject() {
        return this.emailSubject;
    }
    
    public String getEmailBody() {
        return this.emailBody;
    }

    public String getEmailFrom() {
        return this.emailFrom;
    }

    public EmailTemplateToUsers(String templateName) {
        if ( templateName != null ) 
            this.templateName = templateName;
    }

    // purpose is to get template from db, and all related people to this template
    //  relation via provider, education, classes, role, people. People will be filtered
    
    public Document getRelatedPeople(String url) {
        return this.getRelatedPeople(url, null);
    }
        
    public Document getRelatedPeople(String url, String param) {
        Document result = null;
        if (  templateName.length() == 0 || url == null ) 
            return result;
        if ( param == null ) param = "";
        if ( param.trim().length() > 0 && param.trim().charAt(0) != '&' )
            param = "&"+param;
        try {
          String URL = EmailTemplateToUsers.internalUrl + url + "?" +
                       "username=admin&" +
                       "password=admin2k&"+
                       "authenticate=plain&"+
                       "command=login&"+
                       "templatename="+this.templateName + 
                       param;
          XMLReader reader = XMLReaderFactory.createXMLReader();
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();
          result = db.parse(URL);
          
      } catch (Exception e) {
          log.error("Can't get proactivemail users for template '"+this.templateName+"'.\r\n    "+e.toString());
      }
      return result;
    }
    
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            Node n = el.getFirstChild();
            if ( n != null )
                textVal = n.getNodeValue();
        }

        return textVal;
    }    
    
    public void sendEmailToUsers(Document d) {
        if ( d == null ) return;

        this.startTime = System.currentTimeMillis()/1000;
        
        try {
            Cloud cloud = ContextProvider.getCloudContext("local").getCloud("mmbase");
            String usernameSystem = "system", admin = "admin";
            
            Element elRoot = d.getDocumentElement();
            
            this.emailSubject = this.getTextValue(elRoot, "subject");
            this.emailBody = this.getTextValue(elRoot, "body");
            this.emailFrom = this.getTextValue(elRoot, "from");
            
            if ( this.emailFrom == null ) this.emailFrom = "";

            NodeList nlUsers = elRoot.getElementsByTagName("users");
            if ( nlUsers.getLength() <= 0 ) return;
            Element elUsers = (Element)nlUsers.item(0);
            
            NodeList nlUser = elUsers.getElementsByTagName("user");
            for ( int n = 0; n < nlUser.getLength(); n++ ) {
                // get 'users' node
                Element elUser = (Element)nlUser.item(n);

                // these can be null, be carefull in below code 
                String firstname = this.getTextValue(elUser, "firstname");
                String lastname = this.getTextValue(elUser, "lastname");
                String username = this.getTextValue(elUser, "username");
                String email = this.getTextValue(elUser, "email");

                if (  email != null && email.length() > 0 && 
                      this.emailSubject != null && this.emailSubject.length() > 0 && 
                      this.emailBody != null && this.emailBody.length() > 0) {
                    org.mmbase.bridge.Node message = cloud.getNodeManager("emails").createNode();
                    if ( message != null ) {
                        message.setStringValue("from", this.emailFrom);
                        message.setStringValue("to", "g.kostadinov@levi9.com"); // send on test account for now
                        message.setStringValue("subject", this.emailSubject);
                        message.setStringValue("body", this.emailBody);
                        message.setIntValue("date", (int) (System.currentTimeMillis() / 1000));
                        message.commit();
                        message.setIntValue("type", 1);
                        message.commit();
                        this.messageCount++;
                     }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing XML for template '"+this.templateName+"'.\r\n    "+e.toString());
        } finally {
            this.endTime = System.currentTimeMillis()/1000;
            if ( this.messageCount > 0 )
                this.setBatches();
        }
    }
    

    public long getBatches() {
        try {
            MMBase mmb = (MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
            MMObjectBuilder batchesBuilder = mmb.getBuilder("proactivemailbatches");
            NodeSearchQuery nsQuery = new NodeSearchQuery(batchesBuilder);
            StepField nameField = nsQuery.getField(batchesBuilder.getField("name"));
            BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(nameField, this.templateName);
            nsQuery.setConstraint(constraint);
            List batchesList = batchesBuilder.getNodes(nsQuery);
            if ( batchesList.size() > 0 ) {
                MMObjectNode batchNode  = (MMObjectNode) batchesList.get(batchesList.size()-1);
                int batchStartTime = batchNode.getIntValue("start_time");
                int batchEndTime = batchNode.getIntValue("end_time");
                return batchEndTime;
            }
        } catch ( Exception e ) {
            log.error("Error while getting batches for template '"+this.templateName+"'.\r\n    "+e.toString());
        }
        return -1;
    }

    public void setBatches() {
        try {
            String username = "system";
            String admin = "admin";
            MMBase mmb = (MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
            MMObjectBuilder batchesBuilder = mmb.getBuilder("proactivemailbatches");
            NodeSearchQuery nsQuery = new NodeSearchQuery(batchesBuilder);
            StepField nameField = nsQuery.getField(batchesBuilder.getField("name"));
            BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(nameField, this.templateName);
            nsQuery.setConstraint(constraint);
            List batchesList = batchesBuilder.getNodes(nsQuery);
            MMObjectNode batchNode = null;
            if ( batchesList.size() > 0 ) 
                batchNode  = (MMObjectNode) batchesList.get(batchesList.size()-1);
            else 
                batchNode = batchesBuilder.getNewNode(admin);
            if ( batchNode != null ) {
                batchNode.setValue("name", this.templateName);
                batchNode.setValue("start_time", this.startTime);
                batchNode.setValue("end_time", this.endTime);
                batchNode.setValue("sent_messages", this.messageCount);
            }
            if ( batchesList.size() > 0 )
                batchNode.commit();
            else 
                batchesBuilder.insert(admin, batchNode);
                
        } catch ( Exception e ) {
            log.error("Error while setting batches for template '"+this.templateName+"'.\r\n    "+e.toString());
        }
    }
    
}
