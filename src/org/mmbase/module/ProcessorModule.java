/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.*;


/**
 * The Processor Module extends the baisc module to the Processor
 * interface so it can perform for servscan (pagelets).
 *
 * @author Daniel Ockeloen
 */
public class ProcessorModule extends Module implements ProcessorInterface {

    /**
     * Returns a virtual builder used to create node lists from the results
     * returned by getList().
     * The default method does not associate the builder with a cloud (mmbase module),
     * so processormodules that need this association need to override this method.
     * Note that different lists may return different builders.
     * @param command the LIST command for which to retrieve the builder
     * @param params contains the attributes for the list
     **/
    public MMObjectBuilder getListBuilder(String command,Map params) {
        return new VirtualBuilder(null);
    }

    /**
     * Generate a list of values from a command to the processor.
     * The values are grouped into nodes.
     * @param context the context of the page or calling application (currently, this should be a scanpage object)
     * @param command the list command to execute.
     * @param params contains the attributes for the list
     * @return a <code>Vector</code> that contains the list values contained in MMObjectNode objects
     **/
    public Vector getNodeList(Object context, String command, Map params) throws ParseException {
        StringTagger tagger=null;
        if (params instanceof StringTagger) {
            tagger= (StringTagger)params;
        } else {
            tagger= new StringTagger("");
            if (params!=null) {
                for (Iterator keys=params.keySet().iterator(); keys.hasNext(); ) {
                    String key=(String)keys.next();
                    Object o = params.get(key);
                    if (o instanceof Vector) {
                        tagger.setValues(key,(Vector)o);
                    } else {
                        tagger.setValue(key,""+o);
                    }
                }
            }
        }
        scanpage sp=null;
        if (context instanceof scanpage) {
            sp = (scanpage)context;
        }
        Vector v=getList(sp,tagger,command);
        int items=1;
        try { items=Integer.parseInt(tagger.Value("ITEMS")); } catch (NumberFormatException e) {}
        Vector fieldlist=tagger.Values("FIELDS");
        Vector res=new Vector(v.size() / items);
        MMObjectBuilder bul= getListBuilder(command,params);
        for(int i= 0; i<v.size(); i+=items) {
            MMObjectNode node = new MMObjectNode(bul);
            for(int j= 0; (j<items) && (j<v.size()); j++) {
                if ((fieldlist!=null) && (j<fieldlist.size())) {
                    node.setValue((String)fieldlist.get(j),v.get(i+j));
                } else {
                    node.setValue("item"+(j+1),v.get(i+j));
                }
            }
            res.add(node);
        }
        return res;
    }

    /**
     * Generate a list of values from a command to the processor
     * @param sp the page context
     * @param params contains the attributes for the list
     * @param command the list command to execute.
     **/
    public Vector  getList(scanpage sp,StringTagger params, String command) throws ParseException {
        throw new ParseException("Module " + this.getClass().getName() + " does not implement LIST");
    }

    /**
     * Execute the commands provided in the form values
     */
    public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
        return(false);
    }

    /**
	 * Replace a command by a string
	 **/
    public String replace (scanpage sp, String command) {
        return("This module doesn't implement this processor call");
    }

    /**
	 * Replace a command by a string
	 * who the hell uses this (daniel)
	 **/
    public String replace (scanpage sp, StringTagger command) {
        return("This module doesn't implement this processor call");
    }

    /**
	 * Do a cache check (304) for this request
	 */
    public boolean cacheCheck(scanpage sp,String cmd) {
        return(false);
    }
	
	
    public void init() {
    }
	
    public void reload() {
    }

    public void onload() {
    }

    public void unload() {
    }
}
