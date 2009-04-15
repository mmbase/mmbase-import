/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Remco Bos
 */
public abstract class HibernateService {

    private SessionFactory sessionFactory;

    /**
     * Injects a Hibernate session factory.
     *
     * @param sessionFactory the session factory to use
     */
    @Required
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns the current Hibernate session.
     *
     * @return a Hibernate session bound to the current transaction
     */
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
