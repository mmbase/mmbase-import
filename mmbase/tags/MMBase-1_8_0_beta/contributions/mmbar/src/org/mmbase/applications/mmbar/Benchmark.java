/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.mmbar;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author     Daniel Ockeloen
 * @created    February 28, 2005
 */
public class Benchmark {

    // score of the test
    private float result;

    // os the test was ran on
    private String os;

    // cpu the test was ran on 
    private String cpu;

    // database the test was ran on
    private String database;

    // driver the test was ran on
    private String driver;

    // server the test was ran on
    private String server;

    // java version the test was ran on
    private String java;

    /**
     *  Gets the result (score)
     *
     * @return    The result (score)
     */
    public float getResult() {
        return result;
    }


    /**
     *  Sets the result (score) 
     *
     * @param  result  The new result (score)
     */
    public void setResult(float result) {
        this.result = result;
    }


    /**
     *  Gets the os the rest was ran on
     *
     * @return    os ident
     */
    public String getOS() {
        return os;
    }


    /**
     *  Sets the os of the test
     *
     * @param  os  The new os value
     */
    public void setOS(String os) {
        this.os = os;
    }


    /**
     *  Gets the cpu of the test
     *
     * @return    cpu ident
     */
    public String getCPU() {
        return cpu;
    }


    /**
     *  Sets the cpu of the test
     *
     * @param  cpu of the test
     */
    public void setCPU(String cpu) {
        this.cpu = cpu;
    }


    /**
     *  Gets the database of the test
     *
     * @return    the database of the test
     */
    public String getDatabase() {
        return database;
    }


    /**
     *  Sets the database of the test
     *
     * @param  database set the database of the test
     */
    public void setDatabase(String database) {
        this.database = database;
    }


    /**
     *  Gets the driver of the test
     *
     * @return    The driver value
     */
    public String getDriver() {
        return driver;
    }


    /**
     *  Sets the driver of the test
     *
     * @param  driver of the test
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }


    /**
     *  Gets the server of the test
     *
     * @return    The server value
     */
    public String getServer() {
        return server;
    }


    /**
     *  Sets the server of the test
     *
     * @param  server of the test
     */
    public void setServer(String server) {
        this.server = server;
    }


    /**
     *  Gets the java version of the test 
     *
     * @return    The java version
     */
    public String getJava() {
        return java;
    }


    /**
     *  Sets the java version of the test
     *
     * @param  java  The new java value
     */
    public void setJava(String java) {
        this.java = java;
    }

}

