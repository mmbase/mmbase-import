package nl.didactor.util;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.functions.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * @javadoc
 * @version $Id: ClassRoom.java,v 1.10 2008-09-25 16:26:56 michiel Exp $
 */
public class ClassRoom extends FunctionProvider {
    private static final Logger log = Logging.getLoggerInstance(ClassRoom.class);


    {

        addFunction(new NodeFunction("hasrole",
                                    new Parameter[] {
                                        Parameter.NODE,
                                        new Parameter("role", String.class, true),
                                        new Parameter("education", Integer.class, true)
                                    },
                                     ReturnType.BOOLEAN) {
                protected Object getFunctionValue(Node node, Parameters parameters) {
                    MMObjectNode mmnode = MMBase.getMMBase().getBuilder("people").getNode(node.getNumber());
                    return hasRole(mmnode, parameters.getString("role"), (Integer) parameters.get("education"), node.getCloud());
                }
            });
        addFunction(new SetFunction("getTeachers", new Parameter[] { Parameter.NODE }, getClass()));
        addFunction(new SetFunction("getStudents", new Parameter[] { Parameter.NODE }, getClass()));
        addFunction(new SetFunction("getCoaches", new Parameter[] { Parameter.NODE }, getClass()));
    }


    public final static int ALL_CLASSES = -1;

    /**
     * Returns a List of people (Nodes) with a given role
     * related to the class, sorted by name
     *
     * This does not included people related via work-groups?
     *
     * @param klass the class's Node
     * @param role one role's name
     * @return the related People as a list of Nodes
     */
    public static List<Node> getPeople(Node klass, String role) {
        List<Node> list = new ArrayList<Node>();
        NodeIterator people = klass.getCloud().getList(
                klass.getStringValue("number"),
                "classes,classrel,people,related,roles",
                "roles.name,people.number,people.firstname,people.lastname",
                "roles.name='" + role + "'",
                "people.lastname,people.firstname",
                null,
                null,
                true
        ).nodeIterator();

        while (people.hasNext()) {
            list.add(people.nextNode().getNodeValue("people.number"));
        }
        log.debug("Found " + list + " for " + klass.getNumber() + " " + role);
        return list;
    }

    /**
     * Same as getPeople, but only returns teachers
     * @param klass
     * @return List of teacher Nodes
     */
    public static List<Node> getTeachers(Node klass) {
        return getPeople(klass, "teacher");
    }

    /**
     * Same as getPeople, but only returns students.
     * @param klass
     * @return List of student Nodes
     */

    public static List<Node> getStudents(Node klass) {
        return getPeople(klass, "student");
    }
    public static List<Node> getCoaches(Node klass) {
        return getPeople(klass, "coach");
    }

    public static Date getStartDate(Node klass) {
        Node event = klass.getRelatedNodes("mmevents").getNode(0);
        return new Date(event.getIntValue("start") * 1000L);
    }

    public static Date getEndDate(Node klass) {
        Node event = klass.getRelatedNodes("mmevents").getNode(0);
        return new Date(event.getIntValue("stop") * 1000L);
    }


    public static Node getClassRel(Node klass, Node student) {
        NodeList rels = klass.getCloud().getList(
                klass.getStringValue("number"),
                "classes,classrel,people",
                "people.number,classrel.number",
                "people.number="+student.getNumber(),
                null,
                null,
                null,
                true
        );
        if (rels.size() > 0) {
            return rels.getNode(0).getNodeValue("classrel.number");
        }
        return null;
    }



    public static Collection<String> getRoles(Node personnode, int educationno) {
        return getRoles(MMBase.getMMBase().getBuilder("people").getNode(personnode.getNumber()), educationno, personnode.getCloud());
    }

    /**
     * Return the roles of the user based on the given context
     */
    public static Collection<String> getRoles(MMObjectNode personnode, int educationno, Cloud cloud) {
        Set<String> roles = new HashSet<String>();
        List<MMObjectNode> directRoles = personnode.getRelatedNodes("roles");

        for (MMObjectNode role : directRoles) {
            roles.add(role.getStringValue("name"));
        }

        //        Retrieve all the relations between the user and the education or all education

        if (educationno != 0) {
            NodeList rolerel = nl.didactor.util.GetRelation.getRelations(
                    personnode.getNumber(),
                    educationno,
                    "rolerel",
                    cloud);

            if (rolerel.size()> 2) {
                log.error("There is more than 1 relation from user '" + personnode.getNumber() + "' to education '" + educationno + "'");
            }
            for (int rolerelno = 0; rolerelno < rolerel.size(); rolerelno++) {
                // Find all related roles to this relation
                Node rolrel = rolerel.getNode(rolerelno);
                NodeList educationRoles = rolrel.getRelatedNodes("roles");
                for (int i = 0; i < educationRoles.size(); i++) {
                    Node rolenode = educationRoles.getNode(i);
                    roles.add( rolenode.getStringValue("name"));
                }
            }
        }
        return roles;
    }

    /**
     * Return whether or not this user has the given role.
     * This means that the user can have the role in one (or more) of the
     * following ways:
     * <ul>
     *  <li>A 'role' object is related directly to him</li>
     *  <li>There is a 'rolerel' between him and the education from the context</li>
     * </ul>
     */
    public static boolean hasRole(MMObjectNode personnode, String req_rolename, int educationno, Cloud cloud) {
        return getRoles(personnode, educationno, cloud).contains(req_rolename);
    }

    public static List<Integer> getWorkgroupMembers(MMObjectNode usernode, int classno, int educationno, String rolename, Cloud cloud) {
        List<Integer> members= new ArrayList<Integer>();
        List<MMObjectNode> workgroups = usernode.getRelatedNodes("workgroups");
        for (MMObjectNode workgroup : workgroups) {
            // The right class is the class corresponding to the education
            int rightclass=0;

            List<MMObjectNode> classes= workgroup.getRelatedNodes("classes");
            // Well-Formedness: size == 1
            for (int k=0; k<classes.size(); k++) {
                MMObjectNode classdef= (MMObjectNode) classes.get(k);
                int classno1= classdef.getNumber();

                /* obtain education -> should be equal
                 Vector educations= classdef.getRelatedNodes("educations");
                 // Well-Formedness: size == 1
                  for (int l=0; l<educations.size(); l++) {
                  MMObjectNode education0= (MMObjectNode) educations.get(l);
                  if (education0.getNumber() != educationno) {
                  cloud not well-formed
                  }
                  }
                  */
                if (classno == classno1) {
                    List<MMObjectNode> people = workgroup.getRelatedNodes("people");
                    for (MMObjectNode person : people) {
                        //System.out.println( person.getStringValue("firstname")+ person.getStringValue("last name"));
                        if ((rolename == null)
                                || hasRole( person, rolename, educationno, cloud)) {
                            members.add(Integer.valueOf(person.getNumber()));
                        } else {
                        }
                        //person.removeRelations();
                        //person.getBuilder().removeNode(givenanswer);
                    }
                }
            }
        }
        return members;
    }

    public static List<Integer> getClassMembers(MMObjectNode usernode, int classno, int educationno, String rolename,
                                                Cloud cloud) {
//        /System.out.println( usernode.getNumber());

        List<Integer> members= new ArrayList<Integer>();
        List<MMObjectNode> classes= usernode.getRelatedNodes("classes");
        for (MMObjectNode classdef : classes) {
            int classno1= classdef.getNumber();

            /* obtain education -> should be equal
             Vector educations= classdef.getRelatedNodes("educations");
             // Well-Formedness: size == 1
              for (int l=0; l<educations.size(); l++) {
              MMObjectNode education0= (MMObjectNode) educations.get(l);
              if (education0.getNumber() != educationno) {
              cloud not well-formed
              }
              }
              */
            if ((classno == classno1) || (classno == ClassRoom.ALL_CLASSES)) {
                List<MMObjectNode> people = classdef.getRelatedNodes("people");
                for (MMObjectNode person : people) {
                    if ((rolename == null)
                            || hasRole( person, rolename, educationno, cloud)) {
                        members.add(Integer.valueOf(person.getNumber()));
                    } else {
                    }
                    //person.removeRelations();
                    //person.getBuilder().removeNode(givenanswer);
                }
            }
        }
        return members;
    }

//    Is the user the workgroupmember of the subject?
    public static boolean isWorkgroupMember(MMObjectNode usernode, int subjectpersonnode, int classno, int educationno, String rolename,
                                            Cloud cloud)  {
        MMObjectNode subjectnode = MMBase.getMMBase().getBuilder("people").getNode( subjectpersonnode);
        if (subjectnode == null) {
            throw new RuntimeException( "Person with number '" + subjectpersonnode+ "' not found");
        }
        return getWorkgroupMembers(subjectnode, classno, educationno, rolename, cloud).contains(usernode.getNumber());
    }

//    Is the user a classmember of the subject?
    public static boolean isClassMember(MMObjectNode usernode, int subjectpersonnode, int classno, int educationno, String rolename,
                                        Cloud cloud) {
        MMObjectNode subjectnode = MMBase.getMMBase().getBuilder("people").getNode( subjectpersonnode);
        if (subjectnode == null) {
            throw new RuntimeException( "Person with number '" + subjectpersonnode+ "' not found");
        }
        return getClassMembers(subjectnode, classno, educationno, rolename, cloud).contains(usernode.getNumber());
    }

}
