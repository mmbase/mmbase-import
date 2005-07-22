package nl.didactor.util;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;
import java.util.Vector;
import java.lang.Integer;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

public class ClassRoom {
    public final static int ALL_CLASSES= -1;
    /**
     * Return the roles of the user based on the given context
     */
    public static Collection getRoles( MMObjectNode personnode, int educationno, Cloud cloud)
    {
        HashSet roles = new HashSet();
        Vector directRoles = personnode.getRelatedNodes("roles");

        for (int i=0; i<directRoles.size(); i++) {
            MMObjectNode role = (MMObjectNode)directRoles.get(i);
            roles.add( role.getStringValue("name"));
        }

//      Retrieve all the relations between the user and the education or all education
          
        if (educationno != 0) {
            NodeList rolerel = nl.didactor.util.GetRelation.getRelations(
                personnode.getNumber(), 
                educationno,
                "rolerel", 
                cloud);
       
            if (rolerel.size()> 2) {
                System.err.println("There is more than 1 relation from user '" + personnode.getNumber() + "' to education '" + educationno + "'");
            }
            for (int rolerelno=0; rolerelno< rolerel.size() ;rolerelno++) {
                // Find all related roles to this relation
                Node rolrel = rolerel.getNode( rolerelno);
                NodeList educationRoles = rolrel.getRelatedNodes("roles");
                for (int i=0; i<educationRoles.size(); i++) {
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
public static boolean hasRole( MMObjectNode personnode, String req_rolename, int educationno, Cloud cloud)
{
    return getRoles( personnode, educationno, cloud).contains( req_rolename);
}

public static List getWorkgroupMembers( MMObjectNode usernode, int classno, int educationno, String rolename,
                                        Cloud cloud)
throws JspTagException
{             
    ArrayList members= new ArrayList();
    Vector workgroups= usernode.getRelatedNodes("workgroups");
    for (int j=0; j<workgroups.size(); j++) {
        MMObjectNode workgroup= (MMObjectNode)workgroups.get(j);
       // The right class is the class corresponding to the education
       int rightclass=0;

        Vector classes= workgroup.getRelatedNodes("classes");
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
                Vector people= workgroup.getRelatedNodes("people");
                for (int m=0; m<people.size(); m++) {
                    MMObjectNode person= (MMObjectNode) people.get( m);
                    //System.out.println( person.getStringValue("firstname")+ person.getStringValue("last name"));
                    if ((rolename == null)
                        || hasRole( person, rolename, educationno, cloud)) {
                        members.add( new Integer( person.getNumber()));
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

public static List getClassMembers( MMObjectNode usernode, int classno, int educationno, String rolename,
                                    Cloud cloud)
throws JspTagException
{
//    /System.out.println( usernode.getNumber());
             
    ArrayList members= new ArrayList();
        Vector classes= usernode.getRelatedNodes("classes");
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
            if ((classno == classno1) || (classno== ClassRoom.ALL_CLASSES)) {
                Vector people= classdef.getRelatedNodes("people");
                for (int m=0; m<people.size(); m++) {
                    MMObjectNode person= (MMObjectNode) people.get( m);
                    //System.out.println( person.getStringValue("firstname")+ person.getStringValue("last name"));
                    if ((rolename == null)
                        || hasRole( person, rolename, educationno, cloud)) {
                        members.add( new Integer( person.getNumber()));
                    } else {
                    }
                    //person.removeRelations();
                    //person.getBuilder().removeNode(givenanswer);
                }
            }
        }
    return members;
}

// Is the user the workgroupmember of the subject?
public static boolean isWorkgroupMember( MMObjectNode usernode, int subjectpersonnode, int classno, int educationno, String rolename,
                                        Cloud cloud)
throws JspTagException
{
    MMObjectNode subjectnode = MMBase.getMMBase().getBuilder("people").getNode( subjectpersonnode);
    if (subjectnode == null) {
        throw new JspTagException( "Person with number '" + subjectpersonnode+ "' not found");
    }
    List members= getWorkgroupMembers( subjectnode, classno, educationno, rolename, cloud);
    return members.contains( new Integer( usernode.getNumber()));
}

// Is the user a classmember of the subject?
public static boolean isClassMember( MMObjectNode usernode, int subjectpersonnode, int classno, int educationno, String rolename,
                             Cloud cloud)
throws JspTagException
{
    MMObjectNode subjectnode = MMBase.getMMBase().getBuilder("people").getNode( subjectpersonnode);
        if (subjectnode == null) {
            throw new JspTagException( "Person with number '" + subjectpersonnode+ "' not found");
        }
        List members= getClassMembers( subjectnode, classno, educationno, rolename, cloud);
        return members.contains( new Integer( usernode.getNumber()));
}


}
