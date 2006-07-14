/*
 * Copyright (c) 2006 Levi9 Global Sourcing. All Rights Reserved.
 * This software is the confidential and proprietary information of
 * Levi9 Global Sourcing. ("Confidential Information"). You shall
 * not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you
 * entered into with Levi9 Global Sourcing.
 * Levi9 Global Sourcing makes no representations or warranties about the
 * suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability,
 * fitness for a particular purpose, or non-infringement. Levi9 Global Sourcing
 * shall not be liable for any damages suffered by licensee as a
 * result of using, modifying or distributing this software or its
 * derivatives.
 */

package nl.didactor.reports.util;


/**
 * @author p.becic
 */
public class TimeUtil {

    /**
     * Converts time specified in seconds to string in HH:MM:SS format.
     */
    public static String secondsToHHMMSS(long sec) {
        if (sec < 0)
            sec = 0;

        StringBuffer buf = new StringBuffer();

        long h = sec / 3600;
        sec %= 3600;
        if (h > 0) {
            if (h < 10)
                buf.append('0');
            buf.append(new Long(h).toString());
            buf.append(':');
        }
        else {
            buf.append("00:");
        }

        long m = sec / 60;
        sec %= 60;
        if (m > 0 || buf.length() > 0) {
            if (m < 10)
                buf.append('0');
            buf.append(new Long(m).toString());
            buf.append(':');
        }
        else {
            buf.append("00:");
        }

        if (sec < 10)
            buf.append('0');
        buf.append(new Long(sec).toString());

        return buf.toString();
    }

    /**
     * Converts time specified in miliseconds to string in HH:MM:SS format.
     */
    public static String milisecondsToHHMMSS(long milisec) {
        return secondsToHHMMSS(milisec / 1000);
    }

}
