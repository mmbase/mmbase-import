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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import nl.didactor.reports.data.EventType;


/**
 * @author p.becic
 * @javadoc It is driving me mad. It is not at all obivous wat is happening here.
 */
public class DidactorSessionListener implements HttpSessionBindingListener {
    private final String username;
    private final long loginTime;

    public DidactorSessionListener(String username) {
        this.username = username;
        this.loginTime = System.currentTimeMillis();
    }

    /**
     * called every time this object is added to a session
     */
    public void valueBound(HttpSessionBindingEvent event) {
        // add this object to the application context
        List activeUsersList = (List) event.getSession()
                .getServletContext().getAttribute("active_users");
        if (activeUsersList == null) {
            activeUsersList = new ArrayList();
        }
        activeUsersList.add(this);
        event.getSession().getServletContext().setAttribute("active_users",
                activeUsersList);
    }

    /**
     * called every time this object is removed from a session
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        HttpSession session = event.getSession();

        long duration = System.currentTimeMillis() - loginTime;

        // add event 'logout' to database
        EventManager.createAndStoreEvent(username, session.getId(), null, null,
                null, new Integer(EventType.LOGOUT), new Long(duration),
                "logout");

        // remove this object from the application context
        List activeUsersList = (ArrayList) event.getSession()
                .getServletContext().getAttribute("active_users");
        if (activeUsersList != null) {
            activeUsersList.remove(this);
            event.getSession().getServletContext().setAttribute("active_users",
                    activeUsersList);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss z");
        return username + " - " + simpleDateFormat.format(new Date(loginTime));
    }

}