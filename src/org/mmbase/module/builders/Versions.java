/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 * @sql
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class Versions extends MMObjectBuilder implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(Versions.class.getName());

    private Hashtable CacheVersionHandlers=new Hashtable();

    /**
     * @javadoc
     */
    public boolean init() {
        super.init();
        startCacheTypes();
        return true;
    }

    /**
     * @javadoc
     */
    public int getInstalledVersion(String name,String type) {
        String query="name=='"+name+"'+type=='"+type+"'";
        Enumeration b=search(query);
        if (b.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)b.nextElement();
            return node.getIntValue("version");
        }
        return -1;
    }

    /**
     * @javadoc
     */
    public void setInstalledVersion(String name,String type,String maintainer,int version) {
        MMObjectNode node=getNewNode("system");
        node.setValue("name",name);
        node.setValue("type",type);
        node.setValue("maintainer",maintainer);
        node.setValue("version",version);
        insert("system",node);
    }

    /**
     * @javadoc
     * @sql
     */
    public void updateInstalledVersion(String name,String type,String maintainer,int version) {
        String query="name=='"+name+"'+type=='"+type+"'";
        Enumeration b=search(query);
        if (b.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)b.nextElement();
            node.setValue("version",version);
            node.commit();
        }
    }

    /**
     * @javadoc
     */
    public void startCacheTypes() {
        // is there a CacheVersion file ?
        String cacheversionfile=getInitParameter("cacheversionfile");

        if (cacheversionfile!=null && !cacheversionfile.equals("")) {
            VersionXMLCacheNodeReader parser=new VersionXMLCacheNodeReader(cacheversionfile);
            parser.setBuilder(this);
            CacheVersionHandlers=parser.getCacheVersions(CacheVersionHandlers);
            System.out.println("Cache version Handlers="+CacheVersionHandlers);
        }
        for (Enumeration e=CacheVersionHandlers.keys();e.hasMoreElements();) {
                String bname=(String)e.nextElement();
            mmb.addLocalObserver(bname,this);
            mmb.addRemoteObserver(bname,this);
        }
    }

    /**
     * @javadoc
     */
    private boolean nodeChanged(String machine,String number,String builder,String ctype) {
        log.service("Versions -> signal change "+number+" "+builder+" "+ctype);
        Vector subs=(Vector)CacheVersionHandlers.get(builder);
        try {
            int inumber=Integer.parseInt(number);
            if (subs!=null) {
                for (Enumeration e=subs.elements();e.hasMoreElements();) {
                    VersionCacheNode cnode=(VersionCacheNode)e.nextElement();
                    cnode.handleChanged(builder,inumber);
                }
            }
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
//			e.printStackTrace();
        }
        return true;
    }

    /**
     * @javadoc
     */
    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        getNode(number); // to make sure cache is valid
        super.nodeLocalChanged(machine,number,builder,ctype);
        return nodeChanged(machine, number, builder, ctype);
    }

    /**
     * @javadoc
     */
    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        super.nodeRemoteChanged(machine,number,builder,ctype);
        return nodeChanged(machine, number, builder, ctype);
    }
}
