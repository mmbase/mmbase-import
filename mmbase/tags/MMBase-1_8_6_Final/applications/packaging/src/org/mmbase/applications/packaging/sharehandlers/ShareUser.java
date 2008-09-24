/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.sharehandlers;

/**
 * @author     Daniel Ockeloen
 */
public class ShareUser {

    private String name;
    private String password;
    private String method;
    private String host;


    /**
     *Constructor for the ShareUser object
     *
     * @param  name  Description of the Parameter
     */
    public ShareUser(String name) {
        this.name = name;
    }


    /**
     *  Sets the name attribute of the ShareUser object
     *
     * @param  name  The new name value
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *  Sets the password attribute of the ShareUser object
     *
     * @param  password  The new password value
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     *  Sets the method attribute of the ShareUser object
     *
     * @param  method  The new method value
     */
    public void setMethod(String method) {
        this.method = method;
    }


    /**
     *  Sets the host attribute of the ShareUser object
     *
     * @param  host  The new host value
     */
    public void setHost(String host) {
        this.host = host;
    }


    /**
     *  Gets the name attribute of the ShareUser object
     *
     * @return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     *  Gets the password attribute of the ShareUser object
     *
     * @return    The password value
     */
    public String getPassword() {
        return password;
    }


    /**
     *  Gets the method attribute of the ShareUser object
     *
     * @return    The method value
     */
    public String getMethod() {
        return method;
    }


    /**
     *  Gets the host attribute of the ShareUser object
     *
     * @return    The host value
     */
    public String getHost() {
        return host;
    }

}

