/*
 * @(#)TicDateFormat.java Jun 28, 2005
 *
 * Copyright 2005 Ex Machina. All rights reserved.
 * Ex Machina Proprietary/Confidential. Use is subject to license terms.
 */
package stats;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import jcckit.util.TicLabelFormat;

/**
 * @author Kees Jongenburger <kees@exmachina.nl>
 * @version $Id: TicDateFormat.java,v 1.1 2005-06-29 13:09:44 keesj Exp $
 */
public class TicDateFormat implements TicLabelFormat {

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
    /* (non-Javadoc)
     * @see jcckit.util.TicLabelFormat#form(double)
     */
    public String form(double arg0) {
        
        
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis((long)arg0);
        return dateFormat.format(c.getTime());
    }
}
