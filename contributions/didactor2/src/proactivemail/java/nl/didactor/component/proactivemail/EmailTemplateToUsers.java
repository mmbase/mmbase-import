package nl.didactor.component.proactivemail;

/**
 * EmailTemplateToUsers class
 * 
 * @author Goran Kostadinov     (Levi9 Balkan Global Sourcing)
 *
 * @version $Id$
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.ContextProvider;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import nl.didactor.mail.ExtendedJMSendMail;

public class EmailTemplateToUsers {
    private static Logger log = Logging.getLoggerInstance(EmailTemplateToUsers.class);
    
    protected static String internalUrl = ""; 
    private String emailSubject = "";
    private String emailBody = "";
    private boolean emailActive = true;
    private Date emailDate = new Date();
    private HashMap users;
    private String templateName = "";
    private long startTime = System.currentTimeMillis()/1000;
    private long endTime = System.currentTimeMillis()/1000;
    private int messageCount = 0;
    
    public String getEmailSubject() {
        return this.emailSubject;
    }
    
    public String getEmailBody() {
        return this.emailBody;
    }
    
    public boolean getEmailActive() {
        return this.emailActive;
    }
    
    public Date getEmailDate() {
        return this.emailDate;
    }
    
    public Collection getUsers() {
        return this.users.values();
    }
    
    public EmailTemplateToUsers(String templateName) {
        if ( templateName != null ) 
            this.templateName = templateName;
    }

    // purpose is to get template from db, and all related people to this template
    //  relation via provider, education, classes, role, people. People will be filtered
    public Document getRelatedPeople(String url) {
        Document result = null;
        if (  templateName.length() == 0 ) 
            return result;
        if ( url == null )
            url = "/proactivemail/getusers.jsp";
        try {
          URL didactor = new URL(EmailTemplateToUsers.internalUrl+
                                 url+"?"+
                                 "username=admin&" +
                                 "password=admin2k&"+
                                 "authenticate=plain&"+
                                 "command=login&"+
                                 "templatename="+this.templateName);
          SAXReader reader = new SAXReader();
          result = reader.read(didactor);
      } catch (Exception e) {
          log.error("Can't get proactivemail users for template '"+this.templateName+"'.\r\n    "+e.toString());
      }
      return result;
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

    public void sendEmailToUsers(Document d) {
        if ( d == null ) return;
        
        this.startTime = System.currentTimeMillis()/1000;
        Element root = d.getRootElement();
        
        try {
            MMBase mmb = (MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
            Cloud cloud = ContextProvider.getCloudContext("local").getCloud("mmbase");
            String subject = "";
            String from = "";
            String body = "";
            ArrayList userAddress = new ArrayList();
            Iterator elements = root.elementIterator();
            while (elements.hasNext()) {
                Element element = (Element) elements.next();
                if( element.getName().equals( "subject" ) )
                    subject = element.getText();
                else if( element.getName().equals( "body" ) )
                    body = element.getText();
                else if( element.getName().equals( "from" ) )
                    from = element.getText();
                else if ( element.getName().equals( "users" ) )  {
                    Iterator users = element.elementIterator();
                    int i = 1;
                    while (users.hasNext()) {
                        Element user = (Element) users.next();
                        if( user.getName().equals( "user" ) ) {
                            String firstname = "";
                            String lastname = "";
                            String username = "";
                            String email = "";
                            Iterator userInfos = user.elementIterator();
                            while (userInfos.hasNext()) {
                                Element userInfo = (Element) userInfos.next();
                                if( userInfo.getName().equals( "firstname" ) )
                                    firstname = userInfo.getText();
                                else if( userInfo.getName().equals( "lastname" ) )
                                    lastname = userInfo.getText();
                                else if( userInfo.getName().equals( "username" ) )
                                    username = userInfo.getText();
                                else if( userInfo.getName().equals( "email" ) )
                                    email = userInfo.getText();
                            }
                            if ( email.length() > 0 && subject.length() > 0 && body.length() > 0) {
                                // Wait for mmbase to be up and running.
                                String usernameSystem = "system";
                                String admin = "admin";
                                
                                Node message = cloud.getNodeManager("emails").createNode();
                                
                                if ( message != null ) {
                                    message.setStringValue("from", "Didactor");
                                    message.setStringValue("to", "g.kostadinov@levi9.com");
                                    message.setStringValue("subject", "Srecno");
                                    message.setStringValue("body", "Ovo je test poruka. Ako je vidis, znaci da je uspelo. ProActiveMail");
                                    message.setIntValue("type", 1);
                                    message.setStringValue("cc", "");
                                    message.setStringValue("headers", "");
                                    message.setIntValue("date", (int) (System.currentTimeMillis() / 1000));
                                    message.commit();
                                    this.messageCount++;
                                    
                                    //DidactorProActiveMail.sendEmail(message);
                                }
                            }
                        }
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
}
