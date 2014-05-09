/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.Processor;
import java.util.regex.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Recognizes and canonicalizes phone numbers. Currently only supports dutch mobile phone
 * numbers. This processor can be used as a set-processor on mobile number fields.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 **/
public  class NumberRecognizer implements Processor {


    public static final Pattern DUTCH = Pattern.compile("(?:00316|06|\\+316)\\s*[\\-\\s]?\\s*([0-9]{8})");

    public Object process(Node node, Field field, Object value) {
        String number = "" + value;
        Matcher dutch = DUTCH.matcher(number);
        if (dutch.matches()) {
            return "00316" + dutch.group(1);
        } else {
            return value;
        }
    }

    public static void main(String[] argv) {
        System.out.println(new NumberRecognizer().process(null, null, argv[0]));
    }


}
