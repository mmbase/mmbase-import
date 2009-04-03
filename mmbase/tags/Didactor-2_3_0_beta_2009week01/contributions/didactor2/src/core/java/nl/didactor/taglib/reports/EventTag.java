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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;

import nl.didactor.events.*;
import org.mmbase.util.Casting;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;


/**
 * @javadoc
 * @version $Id$
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

                HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

                Integer provider = Casting.toInt(request.getAttribute("provider"));
                Integer education = Casting.toInt((educationId != null) ? educationId : request.getAttribute("education"));
                Integer classNumber = Casting.toInt(request.getAttribute("class"));


                Event event = new Event(username, request, provider, education, classNumber, eventtype, eventvalue, note);
                org.mmbase.core.event.EventManager.getInstance().propagateEvent(event);
            }
        } catch (Exception ex) {
            throw new JspTagException(ex.getMessage(), ex);
        }
        return SKIP_BODY;
    }

}
