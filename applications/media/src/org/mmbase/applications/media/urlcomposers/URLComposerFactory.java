/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/


package org.mmbase.applications.media.urlcomposers;
import org.mmbase.module.core.MMObjectNode;
import java.util.*;
/**
 * The URLComposerFactory contains the code to decide which kind of URLComposer is instatiated.
 *
 * @author Michiel Meeuwissen
 * @version $Id: URLComposerFactory.java,v 1.1 2003-02-03 18:06:20 michiel Exp $
 */

public class URLComposerFactory  {

    private static URLComposerFactory instance = new URLComposerFactory();

    private URLComposerFactory() { }

    public  static URLComposerFactory getInstance() {
        return instance;
    }

    public  List createURLComposers(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        List result = new ArrayList();
        result.add(new MediaURLComposer(provider, source, fragment, info));
        return result;
    }

}
