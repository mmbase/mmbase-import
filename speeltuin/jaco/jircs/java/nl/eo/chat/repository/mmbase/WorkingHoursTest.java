/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.mmbase;

import java.util.Calendar;
import java.util.Date;

/**
 * Use this class to test the working hours class.
 *
 * @author Jaco de Groot
 */
class WorkingHoursTest {
    private static final int oneMinute = 60 * 1000;
    private static final int oneHour = 60 * oneMinute;

    public static void main(String[] a) {
        test("bla", 1, 1, -1, -1);
        test("10:00-14:00", 9, 30, 30 * oneMinute, 0);
        test("10:00-14:00", 13, 50, 0, 10 * oneMinute);
        test("10:00-14:00", 14, 30, 19 * oneHour + 30 * oneMinute, 0);
        test("22:00-02:00", 21, 30, 30 * oneMinute, 0);
        test("22:00-02:00", 23, 30, 0, 30 * oneMinute + 2 * oneHour);
        test("22:00-02:00", 1, 30, 0, 30 * oneMinute);
        test("22:00-02:00", 3, 0, 19 * oneHour, 0);
    }

    private static void test(String s, int hour, int minute, long correctOutputOpen, long correctOutputClose) {
        WorkingHours workingHours = new WorkingHours(s);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long output;
        output = workingHours.open(calendar.getTime());
        if (correctOutputOpen == output) {
            System.out.println(s + " " + hour + ":" + minute + " open OK");
        } else {
            System.out.println(s + " " + hour + ":" + minute + " open WRONG (" + correctOutputOpen + "!=" + output + ")");
        }
        output = workingHours.close(calendar.getTime());
        if (correctOutputClose == output) {
            System.out.println(s + " " + hour + ":" + minute + " close OK");
        } else {
            System.out.println(s + " " + hour + ":" + minute + " close WRONG (" + correctOutputClose + "!=" + output + ")");
        }
    }

}
