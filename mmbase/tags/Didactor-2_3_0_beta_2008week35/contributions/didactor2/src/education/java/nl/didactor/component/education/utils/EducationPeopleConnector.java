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
 * @version $Id: EducationPeopleConnector.java,v 1.7 2007-06-08 14:03:33 michiel Exp $
 */
public class EducationPeopleConnector {
    private Cloud cloud;

    private Node node;

    public void setNode(Node n) {
        node = n;
        cloud = n.getCloud();
    }


    public Set<Node> relatedPersons() {
        Node nodeEducation = node;
        Set<Node> hsetResult = new HashSet<Node>();
        NodeList nodelistPeople = nodeEducation.getRelatedNodes("people", "classrel", "destination");
        for(NodeIterator it = nodelistPeople.nodeIterator(); it.hasNext(); ) {
            hsetResult.add(it.nextNode());
        }

        NodeList nodelistClasses = nodeEducation.getRelatedNodes("classes", "classrel", "destination");
        for(NodeIterator it = nodelistClasses.nodeIterator(); it.hasNext();) {
            Node nodeClass = it.nextNode();
            nodelistPeople = nodeClass.getRelatedNodes("people", "classrel", "source");
            for(NodeIterator it2 = nodelistPeople.nodeIterator(); it2.hasNext();) {
                hsetResult.add(it2.nextNode());
            }
        }
        return hsetResult;
    }

    public Set<Node> relatedEducations() {
        Node nodePerson = node;
        Set<Node> hsetResult = new HashSet<Node>();

        NodeList nodelistEducations = nodePerson.getRelatedNodes("educations", "classrel", "source");
        for(NodeIterator it = nodelistEducations.nodeIterator(); it.hasNext(); ) {
            hsetResult.add(it.nextNode());
        }

        NodeList nodelistClasses = nodePerson.getRelatedNodes("classes", "classrel", "destination");
        for(NodeIterator it = nodelistClasses.nodeIterator(); it.hasNext(); ) {
            Node nodeClass = it.nextNode();
            NodeList nodelistPeople = nodeClass.getRelatedNodes("educations", "classrel", "source");
            for(NodeIterator it2 = nodelistPeople.nodeIterator(); it2.hasNext();) {
                hsetResult.add(it2.nextNode());
            }
        }
        return hsetResult;
    }


}
