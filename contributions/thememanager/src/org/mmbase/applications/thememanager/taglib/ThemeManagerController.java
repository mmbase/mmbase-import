/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.thememanager.taglib;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.util.logging.*;
import org.mmbase.applications.thememanager.*;



/**
 * @author Daniel Ockeloen
 * @version $Id: guiController.java
 */
public class ThemeManagerController extends ThemeManager {

	private static Logger log = Logging.getLoggerInstance(ThemeManagerController.class.getName());

	public static List getAssignedList() {
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		HashMap m=getAssigned();
		Iterator keys=m.keySet().iterator();
		while (keys.hasNext()) {
                        MMObjectNode virtual = builder.getNewNode("admin");
			String k=(String)keys.next();
			String v=(String)m.get(k);
                        virtual.setValue("id",k);
                        virtual.setValue("theme",v);
			list.add(virtual);
		}
		return list;
        }


	public static List getThemesList() {
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		HashMap m=getThemes();
		Iterator keys=m.keySet().iterator();
		while (keys.hasNext()) {
                        MMObjectNode virtual = builder.getNewNode("admin");
			String k=(String)keys.next();
			//Theme th=(Theme)m.get(k);
                        virtual.setValue("id",k);
                        //virtual.setValue("theme",v);
			list.add(virtual);
		}
		return list;
        }


	public static MMObjectNode getAssignInfo(String id) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");

		String themename=getAssign(id);
		if (themename!=null) {
                        virtual.setValue("theme",themename);
		}
		return virtual;
        }


	public static MMObjectNode getThemeInfo(String id) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");

		Theme th=getTheme(id);
		if (th!=null) {
                        virtual.setValue("id",id);
                        virtual.setValue("stylesheetscount",th.getStyleSheetsCount());
                        virtual.setValue("imagesetscount",th.getImageSetsCount());
		}
		return virtual;
        }


	public static List getThemeStyleSheetsList(String id) {
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		Theme th=getTheme(id);
		if (th!=null) {
			HashMap m=th.getStyleSheets();
			Iterator keys=m.keySet().iterator();
			while (keys.hasNext()) {
				String k=(String)keys.next();
				String v=(String)m.get(k);
                		MMObjectNode virtual = builder.getNewNode("admin");
                        	virtual.setValue("id",k);
                        	virtual.setValue("path",v);
				list.add(virtual);
			}
		}
		return list;
        }


	public static List getThemeImageSetsList(String id) {
        return getThemeImageSetsList(id,"");
    }


	public static List getThemeImageSetsList(String id, String role) {
        log.debug("role=" + role +" and id =" + id);
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        //don't know if this is a nice way of doing this
        String assignedID = getAssign(id);
        Theme th;
        if (assignedID == null) {
            th = getTheme(id);
        } else {
            th = getTheme(assignedID);
        }

       
		if (th!=null) {
			HashMap m;
            if (role.equals("")) {
                m = th.getImageSets();
            } else {
                m = th.getImageSets(role);
            }
			Iterator keys=m.keySet().iterator();
			while (keys.hasNext()) {
				String k=(String)keys.next();
				ImageSet is=(ImageSet)m.get(k);
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("id",k);
                virtual.setValue("role",is.getRole());
                virtual.setValue("imagecount",is.getCount());
                list.add(virtual);
			}
		}
		return list;
        }



}
