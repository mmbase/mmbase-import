/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.crontab;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: MockTest.java 41950 2010-04-19 11:16:24Z michiel $
 */
public class RunningCronJobTest  {

    @Test
    public void equals() throws Exception {
        Date date1 = new Date();
        Date date2 = new Date(0);
        CronEntry ce1 = new CronEntry("x", "* * * * *", "test", TestCronJob.class.getName(), null);
        CronEntry ce2 = new CronEntry("x", "* * * * *", "test", TestCronJob.class.getName(), null);
        RunningCronEntry re1 = new RunningCronEntry(ce1, date1, "localhost", 1);
        RunningCronEntry re2 = new RunningCronEntry(ce2, date2, "localhost", 1);
        assertEquals(re1, re2);
        assertEquals(re1.hashCode(), re2.hashCode());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(os);
        out.writeObject(re1);

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(os.toByteArray()));
        RunningCronEntry re3 = (RunningCronEntry) in.readObject();

        assertEquals(re3, re2);
        assertEquals(re3.hashCode(), re2.hashCode());


    }

}
