/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.tools.MMAppTool;

import java.awt.*;

/**
 * Class RelationLine
 *
 * @application MMAppTool
 * @javadoc
 */
public class RelationLine extends Object {

    private AppCanvas parent;
    private float bscale;
    private final static boolean debug=false;
    private boolean active=false;
    private BuilderOval fb,tb;
    private String type;

    public RelationLine(AppCanvas parent,BuilderOval fb,BuilderOval tb, String type) {
        this.parent=parent;
        this.bscale=2;
        this.fb=fb;
        this.tb=tb;
        this.type=type;
    }

    public void paint(Graphics g) {
        if (active) {
            g.setColor(parent.getActiveColor());
        } else {
            g.setColor(parent.getLineColor());
        }
        if (fb.getName().equals(tb.getName())) {
            g.drawOval(fb.getX()+10,fb.getY()-50,30,50);
            if (!"related".equals(type)) {
                int x=fb.getX()+25;
                int y=fb.getY()-50;
                paintRelationNames(g,x,y);
            }
        }
        g.drawLine(fb.getX(),fb.getY(),tb.getX(),tb.getY());
        //g.drawLine(fb.getX()+1,fb.getY()+1,tb.getX()+1,tb.getY()+1);
        if (!"related".equals(type)) {
            int x=(fb.getX()+tb.getX())/2;
            int y=(fb.getY()+tb.getY())/2;
            paintRelationNames(g,x,y);
        }
    }

    private void paintRelationNames(Graphics g, int x, int y) {
        g.fillOval(x-4,y-4,8,8);
        g.setFont(new Font("Arial",Font.BOLD,12));
        g.drawString(type,x+6,y+4);
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

    public boolean between(BuilderOval fb, BuilderOval tb) {
        if ((this.fb.getName().equals(fb.getName()) && this.tb.getName().equals(tb.getName()))
        || (this.fb.getName().equals(tb.getName()) && this.tb.getName().equals(fb.getName()))) {
            return true;
        } else {
            return false;
        }
    }

    public void addType(String type) {
        this.type = this.type + "," + type;
    }

}
