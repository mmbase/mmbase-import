/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools.MMAppTool;

import java.lang.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Class RelationLine
 * 
 * @javadoc
 */

public class RelationLine extends Object {

	private AppCanvas parent;
	private float bscale;
	private final static boolean debug=false;
	private boolean active=false;
	private BuilderOval fb,tb;

	public RelationLine(AppCanvas parent,BuilderOval fb,BuilderOval tb) {
		this.parent=parent;
		this.bscale=2;
		this.fb=fb;
		this.tb=tb;
	}

	public void paint(Graphics g) {
		if (active) {
			g.setColor(parent.getActiveColor());
		} else {
			g.setColor(parent.getLineColor());
		}
		g.drawLine(fb.getX(),fb.getY(),tb.getX(),tb.getY());
		//g.drawLine(fb.getX()+1,fb.getY()+1,tb.getX()+1,tb.getY()+1);
	}


	public boolean isInside(int mx, int my) {
		return(false);
	}

	public void setActive(boolean active) {
		this.active=active;
	}

	public boolean getActive() {
		return(active);
	}

}
