package org.mmbase.util.functions;

import java.util.*;

import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: ExampleBean.java,v 1.1 2004-11-24 13:23:03 pierre Exp $
 * @since MMBase-1.8
 */
public final class ExampleBean {
    private static final Logger log = Logging.getLoggerInstance(ExampleBean.class);


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
