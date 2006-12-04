package nl.didactor.component.education.utils;

import java.util.HashSet;
import java.util.Set;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;


/**
 * @todo what it the point of this class, besides making sure that you need to restart the server if
 * somethings wrong here, and making things less clear in JSP's? (because you need to guess what
 * happens in this class, which btw also lacks javadoc).
 *
 * @version $Id: EducationPeopleConnector.java,v 1.4 2006-12-04 16:04:01 mmeeuwissen Exp $
 */
public class EducationPeopleConnector {
    final Cloud cloud;

    public EducationPeopleConnector(Cloud cloud) {
        this.cloud = cloud;
    }

    public Set relatedPersons(String educationNumber) {
        Set hsetResult = new HashSet();

        Node nodeEducation = cloud.getNode((new Integer(educationNumber)).intValue());

        NodeList nodelistPeople = nodeEducation.getRelatedNodes("people", "classrel", "destination");
        for(NodeIterator it = nodelistPeople.nodeIterator(); it.hasNext(); ) {
            hsetResult.add("" + it.nextNode().getNumber());
        }

        NodeList nodelistClasses = nodeEducation.getRelatedNodes("classes", "classrel", "destination");
        for(NodeIterator it = nodelistClasses.nodeIterator(); it.hasNext();) {
            Node nodeClass = it.nextNode();
            nodelistPeople = nodeClass.getRelatedNodes("people", "classrel", "source");
            for(NodeIterator it2 = nodelistPeople.nodeIterator(); it2.hasNext();) {
                hsetResult.add("" + it2.nextNode().getNumber());
            }
        }
        return hsetResult;
    }

    public Set relatedEducations(String personNumber) {

        Set hsetResult = new HashSet();
        Node nodePerson = cloud.getNode((new Integer(personNumber)).intValue());

        NodeList nodelistEducations = nodePerson.getRelatedNodes("educations", "classrel", "source");
        for(NodeIterator it = nodelistEducations.nodeIterator(); it.hasNext(); ) {
            hsetResult.add("" + it.nextNode().getNumber());
        }

        NodeList nodelistClasses = nodePerson.getRelatedNodes("classes", "classrel", "destination");
        for(NodeIterator it = nodelistClasses.nodeIterator(); it.hasNext(); ) {
            Node nodeClass = it.nextNode();
            NodeList nodelistPeople = nodeClass.getRelatedNodes("educations", "classrel", "source");
            for(NodeIterator it2 = nodelistPeople.iterator(); it2.hasNext();) {
                hsetResult.add("" + it2.nextNode().getNumber());
            }
        }
        return hsetResult;
    }

}
