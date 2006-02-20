package nl.didactor.isbo;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ISBOReader {
    private static Logger log = Logging.getLoggerInstance(ISBOReader.class.getName());

    private Cloud cloud;
    private NodeManager peopleManager;
    private NodeManager classManager;
    private NodeManager educationManager;
    private NodeManager roleManager;
    private DocumentBuilder docBuilder;
    private org.mmbase.bridge.Node studentRoleNode;
    private org.mmbase.bridge.Node teacherRoleNode;
    
    public ISBOReader(Cloud cloud) {
        this.cloud = cloud;
        this.peopleManager   = cloud.getNodeManager("people");
        this.classManager     = cloud.getNodeManager("classes");
        this.educationManager = cloud.getNodeManager("educations");
        this.roleManager		= cloud.getNodeManager("roles");
        this.studentRoleNode = roleManager.getList("name='student'",null,null).getNode(0);
        this.teacherRoleNode = roleManager.getList("name='teacher'",null,null).getNode(0);
        docBuilder = DocumentReader.getDocumentBuilder();
    }

    public void parse( InputStream is ) throws Exception {
        processDoc(docBuilder.parse(is));
    }

    public void processDoc(Document doc) throws Exception {
        Element root = doc.getDocumentElement();
        NodeList studentList = root.getElementsByTagName("person");
        for (int i=0; i < studentList.getLength(); i++) {
            setPerson(studentList.item(i));
        }
        
        NodeList classList = root.getElementsByTagName("class");
        for (int i=0; i < classList.getLength(); i++) {
            setClass(classList.item(i));
        }
    }
    
    private void setPerson(Node el) throws Exception {
        Map fields = new HashMap();
        NodeList nl = el.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == n.ELEMENT_NODE) {
            	String fieldname = n.getNodeName();
            	Node child = n.getFirstChild();
            	String value = child == null ? null : child.getNodeValue().trim();
            	fields.put(fieldname,value);
            }
        }
        String username = (String)fields.get("username");
        if (username == null) {
        	throw new Exception("Can't insert a person without a username");
        }
        log.info("Processing person "+username);
        org.mmbase.bridge.Node student = getPerson(username,true);
        String password = (String) fields.get("password");
        if (password != null) {
        	student.setValue("password",(String) fields.get("password"));
        }
        
        // set the role (if specified)
        // a student role implies NO other roles - other roles will be removed
        // a teacher role will add the teacher role if it hasn't been added already, but
        // will NOT remove other roles
        
        String role = (String) fields.get("role");
        if ("student".equals(role)) {
        	boolean alreadyHasRole = false;
        	org.mmbase.bridge.NodeIterator ri = cloud.getList(
                    student.getStringValue("number"),
                    "people,related,roles",
                    "related.number,roles.name",
                    null,
                    null,
                    null,
                    null,
                    true
            ).nodeIterator();
        	while (ri.hasNext()) {
        		org.mmbase.bridge.Node rr = ri.nextNode();
        		if (rr.getStringValue("name").equals("student")) {
        			alreadyHasRole = true;
        		}
        		else {
        			cloud.getNode(rr.getStringValue("related.number")).delete();
        		}
        	}
        	if (!alreadyHasRole) {
        		student.createRelation(studentRoleNode,cloud.getRelationManager("related")).commit();
        	}
        }
        else if ("teacher".equals(role)) {
        	boolean alreadyHasRole = cloud.getList(
                    student.getStringValue("number"),
                    "people,related,roles",
                    "related.number,roles.name",
                    "roles.name='teacher'",
                    null,
                    null,
                    null,
                    true
            ).size() > 0;
        	if (!alreadyHasRole) {
        		student.createRelation(teacherRoleNode,cloud.getRelationManager("related")).commit();
        	}
        }
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
        if (birthdate != null) {
            student.setIntValue("dayofbirth", parseDate(birthdate));
        }
        student.commit();
    }

    private void setClass(Node el) throws Exception{
        Map fields = new HashMap();
        Set students = new HashSet();
        Set newEducations = new HashSet();
        NodeList nl = el.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == n.ELEMENT_NODE) {
            	String fieldname = n.getNodeName();
                if ("people".equals(fieldname)) {
                    NodeList sl = n.getChildNodes();
                    for (int j = 0; j < sl.getLength(); j++) {
                        Node s = sl.item(j);
                        if ("username".equals(s.getNodeName())) {
                        	Node child = s.getFirstChild();
                        	String value = child == null ? null : child.getNodeValue().trim();
                            students.add(value);
                        }
                    }
                }
                else if ("education".equals(fieldname)) {
                	Node child = n.getFirstChild();
                	String value = child == null ? null : child.getNodeValue().trim();
                	if (child == null || value == null) {
                		throw new Exception("No education specified for class!");
                	}
                    newEducations.add(value);
                }
                else {
                	Node child = n.getFirstChild();
                	String value = child == null ? null : child.getNodeValue().trim();
                    fields.put(fieldname,value);
                }
            }
        }
        String name = (String) fields.get("name");
        log.info("Processing class "+name);
        org.mmbase.bridge.Node klas = getClass(name);
        klas.commit();
        org.mmbase.bridge.NodeIterator elist = klas.getRelatedNodes("educations").nodeIterator();
        Set currentEducations = new HashSet();
        while (elist.hasNext()) {
            String education = elist.nextNode().getStringValue("name").trim().toLowerCase();
        	log.info("class already has a relation with education '"+education+"'");
        	currentEducations.add(education);
        }
        Iterator i = newEducations.iterator();
        while (i.hasNext()) {
        	String ename = ((String) i.next()).trim();
        	if (!currentEducations.contains(ename.toLowerCase())) {
        		org.mmbase.bridge.Node education = getEducation(ename);
        		log.info("coupling class to education '"+education.getStringValue("name")+"' ("+ename+")");
        		klas.createRelation(education,cloud.getRelationManager("classrel")).commit();
        	}
        }

        Map currentStudents = new HashMap();
        org.mmbase.bridge.NodeIterator si = cloud.getList(
                klas.getStringValue("number"),
                "classes,classrel,people,",
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
                klas.createRelation(getPerson(sname,false),cloud.getRelationManager("classrel")).commit();
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
        
        org.mmbase.bridge.NodeList eventNodes = klas.getRelatedNodes("mmevents");
        org.mmbase.bridge.Node eventNode = null;
        if (eventNodes.size() > 0) {
        	eventNode = eventNodes.getNode(0);
        }
        else {
        	eventNode = cloud.getNodeManager("mmevents").createNode();
        	eventNode.commit();
        	klas.createRelation(eventNode,cloud.getRelationManager("related")).commit();
        }
        eventNode.setIntValue("start",parseDate((String) fields.get("startdate")));
        eventNode.setIntValue("stop",parseDate((String) fields.get("enddate")));
      	eventNode.commit();
    }

    private int parseDate(String date) {
        try {
            return (int) ((new SimpleDateFormat("yyyy-MM-dd")).parse(date).getTime() / 1000l);
        } catch (Exception e) {
            log.error("Can't parse date '"+date+"' (must be yyyy-MM-dd format), setting to 0");
            return 0;
        }
    }
    
    private org.mmbase.bridge.Node getPerson(String username, boolean create) throws Exception { 
        org.mmbase.bridge.NodeList slist = peopleManager.getList("username='"+username+"'",null,null);
        org.mmbase.bridge.Node student = null;
        if (slist.size() == 0) {
            if (create) {
                student = peopleManager.createNode();
                student.setValue("username",username);
                student.commit();
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
