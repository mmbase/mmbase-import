/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers;
import java.util.ResourceBundle;


/**
 * Some url-composers need to know where templates are. Put a properties file in classes.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Config.java,v 1.5 2005-05-14 14:07:42 nico Exp $
 */

public class Config  {

    // should perhaps be changed to XML, but this is easier for the moment. No time, no time!


    public  static final String templatesDir;
    public  static final String host;
    public  static final String editwizardsDir;
    static {
        ResourceBundle manager =  ResourceBundle.getBundle("org.mmbase.applications.media.resources.config");
        templatesDir = manager.getString("templatesDir");
        host         = manager.getString("host");
        editwizardsDir   = manager.getString("editwizardsDir");
    }

}
