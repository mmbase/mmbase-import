package org.mmbase.service.implementations.dropboxes;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;

import org.mmbase.service.interfaces.*;


/**
 */
public class dropboxDummy implements dropboxInterface {

	public void startUp() {
	}

	public void shutDown() {
	}

	public String getVersion() {		
		return("0");
	}

    public String doDir( String cmds ) {
        return("");
    }


	public void setDir(String dir) {
	}

	public void setCmd(String cmd) {
	}

	public void setWWWPath(String wwwpath) {
	}
}
