/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine.util;

import java.util.*;
import java.io.*;
/**
 * @author keesj
 * @version $Id: DollarReplacer.java,v 1.2 2004-04-05 11:52:33 keesj Exp $
 */
public class DollarReplacer {
    Properties properties = new Properties();
    public DollarReplacer() {
        super();
    }

    public static void replace(Properties props, Reader in, Writer out) throws IOException {
        int c;
        
        while ( (c =  in.read()) >= 0) {
            if (c == '$') { // we have a dollar statement
                StringBuffer sb = new StringBuffer();
                //TODO:keesj redefiine  what a keyword is and do something like while(char.isAlfa)
                while ((c = in.read()) >= 0 && c != ' ' && c != ';' && c!= '$') {
                	sb.append((char)c);
                }
                String key = sb.toString();
                String value = props.getProperty(key);
                if (value == null){
                	out.write(key);
                } else {
                	out.write(value);
                }
				if (c != -1){
					out.write((char)c);
				}

            } else {
                out.write((char)c);
            }
        }
    }
    
    public static void main(String [] argv) throws IOException{
    	System.err.println("type $mmbase please");
    	Reader in = new StringReader("hoi !!! $mmbase and $opencms rule");
    	Writer w = new OutputStreamWriter(System.out);
    	Properties p = new Properties();
    	p.setProperty("opencms","mmbase");
    	
    	DollarReplacer.replace(p,in,w);
    	w.flush();
		System.err.println("thanks");
    }
}
