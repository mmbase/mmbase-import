package org.mmbase.util.functions;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: ExampleBean.java,v 1.2 2005-01-30 16:46:36 nico Exp $
 * @since MMBase-1.8
 */
public final class ExampleBean {

    private String parameter1;
    private Integer parameter2;    
    private String parameter3 = "default";

    public void setParameter1(String hoi) {
        parameter1 = hoi;
    }

    public void setParameter2(Integer j) {
        parameter2 = j;
    }
    public void setAnotherParameter(String a) {
        parameter3 = a;
    }
    public String getAnotherParameter() {
        return parameter3;
    }


    public String stringFunction() {
        return "[[" + parameter1 + "/" + parameter3 + "]]";
    }

    public Integer integerFunction() {
        return new Integer(parameter2.intValue() * 3);
    }


}
