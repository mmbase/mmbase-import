/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.tools.MMAppTool;

/**
 * Class MMAppTool
 *
 * @application MMAppTool
 * @javadoc
 */
public class MMAppTool extends Object {

    Display dis;

    public MMAppTool() {
        // create the Display
        dis = new Display(this);
        dis.show();
    }


    public MMAppTool(String appfile) {
        // create the Display
        dis = new Display(this,appfile);
        dis.show();
    }

    public static void main(String[] args) {
        System.out.println("Starting MMAppTool");
        if (args.length>0) {
            MMAppTool app=new MMAppTool(args[0]);
        } else {
            MMAppTool app=new MMAppTool();
        }
    }

    public void doExit() {
        System.out.println("Clossing MMAppTool");
        //System.exit(0);
        dis.hide();
        dis.dispose();
    }
}
