/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.tools.MMAppTool;

import java.awt.*;
import java.util.*;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.util.xml.BuilderReader;

/**
 * Class BuilderOval
 *
 * @application MMAppTool
 * @javadoc
 */
public class BuilderRect extends Object {

    private AppCanvas parent;
    private float bscale;
    private final static boolean debug=false;
    private boolean active=false;
    private int x,y,dx,dy,h;
    private FontMetrics fm;
    private Font namefont;
    private BuilderReader bul;
    private Vector fields;
    private String name;

    public BuilderRect(AppCanvas parent,String name,BuilderReader bul,int size,int x, int y) {
        this.parent=parent;
        this.name=name;
        this.bul=bul;
        this.x=x;
        this.y=y;
        this.bscale=1.1F;
        namefont=new Font("Arial",Font.BOLD,size);
    }

    public void paint(Graphics g) {
        if (fm==null) {
            fm=g.getFontMetrics(namefont);
            recalc();
        }
        g.setColor(parent.getObjectColor());
        g.fillRoundRect(x,y+50,dx,dy,10,10);
        g.fillRoundRect(x,y,dx,30,10,10);
        if (active) {
            g.setColor(parent.getActiveColor());
        } else {
            g.setColor(parent.getLineColor());
        }
        g.drawRoundRect(x,y+50,dx,dy,10,10);
        g.drawRoundRect(x,y,dx,30,10,10);

        g.setColor(parent.getTextColor());
        g.setFont(namefont);

        g.drawString("Builder : \""+name+"\"       Version : \""+bul.getBuilderVersion()+" / "+bul.getBuilderMaintainer()+"\"",x+15,y+h+2);

        // first set the header
        g.drawString("GUIName",x+15,y+50+h);
        g.drawString("DBName",x+150,y+50+h);
        g.drawString("DBType",x+300,y+50+h);
        g.drawString("DBSize",x+450,y+50+h);

        int ypos=y+50+(h*3);
        for (Enumeration e=fields.elements();e.hasMoreElements();) {
            FieldDefs def=(FieldDefs)e.nextElement();
            String dbname=def.getDBName();
            if (!dbname.equals("number") && !dbname.equals("owner")) {
                g.drawString(def.getGUIName(),x+15,ypos);
                g.drawString(def.getDBName(),x+150,ypos);
                g.drawString(getTypeString(def.getDBType()),x+300,ypos);
                if (def.getDBSize()!=-1) {
                    g.drawString(""+def.getDBSize(),x+450,ypos);
                }
                ypos+=h;
            }
        }
    }


    public void recalc() {
        // recalc my oval based on the name, font, size
        fields=bul.getFieldDefs();

        /*
        for (Enumeration e=fields.elements();e.hasMoreElements();) {
            FieldDefs def=(FieldDefs)e.nextElement();
            System.out.println("def="+def);
        }
         */

        h=fm.getHeight();
        dx=(int)(500*bscale);
        dy=(int)((h*fields.size())*bscale);
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

    public String getTypeString(int dbtype) {
        return FieldDefs.getDBTypeDescription(dbtype);
    }

}
