package nl.didactor.isbo;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.XMLErrorHandler;
import org.mmbase.util.XMLEntityResolver;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import java.text.SimpleDateFormat;


public class ISBOReader {
    private static Logger log = Logging.getLoggerInstance(ISBOReader.class.getName());

    private Cloud cloud;
    private NodeManager studentManager;
    private NodeManager classManager;
    private NodeManager educationManager;
    private DocumentBuilder docBuilder;
    
    public ISBOReader(Cloud cloud) {
        this.cloud = cloud;
        this.studentManager   = cloud.getNodeManager("people");
        this.classManager     = cloud.getNodeManager("classes");
        this.educationManager = cloud.getNodeManager("educations");
        
        docBuilder = DocumentReader.getDocumentBuilder();
    }

    public void parse( InputStream is ) throws Exception {
        processDoc(docBuilder.parse(is));
    }

    public void processDoc(Document doc) throws Exception {
        Element root = doc.getDocumentElement();
        NodeList studentList = root.getElementsByTagName("student-detail");
        for (int i=0; i < studentList.getLength(); i++) {
            setStudent(studentList.item(i));
        }
        
        NodeList classList = root.getElementsByTagName("class");
        for (int i=0; i < classList.getLength(); i++) {
            setClass(classList.item(i));
        }
    }
    
    private void setStudent(Node el) throws Exception {
        Map fields = new HashMap();
        NodeList nl = el.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == n.ELEMENT_NODE) {
                fields.put(n.getNodeName(),n.getFirstChild().getNodeValue().trim());
            }
        }
        String username = (String)fields.get("username");
        log.info("Processing student "+username);
        org.mmbase.bridge.Node student = getStudent(username,true);
        student.setValue("password",(String) fields.get("password"));
        student.setValue("firstname",(String) fields.get("firstname"));
        student.setValue("lastname",(String) fields.get("lastname"));
        student.setValue("suffix",(String) fields.get("suffix"));
        student.setValue("initials",(String) fields.get("initials"));
        student.setValue("address",(String) fields.get("address"));
        student.setValue("zipcode",(String) fields.get("zipcode"));
        student.setValue("city",(String) fields.get("city"));
        student.setValue("country",(String) fields.get("country"));
        student.setValue("telephone",(String) fields.get("telephone"));
        student.setValue("mobile",(String) fields.get("mobile"));
        student.setValue("email",(String) fields.get("email"));
        student.setValue("website",(String) fields.get("website"));
        student.setValue("externid",(String) fields.get("externid"));
        String birthdate = (String) fields.get("birthdate");
        if (birthdate.length() > 0) {
            student.setIntValue("dayofbirth", parseDate(birthdate));
        }
        student.commit();
    }

    private void setClass(Node el) throws Exception{
        Map fields = new HashMap();
        Set students = new HashSet();
        Set educations = new HashSet();
        NodeList nl = el.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == n.ELEMENT_NODE) {
                if ("students".equals(n.getNodeName())) {
                    NodeList sl = n.getChildNodes();
                    for (int j = 0; j < sl.getLength(); j++) {
                        Node s = sl.item(j);
                        if ("username".equals(s.getNodeName())) {
                            String username = s.getFirstChild().getNodeValue().trim();
                            students.add(username);
                        }
                    }
                }
                else if ("education".equals(n.getNodeName())) {
                    educations.add(n.getFirstChild().getNodeValue().trim());
                }
                else {
                    fields.put(n.getNodeName(),n.getFirstChild().getNodeValue().trim());
                }
            }
        }
        String name = (String) fields.get("name");
        log.info("Processing class "+name);
        org.mmbase.bridge.Node klas = getClass(name);
        klas.commit();
        org.mmbase.bridge.NodeIterator elist = klas.getRelatedNodes("educations").nodeIterator();
        while (elist.hasNext()) {
            org.mmbase.bridge.Node education = elist.nextNode();
            if (educations.contains(education.getStringValue("name"))) {
               educations.remove(education.getStringValue("name"));
            }
        }
        Iterator i = educations.iterator();
        while (i.hasNext()) {
            org.mmbase.bridge.Node education = getEducation((String)i.next());
            klas.createRelation(education,cloud.getRelationManager("related")).commit();
        }

        Map currentStudents = new HashMap();
        org.mmbase.bridge.NodeIterator si = cloud.getList(
                klas.getStringValue("number"),
                "classes,classrel,people",
                "classrel.number,people.username",
                null,
                null,
                null,
                null,
                true
        ).nodeIterator();
        while (si.hasNext()) {
            org.mmbase.bridge.Node s = si.nextNode();
            currentStudents.put(s.getStringValue("people.username"),s.getStringValue("classrel.number"));
        }
        Iterator studentIterator = students.iterator();
        while (studentIterator.hasNext()) {
            String sname = (String) studentIterator.next();
            if (! currentStudents.containsKey(sname) ) {
                klas.createRelation(getStudent(sname,false),cloud.getRelationManager("classrel")).commit();
            }
            else {
                currentStudents.remove(sname);
            }
        }
        Iterator removeIterator = currentStudents.values().iterator();
        while (removeIterator.hasNext()) {
            String relnum = (String) removeIterator.next();
             org.mmbase.bridge.Node n = cloud.getNode(relnum);
             if (n != null) {
                 n.delete(true);
             }
        }
    }

    private int parseDate(String date) {
        try {
            return (int) ((new SimpleDateFormat("yyyy-MM-dd")).parse(date).getTime() / 1000l);
        } catch (Exception e) {
            log.error("Can't parse date '"+date+"' (must be yyyy-MM-dd format), setting to 0");
            return 0;
        }
    }
    
    private org.mmbase.bridge.Node getStudent(String username, boolean create) throws Exception { 
        org.mmbase.bridge.NodeList slist = studentManager.getList("username='"+username+"'",null,null);
        org.mmbase.bridge.Node student = null;
        if (slist.size() == 0) {
            if (create) {
                student = studentManager.createNode();
                student.setValue("username",username);
            }
            else {
                throw new Exception("Can't find student '"+username+"'");
            }
        }
        else {
            student = slist.getNode(0);
        }
        return student;
    }

    private org.mmbase.bridge.Node getEducation(String name) throws Exception { 
        org.mmbase.bridge.NodeList slist = educationManager.getList("name='"+name+"'",null,null);
        if (slist.size() == 0) {
            throw new Exception("Can't find education with name '"+name+"'");
        }
        return slist.getNode(0); 
    }


    private org.mmbase.bridge.Node getClass(String name) { 
        org.mmbase.bridge.NodeList slist = classManager.getList("name='"+name+"'",null,null);
        org.mmbase.bridge.Node klass = null;
        if (slist.size() == 0) {
            klass = classManager.createNode();
            klass.setValue("name",name);
        }
        else {
            klass = slist.getNode(0);
        }
        return klass;
    }

}
