/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools.MMAppTool;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import org.mmbase.util.*;

public class AppCanvas extends Canvas implements MouseMotionListener {

	private Display parent;
	private Vector builders=new Vector();
	private Vector relations=new Vector();
	private Vector relationsources=new Vector();
	private Image offScreenImage; 
        private Graphics offScreenGraphics; 
        private Dimension offScreenSize; 
	private BuilderOval db;
	private int textsize=15;
	private Color bgcolor=new Color(0,0,255);
	private Color activecolor=new Color(255,0,0);
	private Color linecolor=new Color(0,0,0);
	private Color textcolor=new Color(0,0,0);
	private Color objectcolor=new Color(255,255,0);

	public AppCanvas(Display parent) {
		this.parent=parent;
		this.addMouseMotionListener(this);	
	}

	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		if (cmd.equals("Exit")) {
			//parent.doExit();
		} else {
			System.out.println("Unknown="+cmd);
		}
	}


	public final synchronized void update (Graphics theG) {

          Dimension d = size();

          if ((offScreenImage == null) 
              || (d.width != offScreenSize.width) 
              || (d.height != offScreenSize.height))
          { 
              offScreenImage = createImage(d.width, d.height); 
              offScreenSize = d; 
              offScreenGraphics = offScreenImage.getGraphics(); 
          }
          offScreenGraphics.setColor(bgcolor); 
          offScreenGraphics.fillRect(0, 0, d.width, d.height); 
          paint(offScreenGraphics); 
          theG.drawImage(offScreenImage, 0, 0, null);
      }

	public void paint(Graphics g) {
		setBackground(getBackGroundColor());

		// draw the Relations
		for (Enumeration e=relations.elements();e.hasMoreElements();) {
			RelationLine r=(RelationLine)e.nextElement();
			r.paint(g);
		}

		// draw the BuilderOvals
		for (Enumeration e=builders.elements();e.hasMoreElements();) {
			BuilderOval b=(BuilderOval)e.nextElement();
			b.paint(g);
		}
	}

	public void setApplication(XMLApplicationReader app) {
		Random rnd=new Random();
		
		// takes all the info out of a reader
		// and sets up the display objects;
		builders=new Vector();		
		Vector wb=app.getDataSources();
		for (Enumeration e=wb.elements();e.hasMoreElements();) {
			Hashtable bset=(Hashtable)e.nextElement();
			String name=(String)bset.get("builder");
			//String maintainer=(String)bset.get("maintainer");
			//String version=(String)bset.get("version");
			//System.out.println("maintainer=\""+maintainer+"\" version=\""+version+"\">"+name+"</builder>");

			int rx=40+Math.abs(rnd.nextInt()%(600));
			int ry=40+Math.abs(rnd.nextInt()%(400));
			//System.out.println("rx="+rx+" ry="+ry);
			BuilderOval b=new BuilderOval(this,name,textsize,rx,ry);
			builders.addElement(b);
		}

		// create the relatons between the builders
		relations=new Vector();
		Vector ar=app.getAllowedRelations();
		for (Enumeration e=ar.elements();e.hasMoreElements();) {
			Hashtable bset=(Hashtable)e.nextElement();
			String from=(String)bset.get("from");
			String to=(String)bset.get("to");

			BuilderOval fb=getBuilderOval(from);
			BuilderOval tb=getBuilderOval(to);

			RelationLine rel=new RelationLine(this,fb,tb);
			relations.add(rel);
		}

	}


	private void checkMouseAreas(int x,int y,boolean drag) {
		boolean changed=false;
		for (Enumeration e=builders.elements();e.hasMoreElements();) {
			BuilderOval b=(BuilderOval)e.nextElement();
			if (b.isInside(x,y) && !drag) {
				if (!b.getActive()) {
					// was not active
					b.setActive(true);
					db=b;
					changed=true;	
				}
			} else {
				if (b.getActive() && !drag) {
					b.setActive(false);
					db=null;
					changed=true;	
				}
			}
		}
		if (drag && db!=null) {
			db.setX(x);
			db.setY(y);
			db.recalc();
		}
		if (changed || drag) repaint();
	}

	public void mouseDragged(MouseEvent e) {
		checkMouseAreas(e.getX(),e.getY(),true);
	}

	public void mouseMoved(MouseEvent e) {
		checkMouseAreas(e.getX(),e.getY(),false);
	}

	public BuilderOval getBuilderOval(String name) {
		for (Enumeration e=builders.elements();e.hasMoreElements();) {
			BuilderOval b=(BuilderOval)e.nextElement();
			if (b.getName().equals(name)) return(b); 
		}
		return(null);
	}

	public void setLineColor(Color c) {
		linecolor=c;
	}

	public Color getLineColor() {
		return(linecolor);
	}

	public Color getBackGroundColor() {
		return(bgcolor);
	}

	public void setBackGroundColor(Color c) {
		bgcolor=c;
	}

	public Color getActiveColor() {
		return(activecolor);
	}

	public void setActiveColor(Color c) {
		activecolor=c;
	}

	public Color getTextColor() {
		return(textcolor);
	}

	public void setTextColor(Color c) {
		textcolor=c;
	}

	public Color getObjectColor() {
		return(objectcolor);
	}

	public void setObjectColor(Color c) {
		objectcolor=c;
	}

	public Vector getBuilderOvals() {
		return(builders);
	}


}
