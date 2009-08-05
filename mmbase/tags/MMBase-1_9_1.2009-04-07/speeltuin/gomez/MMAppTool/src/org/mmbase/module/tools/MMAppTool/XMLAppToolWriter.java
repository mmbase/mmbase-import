/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.tools.MMAppTool;

import java.io.*;
import java.awt.*;
import java.util.*;

/**
 * Class XMLAppToolWriter
 *
 * @application MMAppTool
 * @javadoc
 */
public class XMLAppToolWriter  {

    public static void writeXMLFile(AppCanvas can,String filename) {

        String body="<mmapptoolconfig>\n\n";

        body+="\t<colors>\n";
        Color col=can.getBackGroundColor();
        body+="\t\t<backgroundcolor red=\""+col.getRed()+"\" green=\""+col.getGreen()+"\" blue=\""+col.getBlue()+"\" />\n";
        col=can.getObjectColor();
        body+="\t\t<objectcolor red=\""+col.getRed()+"\" green=\""+col.getGreen()+"\" blue=\""+col.getBlue()+"\" />\n";
        col=can.getTextColor();
        body+="\t\t<textcolor red=\""+col.getRed()+"\" green=\""+col.getGreen()+"\" blue=\""+col.getBlue()+"\" />\n";
        col=can.getLineColor();
        body+="\t\t<linecolor red=\""+col.getRed()+"\" green=\""+col.getGreen()+"\" blue=\""+col.getBlue()+"\" />\n";
        col=can.getActiveColor();
        body+="\t\t<activecolor red=\""+col.getRed()+"\" green=\""+col.getGreen()+"\" blue=\""+col.getBlue()+"\" />\n";
        body+="\t</colors>\n\n";


        body+=getBuilders(can.getBuilderOvals());
        // close the file
        body+="</mmapptoolconfig>\n\n";
        saveFile(filename,body);
    }

    private static String getBuilders(Vector builders) {
        String body="\t<builderovallist>\n";
        for (Enumeration e=builders.elements();e.hasMoreElements();) {
            BuilderOval b=(BuilderOval)e.nextElement();
            String name=b.getName();
            int x=b.getX();
            int y=b.getY();
            int s=b.getFontSize();
            body+="\t\t<builderoval name=\""+name+"\" xpos=\""+x+"\" ypos=\""+y+"\" fontsize=\""+s+"\" />\n";
        }
        body+="\t</builderovallist>\n\n";
        return(body);
    }


    static boolean saveFile(String filename,String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return(true);
    }


}
