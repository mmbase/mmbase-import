package nl.didactor.functions;

import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.w3c.dom.*;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Determines the questions for a certains test.
 * @author Michiel Meeuwissen
 * @version $Id: RichQuestions.java,v 1.2 2008-11-24 13:37:54 michiel Exp $
 */
public class RichQuestions {
    protected final static Logger log = Logging.getLoggerInstance(RichQuestions.class);

    private Node node;

    public void setNode(Node n) {
        node = n;
    }


    /**
     * Simple utulity to iterator a complete document
     */

    protected Iterable<org.w3c.dom.Node> iterable(final Document doc) {
        return new Iterable<org.w3c.dom.Node>() {
            final Element el = doc.getDocumentElement();

            public Iterator<org.w3c.dom.Node> iterator() {
                return new Iterator<org.w3c.dom.Node>() {
                    public org.w3c.dom.Node next = el.getFirstChild();

                    public boolean hasNext() {
                        return next != null;
                    }
                    public org.w3c.dom.Node next() {
                        org.w3c.dom.Node result = next;
                        org.w3c.dom.Node c = next.getFirstChild();
                        if (c == null) {
                            c = next.getNextSibling();
                        }
                        if (c == null) {
                            c = next.getNextSibling();
                            while (c == null) {
                                next = next.getParentNode();
                                if (next == el) { c = null; break; }
                                c = next.getNextSibling();
                            }
                        }
                        next = c;
                        return result;
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }


    protected Node getDestination(RelationList rels, String id) {
        for (Relation r : rels) {
            if (r.getStringValue("id").equals(id)) {
                return r.getDestination();
            }
        }
        return null;
    }

    protected  List<Node> questions(Node n) {
        List<Node> results = new ArrayList<Node>();
        Cloud cloud = n.getCloud();
        NodeManager questions = cloud.getNodeManager("questions");
        NodeManager blocks = cloud.getNodeManager("blocks");
        RelationList rels = n.getRelations("idrel", cloud.getNodeManager("object"), "destination");
        for (org.w3c.dom.Node el : iterable(n.getXMLValue("body"))) {
            if (el instanceof Element) {
                String id = ((Element) el).getAttribute("id");
                if (id != null && ! "".equals(id)) {
                    Node dest = getDestination(rels, id);
                    NodeManager nm = dest.getNodeManager();
                    if (log.isDebugEnabled()) {
                        log.debug(nm.getName() + " " + dest.getNumber());
                    }
                    if (dest != null) {
                        if (questions.getDescendants().contains(nm)) {
                            results.add(dest);
                        } else if (blocks.equals(nm)) {
                            results.addAll(questions(dest));
                        }
                    }
                }
            }
        }
        return results;
    }


    public List<Node> questions() {
        return questions(node);
    }


}
