/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.mmbase;

import java.util.Calendar;
import java.util.Date;

public class WorkingHours {
    private static final long oneDay = 24 * 60 * 60 * 1000;
    private String workingHours;
    private long currentTime;
    private long startTime;
    private long stopTime;
    
    public WorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
    
    /**
     * Returns the nummber of milliseconds before it's time to open.
     *
     * @return  The nummber of milliseconds before it's time to open, 0 if it's
     *          already time to be open or -1 if there's no open time known.
     */
    public long open(Date currentDate) {
        initTimes(currentDate);
        if (currentTime == -1) {
            return -1;
        } else {
            if (startTime == stopTime) {
                return 0;
            } else if (currentTime < startTime) {
                return startTime - currentTime;
            } else if (currentTime < stopTime) {
                return 0;
            } else {
                return startTime + oneDay - currentTime;
            }
        }
    }
    
    /**
     * Returns the nummber of milliseconds before it's time to close.
     *
     * @return  The nummber of milliseconds before it's time to close, 0 if it's
     *          already time to be closed or -1 if there's no closing time
     *          known.
     */
    public long close(Date currentDate) {
        initTimes(currentDate);
        if (currentTime == -1) {
            return -1;
        } else {
            if (startTime == stopTime) {
                return -1;
            } else if (currentTime < startTime) {
                return 0;
            } else if (currentTime < stopTime) {
                return stopTime - currentTime;
            } else {
                return 0;
            }
        }
    }

    private void initTimes(Date currentDate) {
        int startHours;
        int startMinutes;
        int stopHours;
        int stopMinutes;
        try {
            int i = 0;
            int j = workingHours.indexOf(':');
            startHours = Integer.parseInt(workingHours.substring(i, j));
            i = j + 1;
            j = workingHours.indexOf('-', i);
            startMinutes = Integer.parseInt(workingHours.substring(i, j));
            i = j + 1;
            j = workingHours.indexOf(':', i);
            stopHours = Integer.parseInt(workingHours.substring(i, j));
            i = j + 1;
            j = workingHours.length();
            stopMinutes = Integer.parseInt(workingHours.substring(i, j));
        } catch(Exception e) {
            currentTime = -1;
            return;
        }
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(currentDate);
        calendarStart.set(Calendar.HOUR_OF_DAY, startHours);
        calendarStart.set(Calendar.MINUTE, startMinutes);
        calendarStart.set(Calendar.SECOND, 0);
        calendarStart.set(Calendar.MILLISECOND, 0);
        calendarStart.getTime();
        Calendar calendarStop = Calendar.getInstance();
        calendarStop.setTime(currentDate);
        calendarStop.set(Calendar.HOUR_OF_DAY, stopHours);
        calendarStop.set(Calendar.MINUTE, stopMinutes);
        calendarStop.set(Calendar.SECOND, 0);
        calendarStop.set(Calendar.MILLISECOND, 0);
        calendarStop.getTime();
        Calendar current = Calendar.getInstance();
        current.setTime(currentDate);
        currentTime = current.getTime().getTime();
        startTime = calendarStart.getTime().getTime();
        stopTime = calendarStop.getTime().getTime();

        if (stopTime < startTime) {
            if (currentTime < stopTime) {
                startTime = startTime - oneDay;
            } else {
                stopTime = stopTime + oneDay;
            }
        }
    }

}
