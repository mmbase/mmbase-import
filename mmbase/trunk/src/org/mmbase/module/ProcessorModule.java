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
 * The Processor Module extends the basic module to the Processor
 * interface so it can perform for servscan (pagelets).
 *
 * @author Daniel Ockeloen
 * @todo   Should be abstract, deprecated?
 */
public class ProcessorModule extends Module implements ProcessorInterface {

    /**
     * {@inheritDoc}
     **/
    public MMObjectBuilder getListBuilder(String command, Map params) {
        return new VirtualBuilder(null);
    }

    /**
     * {@inheritDoc}
     **/
    public Vector getNodeList(Object context, String command, Map params) {
        StringTagger tagger=null;
        if (params instanceof StringTagger) {
            tagger= (StringTagger)params;
        } else {
            tagger= new StringTagger("");
            if (params!=null) {
                for (Iterator entries=params.entrySet().iterator(); entries.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) entries.next(); 
                    String key=(String) entry.getKey();
                    Object o = entry.getValue();
                    if (o instanceof Vector) {
                        tagger.setValues(key,(Vector)o);
                    } else {
                        tagger.setValue(key,""+o);
                    }
                }
            }
        }
        PageInfo sp = null;
        if (context instanceof PageInfo) {
            sp = (PageInfo)context;
        }
        Vector v = getList(sp,tagger,command);
        int items=1;
        try { items = Integer.parseInt(tagger.Value("ITEMS")); } catch (NumberFormatException e) {}
        Vector fieldlist = tagger.Values("FIELDS");
        Vector res = new Vector(v.size() / items);
        MMObjectBuilder bul = getListBuilder(command,params);
        for(int i= 0; i < v.size(); i+=items) {
            VirtualNode node = new VirtualNode(bul);
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
     * {@inheritDoc}
     **/
    public Vector  getList(PageInfo sp,StringTagger params, String command) {
        throw new UnsupportedOperationException("Module " + this.getClass().getName() + " does not implement LIST");
    }

    /**
     * {@inheritDoc}
     */
    public boolean process(PageInfo sp, Hashtable cmds, Hashtable vars) {
        return false;
    }

    /**
     * {@inheritDoc}
     **/
    public String replace (PageInfo sp, String command) {
        return "This module doesn't implement this processor call";
    }

    /**
     * {@inheritDoc}
     * who the hell uses this (daniel)
     **/
    public String replace (PageInfo sp, StringTagger command) {
        return "This module doesn't implement this processor call";
    }

    /**
     * {@inheritDoc}
     */
    public boolean cacheCheck(PageInfo sp, String cmd) {
        return false;
    }


    /**
     * What should this do, when is this called?
     * @deprecated called by nothing
     * @javadoc
     */

     public void reload() {
     }


    /**
     * What should this do, when is this called?
     * @deprecated called by nothing
     * @javadoc
     */
    public void unload() {
    }

    /**
     * {@inheritDoc}
     * @scope abstract
     */
    public void init() {
    }

    /**
     * {@inheritDoc}
     * @scope abstract
     */
    public void onload() {
    }


}
