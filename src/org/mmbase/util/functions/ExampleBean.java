package org.mmbase.util.functions;

import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: ExampleBean.java,v 1.3 2005-06-09 21:28:33 michiel Exp $
 * @since MMBase-1.8
 */
public final class ExampleBean {

    private String parameter1;
    private Integer parameter2 = new Integer(0);
    private String parameter3 = "default";
    private MMObjectNode node;

    public void setParameter1(String hoi) {
        parameter1 = hoi;
    }

    public void setParameter2(Integer j) {
        parameter2 = j;
    }
    public Integer getParameter2() {
        return parameter2;
    }
    public void setAnotherParameter(String a) {
        parameter3 = a;
    }
    public String getAnotherParameter() {
        return parameter3;
    }

    /**
     * Makes this bean useable as a Node function.
     */
    public void setNode(MMObjectNode node) {
        this.node = node;
    }


    /**
     * A function defined by this class
     */
    public String stringFunction() {
        return "[[" + parameter1 + "/" + parameter3 + "]]";
    }

    public Integer integerFunction() {
        return new Integer(parameter2.intValue() * 3);
    }

    /**
     * A real node-function (using the node argument). Returns the next newer node of same type.
     */
    public MMObjectNode successor() {
        if (node == null) throw new IllegalArgumentException("successor is a node-function");
        int number = node.getNumber();
        NodeSearchQuery query = new NodeSearchQuery(node.getBuilder());
        StepField field = query.getField(node.getBuilder().getField("number"));
        BasicFieldValueConstraint cons = new BasicFieldValueConstraint(field, new Integer(node.getNumber()));
        cons.setOperator(FieldCompareConstraint.GREATER);
        query.setConstraint(cons);
        query.addSortOrder(field);
        query.setMaxNumber(1);
        try {
            java.util.Iterator resultList = node.getBuilder().getNodes(query).iterator();
            if (resultList.hasNext()) {
                return ((MMObjectNode) resultList.next());
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }


}
