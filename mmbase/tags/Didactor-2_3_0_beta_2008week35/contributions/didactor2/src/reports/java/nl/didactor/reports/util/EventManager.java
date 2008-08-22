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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.Session;

import nl.didactor.reports.data.EventLog;
import nl.didactor.reports.data.EventType;

import nl.didactor.events.*;
import org.mmbase.util.logging.*;

/**
 * @author p.becic
 * @javadoc
 */
public class EventManager implements DidactorEventListener {
    private static final Logger log = Logging.getLoggerInstance(nl.didactor.reports.util.EventManager.class);

    public void notify(Event event) {
        
        try {
            if ( event == null ) return;
            String eType  = event.getEventType();
            String eValue = event.getEventValue();
    
            Long eventvalue;
            try {
                eventvalue = Long.decode(eValue);
            } catch (Exception e) {
                eventvalue = new Long(0);
            }

            // here we are expected eventtype to be a number
            Integer eventtype = null;
            try {
                eventtype = new Integer(EventType.getEvent(eType));
            } catch (Exception e1) {
                return;
            }
            
            if (eventtype.intValue() == EventType.LOGIN) {
                // Create SessionListener class and add ti to the session
                DidactorSessionListener sessionListener = new DidactorSessionListener(event.getUsername());
                HttpServletRequest request = event.getRequest();
                HttpSession session = request.getSession();
                session.setAttribute("session_listener", sessionListener);
                session.setAttribute(event.getUsername() + "-login-time", new Long(System.currentTimeMillis()));
            }
            if (eventtype.intValue() == EventType.LOGOUT) {
                HttpServletRequest request = event.getRequest();
                HttpSession session = request.getSession();
                Object loginTimeObj = session.getAttribute(event.getUsername() + "-login-time" );
                if (loginTimeObj != null) {
                    long loginTime = ((Long)loginTimeObj).longValue();
                    long duration = System.currentTimeMillis() - loginTime;
                    eventvalue = new Long(duration);
                    session.removeAttribute(event.getUsername() + "-login-time");
                    session.removeAttribute("session_listener");
                }
    
                String educationId = request.getParameter("education") + "-" + event.getUsername() + "-" + session.getId();
                Object startReadingEducation = session.getAttribute(educationId);
                if(startReadingEducation != null) {
                    long startReading = ((Long)startReadingEducation).longValue();
                    long duration2 = System.currentTimeMillis() - startReading;
                    String edId = educationId.substring(0, educationId.indexOf("-"));
                    Integer educationIdInt = null;
                    try {
                        educationIdInt = Integer.decode(edId);
                    } catch (NumberFormatException e) {}
                    Event event2 = new Event(event.getUsername(), event.getRequest(), event.getProvider(), educationIdInt, event.getClassId(), "reading_education", "" + duration2, "read education");
                    notify(event2);
                }
                session.removeAttribute("educationId");
                session.removeAttribute(educationId);
            }
    
            createAndStoreEvent(event.getUsername(), event.getRequest().getSession().getId(), event.getProvider(), event.getEducation(), event.getClassId(), eventtype, eventvalue, event.getNote());
        } catch (Exception exc) {
            if ( event != null )
                log.error("Can not write event "+event.getNote()+". \r\n"+exc.toString());
            else
                log.error("Can not write null event.");
        }
    }
    
    /**
     * Creates EvenlLog object and store it into database
     * 
     * @param username
     * @param sessionId
     * @param provider
     * @param education
     * @param classNumber
     * @param eventtype
     * @param eventvalue
     * @param note
     */
    public static void createAndStoreEvent(String username, String sessionId,
            Integer provider, Integer education, Integer classNumber,
            Integer eventtype, Long eventvalue, String note) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        // create EventLog object
        EventLog eventLog = new EventLog();
        eventLog.setTimestamp(new Timestamp(System.currentTimeMillis()));
        eventLog.setUsername(username);
        eventLog.setSession(sessionId);
        eventLog.setProvider(provider);
        eventLog.setEducation(education);
        eventLog.setClassNumber(classNumber);
        eventLog.setEventtype(eventtype);
        eventLog.setValue(eventvalue);
        eventLog.setNote(note);
        session.save(eventLog);

        session.getTransaction().commit();

        HibernateUtil.getSessionFactory().close();
    }

    /**
     * Returns HashMap where key is 'username' and value is average time per
     * visit that the student has been logged on Didactor
     * 
     * @param classNumber
     * @return HashMap
     */
    public static HashMap getAverageTimePerLogin() {
        HashMap map = new HashMap();
        map.put("reports_page", ReportsPages.LOGIN_REPORTS);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        String sql_query = "select event.username,round(avg(event.value))"
                + "from EventLog event where event.eventtype=2 group by event.username";
        Query query = session.createQuery(sql_query);
        for (Iterator it = query.iterate(); it.hasNext();) {
            Object[] row = (Object[]) it.next();
            // row[0] = username
            // row[1] = average time per visit that the student has been logged
            // on Didactor
            map.put(row[0], row[1]);
        }
        session.getTransaction().commit();

        return map;
    }

    /**
     * Returns HashMap where key is 'username.education' and value is average
     * time per visit that the student has been logged on the specified
     * education
     * 
     * @param classNumber
     * @return HashMap
     */
    public static HashMap getAverageTimePerEducation(int classNumber) {
        HashMap map = new HashMap();
        map.put("reports_page", ReportsPages.EDUCATION_REPORTS);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        String sql_query = "select event.username,event.education,round(avg(event.value))"
                + "from EventLog event where event.eventtype=3 group by event.username,event.education";
        Query query = session.createQuery(sql_query);
        for (Iterator it = query.iterate(); it.hasNext();) {
            Object[] row = (Object[]) it.next();
            // row[0] = username
            // row[1] = education number
            // row[2] = average time per visit that the student has been logged
            // on the specified education
            map.put(row[0] + "." + row[1], row[2]);
        }
        session.getTransaction().commit();

        return map;
    }

    /**
     * Returns HashMap where key is 'username.learnobject' and value is , a
     * number of visiting of a specific page (learning object).
     * 
     * @param classNumber
     * @return HashMap
     */
    public static HashMap getLearnobjectStatistic(int classNumber) {
        HashMap map = new HashMap();
        map.put("reports_page", ReportsPages.LEARNOBJECT_REPORTS);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        String sql_query = "select event.username,event.value,count(*)"
                + "from EventLog event where event.classNumber=" + classNumber
                + " and event.eventtype=4 group by event.username,event.value";
        Query query = session.createQuery(sql_query);
        for (Iterator it = query.iterate(); it.hasNext();) {
            Object[] row = (Object[]) it.next();
            // row[0] = username
            // row[1] = learnobject number
            // row[2] = number of visiting of a specific page (learning object).
            map.put(row[0] + "." + row[1], row[2]);
        }
        session.getTransaction().commit();

        return map;
    }

    /**
     * Returns HashMap where key is 'username' and value is , a number of added
     * documents in a specified timeframe.
     * 
     * @param classNumber
     * @return HashMap
     */
    public static HashMap getDocumentStatistic(long startTime, long endTime) {
        HashMap map = new HashMap();
        map.put("reports_page", ReportsPages.DOCUMENT_REPORTS);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        String sql_query = "select event.username,event.value "
                + "from EventLog event where event.timestamp>'"
                + new Timestamp(startTime) + "' and event.timestamp<'"
                + new Timestamp(endTime)
                + "' and event.eventtype=5 order by event.username";
        Query query = session.createQuery(sql_query);
        for (Iterator it = query.iterate(); it.hasNext();) {
            Object[] row = (Object[]) it.next();

            // row[0] = username
            // row[1] = 'id' of added document
            if( map.containsKey( row[0] ) )
            {
                ArrayList list = ( ArrayList )(map.get( row[0] ));
                list.add( row[1] );
                map.put( row[0], list );
            }
            else
            {
                ArrayList list = new ArrayList();
                list.add( row[1] );
                map.put( row[0], list );
            }
        }
        session.getTransaction().commit();

        return map;
    }

}
