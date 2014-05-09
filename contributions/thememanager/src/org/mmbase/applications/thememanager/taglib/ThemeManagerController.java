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

	private static Logger log = Logging.getLoggerInstance(ThemeManagerController.class);
	private static HashMap types;


	public static List getAssignedList() {
		log.info("get assignedlist");
                List list = new ArrayList();

		HashMap m=getAssigned();
		Iterator keys=m.keySet().iterator();
		while (keys.hasNext()) {
			HashMap map = new HashMap();
			String k=(String)keys.next();
			String v=(String)m.get(k);
                        map.put("id",k);
                        map.put("theme",v);
			list.add(map);
		}
		return list;
        }


	public static List getThemesList() {
		log.info("get themeslist");
                List list = new ArrayList();

		HashMap m=getThemes();
		Iterator keys=m.keySet().iterator();
		while (keys.hasNext()) {
			HashMap map = new HashMap();
			String k=(String)keys.next();
                        map.put("id",k);
			list.add(map);
		}
		return list;
        }


	public static Map getAssignInfo(String id) {
		Map map = new HashMap();	
		String themename=getAssign(id);
		if (themename!=null) {
                        map.put("theme",themename);
		}
		return map;
        }

	public static Map getThemeInfo(String id) {
		Map map = new HashMap();	
		Theme th=getTheme(id);
		if (th!=null) {
                        map.put("id",id);
                        map.put("stylesheetscount",new Integer(th.getStyleSheetsCount()));
                        map.put("imagesetscount",new Integer(th.getImageSetsCount()));
		}
		return map;
        }


	public static List getThemeStyleSheetsList(String id) {
                List list = new ArrayList();

		Theme th=getTheme(id);
		if (th!=null) {
			HashMap m=th.getStyleSheets();
			if (m!=null) {
				Iterator keys=m.keySet().iterator();
				while (keys.hasNext()) {
					String k=(String)keys.next();
					String v=(String)m.get(k);
					HashMap map = new HashMap();
                        		map.put("id",k);
                        		map.put("path",v);
					list.add(map);
				}
			}
		}
		return list;
        }


	public static List getThemeImageSetsList(String id) {
        	return getThemeImageSetsList(id,"");
    	}


	public static List getThemeImageSetsList(String id, String role) {
        List list = new ArrayList();
	if (role==null) role="";

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
	    if (m!=null) {
			Iterator keys=m.keySet().iterator();
			while (keys.hasNext()) {
				String k=(String)keys.next();
				ImageSet is=(ImageSet)m.get(k);
				HashMap map =  new HashMap();
                		map.put("id",k);
                		map.put("role",is.getRole());
                		map.put("imagecount",new Integer(is.getCount()));
                		list.add(map);
			}
		}
	}
	return list;
        }




	public static List getStyleSheetClasses(String id,String cssid,String searchkey) {
                List list = new ArrayList();

		Theme th=getTheme(id);
		if (th!=null) {
			StyleSheetManager sts=th.getStyleSheetManager(cssid);
			if (sts!=null) {
				Iterator i=sts.getStyleSheetClasses();
				while (i.hasNext()) {
					StyleSheetClass stc=(StyleSheetClass)i.next();
					HashMap map =  new HashMap();
					if (searchkey.equals("*") || stc.getId().indexOf(searchkey)!=-1) {
                        			map.put("id",stc.getId());
                        			map.put("propertycount",new Integer(stc.getPropertyCount()));
						list.add(map);
					}
				}
			}
		}
		return list;
        }


	public static boolean setStyleSheetProperty(String themeid,String cssid,String id,String name,String value) { 
		Theme th=getTheme(themeid);
		if (th!=null) {
			StyleSheetManager sts=th.getStyleSheetManager(cssid);
			if (sts!=null) {
				StyleSheetClass ssc=sts.getStyleSheetClass(id);
				if (ssc!=null) {
					StyleSheetProperty ssp  = ssc.getProperty(name);
					if (ssp!=null) { 
						ssp.setValue(value);
						sts.save();
					}
				}
			}
		}
		return true;
 	}


	public static boolean addStyleSheetProperty(String themeid,String cssid,String id,String name,String value) { 
		Theme th=getTheme(themeid);
		if (th!=null) {
			StyleSheetManager sts=th.getStyleSheetManager(cssid);
			if (sts!=null) {
				StyleSheetClass ssc=sts.getStyleSheetClass(id);
				if (ssc!=null) {
					ssc.setProperty(name,value);
					sts.save();
				}
			}
		}
		return true;
 	}


	public static boolean addStyleSheetClass(String themeid,String cssid,String name) { 
		Theme th=getTheme(themeid);
		if (th!=null) {
			StyleSheetManager sts=th.getStyleSheetManager(cssid);
			if (sts!=null) {
				sts.addStyleSheetClass(name);
				sts.save();
			}
		}
		return true;
 	}


	public static boolean addTheme(String copytheme,String newtheme) { 
		Theme th=getTheme(copytheme);
		if (th!=null) {
			copyTheme(th,newtheme);
		}
		return true;
 	}


	public static boolean removeStyleSheetClass(String themeid,String cssid,String name) { 
		Theme th=getTheme(themeid);
		if (th!=null) {
			StyleSheetManager sts=th.getStyleSheetManager(cssid);
			if (sts!=null) {
				sts.removeStyleSheetClass(name);
				sts.save();
			}
		}
		return true;
 	}


	public static boolean removeStyleSheetProperty(String themeid,String cssid,String id,String name) { 
		Theme th=getTheme(themeid);
		if (th!=null) {
			StyleSheetManager sts=th.getStyleSheetManager(cssid);
			if (sts!=null) {
				StyleSheetClass ssc=sts.getStyleSheetClass(id);
				if (ssc!=null) {
					ssc.removeProperty(name);
					sts.save();
				}
			}
		}
		return true;
 	}

	public static String getStyleSheetProperty(String themeid,String cssid,String id,String name) { 
		Theme th=getTheme(themeid);
		if (th!=null) {
			StyleSheetManager sts=th.getStyleSheetManager(cssid);
			if (sts!=null) {
				StyleSheetClass ssc=sts.getStyleSheetClass(id);
				if (ssc!=null) {
					StyleSheetProperty ssp  = ssc.getProperty(name);
					if (ssp!=null) { 
						return ssp.getValue();
					}
				}
			}
		}
		return ""; 
 	}

	public static List getStyleSheetProperties(String themeid,String cssid,String id) {
                List list = new ArrayList();

		Theme th=getTheme(themeid);
		if (th!=null) {
			StyleSheetManager sts=th.getStyleSheetManager(cssid);
			if (sts!=null) {
				StyleSheetClass ssc=sts.getStyleSheetClass(id);
				if (ssc!=null) {
					Iterator i=ssc.getProperties();
					while (i.hasNext()) {
						StyleSheetProperty ssp=(StyleSheetProperty)i.next();
						HashMap map = new HashMap();
                        			map.put("name",ssp.getName());
                        			map.put("value",ssp.getValue());
						list.add(map);
					}
				}
			}
		}
		return list;
        }


	public static List getStyleSheet(String id) {
                List list = new ArrayList();
		Theme th=getTheme(id);
		if (th!=null) {
			HashMap m=th.getStyleSheets();
			Iterator keys=m.keySet().iterator();
			while (keys.hasNext()) {
				String k=(String)keys.next();
				String v=(String)m.get(k);
				HashMap  map = new HashMap();
                        	map.put("id",k);
                        	map.put("path",v);
				list.add(map);
			}
		}
		return list;
        }

	public boolean setCSSValue(String path,String value) {
		StringTokenizer tok = new StringTokenizer(path,"/\n\r");
		if (tok.hasMoreTokens()) {
			String themename = tok.nextToken();
			if (tok.hasMoreTokens()) {
				String stylesheet =  tok.nextToken();
				if (tok.hasMoreTokens()) {
					String classname =  tok.nextToken();
					if (tok.hasMoreTokens()) {
						String propertyname =  tok.nextToken();
						setStyleSheetProperty(themename,stylesheet,classname,propertyname,value);
					}
				}
			}
		}
		return true;
        }

	public String getCSSValue(String path) {

		StringTokenizer tok = new StringTokenizer(path,"/\n\r");
		if (tok.hasMoreTokens()) {
			String themename = tok.nextToken();
			if (tok.hasMoreTokens()) {
				String stylesheet =  tok.nextToken();
				if (tok.hasMoreTokens()) {
					String classname =  tok.nextToken();
					if (tok.hasMoreTokens()) {
						String propertyname =  tok.nextToken();
						return getStyleSheetProperty(themename,stylesheet,classname,propertyname);
					}
				}
			}
		}
		return "";
	}


	public String getCSSType(String path) {
		if (types==null) fillTypes();

		StringTokenizer tok = new StringTokenizer(path,"/\n\r");
		if (tok.hasMoreTokens()) {
			String themename = tok.nextToken();
			if (tok.hasMoreTokens()) {
				String stylesheet =  tok.nextToken();
				if (tok.hasMoreTokens()) {
					String classname =  tok.nextToken();
					if (tok.hasMoreTokens()) {
						String propertyname =  tok.nextToken();
						String type=(String)types.get(propertyname);
						if (type!=null) return type;
					}
				}
			}
		}
		return "default";
	}

	public void fillTypes() {
		types =  new HashMap();
		types.put("color","color");
		types.put("background","color");
		types.put("font-family","font");
		types.put("font-size","fontsize");
        }

        public boolean hasChanged() {
		if (haschanged) {
			haschanged=false;
			return true;
		} 
		return false;
	}
}
