/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.tools.MMAppTool;

import java.awt.*;

/**
 * Class BuilderOval
 *
 * @application MMAppTool
 * @javadoc
 */
public class BuilderOval extends Object {

    private AppCanvas parent;
    private int x,y;
    private int dx,dy,fx,fy,fl;
    private String name;
    private int size;
    private float bscale;
    private Font namefont;
    private FontMetrics fm;
    private final static boolean debug=false;
    private boolean active=false;

    public BuilderOval(AppCanvas parent,String name,int size,int x,int y) {
        this.parent=parent;
        this.x=x;
        this.y=y;
        this.size=size;
        this.name=name;
        this.bscale=2;
        namefont=new Font("Arial",Font.BOLD,size);
    }

    public void paint(Graphics g) {
        if (fm==null) {
            fm=g.getFontMetrics(namefont);
            recalc();
        }
        g.setColor(parent.getObjectColor());
        g.fillOval(x-(dx/2),y-(dy/2),dx,dy);
        if (active) {
            g.setColor(parent.getActiveColor());
        } else {
            g.setColor(parent.getLineColor());
        }
        g.drawOval(x-(dx/2),y-(dy/2),dx,dy);
        g.setFont(namefont);
        g.setColor(parent.getTextColor());
        g.drawString(name,x-(fx/2),y+(fy/2)-(fl/2));

        if (debug) {
            g.drawRect(x-(dx/2),y-(dy/2),dx,dy);
            g.drawLine(x-(dx/2),y-(dy/2),x+(dx/2),y+(dy/2));
            g.drawLine(x-(dx/2),y+(dy/2),x+(dx/2),y-(dy/2));
        }
    }

    public void recalc() {
        // recalc my oval based on the name, font, size
        fx=fm.stringWidth(name);
        fy=fm.getHeight();
        fl=fm.getAscent()-fm.getLeading();
        dx=(int)(fx*bscale);
        dy=(int)(fy*bscale);
    }

    public boolean isInside(int mx, int my) {
        if (mx>(x-(dx/2)) && mx<(x+(dx/2))) {
            if (my>(y-(dy/2)) && my<(y+(dy/2))) {
                return(true);
            }
        }
        return(false);
    }

    public void setActive(boolean active) {
        this.active=active;
    }

    public boolean getActive() {
        return(active);
    }

    public String getName() {
        return(name);
    }

    public void setX(int x) {
        this.x=x;
    }

    public void setY(int y) {
        this.y=y;
    }

    public int getX() {
        return(x);
    }

    public int getY() {
        return(y);
    }

    public int getMaxX() {
        return x + (dx / 2);
    }

    public int getMaxY() {
        return y + (dy / 2);
    }

    public int getFontSize() {
        return(size);
    }
}
