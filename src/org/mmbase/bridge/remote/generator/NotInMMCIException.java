/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.bridge.remote.generator;

/**
 * @author Kees Jongenburger <keesj@framfab.nl>
 **/
public class NotInMMCIException extends Exception{
    public NotInMMCIException(String message){
        super(message);
    }
}
