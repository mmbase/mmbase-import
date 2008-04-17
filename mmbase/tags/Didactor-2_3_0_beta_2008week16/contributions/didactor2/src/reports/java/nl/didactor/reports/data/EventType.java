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

package nl.didactor.reports.data;

/**
 * @javadoc
 * @author p.becic
 * @version $Id: EventType.java,v 1.3 2007-07-26 14:52:27 michiel Exp $
 */
public class EventType {

    public static final int ACCOUNT_CREATED = 0;
    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;
    public static final int READING_EDUCATION = 3;
    public static final int VISIT_PAGE = 4;
    public static final int ADD_DOCUMENT = 5;
    public static final int UNKNOWN = -1;

    public static int getEvent(String eventName) {
        if ( eventName != null ) {
            if ("account_created".equalsIgnoreCase(eventName)) {
                return ACCOUNT_CREATED;
            } else if ("login".equalsIgnoreCase(eventName)) {
                return LOGIN;
            } else if ("logout".equalsIgnoreCase(eventName)) {
                return LOGOUT;
            } else if ("reading_education".equalsIgnoreCase(eventName)) {
                return READING_EDUCATION;
            } else if ("visit_page".equalsIgnoreCase(eventName)) {
                return VISIT_PAGE;
            } else if ("add_document".equalsIgnoreCase(eventName)) {
                return ADD_DOCUMENT;
            }
        }
        return UNKNOWN;
    }
}
