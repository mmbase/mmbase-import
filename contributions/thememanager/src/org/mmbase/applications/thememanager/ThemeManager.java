/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.thememanager;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.io.*;
import java.util.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.*;
import org.mmbase.module.core.*;

public class ThemeManager {

    private static Logger log = Logging.getLoggerInstance(ThemeManager.class);


    /** DTD resource filename of the themes DTD version 1.0 */
    public static final String DTD_THEMES_1_0 = "themes_1_0.dtd";

    /** Public ID of the themes DTD version 1.0 */
    public static final String PUBLIC_ID_THEMES_1_0 = "-//MMBase//DTD themes config 1.0//EN";

    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_THEMES_1_0, DTD_THEMES_1_0, ThemeManager.class);
    }

  static{
    //First static initializer block
   init();
  }//end first static initializer block

    private static HashMap themes;
    private static HashMap assigned;

    private static void init() { 
	readThemes();
	readAssigned();
    }


        public static String getStyleSheet(String context,String themeid,String cssid) {
		String as=(String)assigned.get(themeid);
		if (as!=null) {
			Theme th=(Theme)themes.get(as);
			if (th!=null) {
				String filename=th.getStyleSheet();
				if (filename!=null) {
                    return(context+"/css/"+filename);
				} else {
					return("");
				}
			} else {
				return("");
			}
		} else {
			return("");
		}
	}


    public static void readThemes() {
       themes=new HashMap();
		try {
	        InputSource is = ResourceLoader.getConfigurationRoot().getInputSource("thememanager/themes.xml");
                DocumentReader reader = new DocumentReader(is,ThemeManager.class);
            	if(reader!=null) {
			// decode themes
            		for(Iterator ns=reader.getChildElements("themes","theme");ns.hasNext(); ) {
                		Element n=(Element)ns.next();
   		               	NamedNodeMap nm=n.getAttributes();
                    		if (nm!=null) {
					String id=null;
					String themefilename=null;
				
					// decode name
                        		org.w3c.dom.Node n3=nm.getNamedItem("id");
                        		if (n3!=null) {
						id=n3.getNodeValue();
					}
					// decode filename
                        		n3=nm.getNamedItem("file");
                        		if (n3!=null) {
						themefilename=n3.getNodeValue();
					}
					Theme newtheme=new Theme(id,themefilename);
					themes.put(id,newtheme);
			    	}
			}
		}

	} catch (Exception e) {
            log.error("missing themes file ");
	}
    }


    public static void readAssigned() {
        assigned=new HashMap();
        try {
       		InputSource is = ResourceLoader.getConfigurationRoot().getInputSource("thememanager/assigned.xml");
                DocumentReader reader = new DocumentReader(is,ThemeManager.class);
            	if(reader!=null) {
			// decode assigned 
            		for (Iterator ns=reader.getChildElements("assigned","assign");ns.hasNext(); ) {
                		Element n=(Element)ns.next();
   		               	NamedNodeMap nm=n.getAttributes();
                    		if (nm!=null) {
					String id=null;
					String themeid=null;
				
					// decode name
                        		org.w3c.dom.Node n3=nm.getNamedItem("id");
                        		if (n3!=null) {
						id=n3.getNodeValue();
					}
					// decode filename
                        		n3=nm.getNamedItem("theme");
                        		if (n3!=null) {
						themeid=n3.getNodeValue();
					}
					assigned.put(id,themeid);
			    	}
			}
		}

	} catch (Exception e) {
            log.error("missing assigned file");
	}
    }

   public static HashMap getAssigned() {
	return assigned;
   }

   public static HashMap getThemes() {
	return themes;
   }

   public static String getAssign(String id) {
	return (String)assigned.get(id);
   }

   public static Theme getTheme(String id) {
	return (Theme)themes.get(id);
   }

   public static String getThemeImage(String context, String themeid,String imageid) {
	return getThemeImage(context,themeid,"default",imageid);
  }


   public static String getThemeImage(String context, String id,String imagesetid,String imageid) {
	String themeid=(String)assigned.get(id);
	if (themeid!=null) {
	Theme th=(Theme)themes.get(themeid);
	if (th!=null) {
		ImageSet im=th.getImageSet(imagesetid);
		if (im!=null) {
			String result=im.getImage(imageid);
			if (result!=null) {
				return context + "/"+themeid+"/"+imagesetid+"/"+result;
			}
		}
	}
	}
	return ""; 
   }

   public static List getThemeImages(String context, String themeID) {
       return getThemeImages(context, themeID,"default");
  }


   public static List getThemeImages(String context, String id, String imagesetid) {
       List list = new ArrayList();
       VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
       String themeid=(String)assigned.get(id);
       if (themeid!=null) {
           Theme th=(Theme)themes.get(themeid);
           if (th!=null) {
               ImageSet im=th.getImageSet(imagesetid);
               if (im!=null) {
                   Iterator it = im.getImageIds();
                   String imageLocation;
                   String imageID;
                   MMObjectNode virtual;
                   while (it.hasNext()) {
                       virtual = builder.getNewNode("admin");
                       imageID = (String)it.next();
                       imageLocation = im.getImage(imageID);
                       virtual.setValue("id",imageID);
                       virtual.setValue("imagelocation",context + "/"+themeid+"/"+imagesetid+"/"+imageLocation);
                       list.add(virtual);
                   }
               }
           }
       }
       return list; 
   }

   public static boolean changeAssign(String id,String newtheme) {
	  assigned.put(id,newtheme); 
	  AssignedFileWriter.write();
          return true;
   }


   public static boolean addAssign(String newid,String newtheme) {
	  assigned.put(newid,newtheme); 
	  AssignedFileWriter.write();
          return true;
   }


   public static boolean removeAssign(String removeid) {
	  assigned.remove(removeid); 
	  AssignedFileWriter.write();
          return true;
   }


}
