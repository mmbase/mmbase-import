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

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.mmbase.util.logging.*;


/**
 * @author p.becic
 */
public class HibernateUtil {
    private static Logger log = Logging.getLoggerInstance(nl.didactor.reports.util.HibernateUtil.class);

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure()
                    .buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
           log.error("Initial SessionFactory creation failed for component 'reports'. \r\n"+ex);
           throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}