/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

import org.mmbase.module.corebuilders.*;

public class ErrorStruct {
    String errorType;
    int line;
    int col;
    String msg;
    
    public ErrorStruct(int line, int col, String msg) {
	this("none",line,col,msg);
    }
    public ErrorStruct(String errorType, int line, int col, String msg) {
	this.errorType = errorType;
	this.line = line;
	this.col = col;
	this.msg = msg;
    }

    public String getErrorType() {
	return errorType;
    }

    public int getLineNumber() {
	return line;
    }

    public int getColumnNumber() {
	return col;
    }

    public String getMessage() {
	return msg;
    }
}
