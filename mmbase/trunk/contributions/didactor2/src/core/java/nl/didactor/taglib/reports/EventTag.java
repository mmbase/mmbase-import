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

package nl.didactor.taglib.reports;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;

import nl.didactor.reports.util.EventManager;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;


/**
 * @author p.becic
 */
public class EventTag extends CloudReferrerTag {
    private String eventtype;

    private String educationId;

    private String eventvalue;

    private String note;

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public void setEducationId(String educationId) {
        this.educationId = educationId;
    }

    public void setEventvalue(String eventvalue) {
        this.eventvalue = eventvalue;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int doStartTag() throws JspTagException {
        try {
            // get cloud object
            Cloud cloud = getCloudVar();
            if (cloud != null) {
                // get username from cloud
                String username = cloud.getUser().getIdentifier();

                HttpServletRequest request = (HttpServletRequest) pageContext
                        .getRequest();
                HttpSession session = request.getSession(false);
                // get sessionId
                String sessionId = session.getId();

                // get provider from request
                String sProvider = request.getParameter("provider");

                String sEducation = (educationId != null) ? educationId
                        : request.getParameter("education");

                // get class from request
                String sClass = request.getParameter("class");

                Integer provider = null;
                Integer education = null;
                Integer classNumber = null;
                if (sProvider != null) {
                    try {
                        provider = Integer.decode(sProvider);
                    }
                    catch (NumberFormatException nfe) {
                        return SKIP_BODY;
                    }
                }

                if (sEducation != null) {
                    try {
                        education = Integer.decode(sEducation);
                    }
                    catch (NumberFormatException nfe) {
                        return SKIP_BODY;
                    }
                }

                if (sClass != null) {
                    try {
                        classNumber = Integer.decode(sClass);
                    }
                    catch (NumberFormatException nfe) {
                        return SKIP_BODY;
                    }
                }

                Integer etype = null;
                Long evalue = null;
                try {
                    etype = Integer.decode(eventtype);
                }
                catch (NumberFormatException nfe) {
                    return SKIP_BODY;
                }

                if (eventvalue != null) {
                    try {
                        evalue = Long.decode(eventvalue);
                    }
                    catch (NumberFormatException nfe) {
                    }
                }

                // create and store Event
                EventManager.createAndStoreEvent(username, sessionId, provider,
                        education, classNumber, etype, evalue, note);
            }
        }
        catch (Exception ex) {
            JspTagException e = new JspTagException(ex.getMessage());
            e.initCause(ex);
            throw e;
        }
        return SKIP_BODY;
    }

}
