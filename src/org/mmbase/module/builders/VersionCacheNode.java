/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 * @version $Id: VersionCacheNode.java,v 1.3 2003-03-10 11:50:21 pierre Exp $
 */
public class VersionCacheNode extends Object {

    	private static Logger log = Logging.getLoggerInstance(VersionCacheNode.class.getName());
	private MMObjectNode versionnode;
	private Vector whens=new Vector();
	private MMBase mmb;
	
	public VersionCacheNode(MMBase mmb) {
		this.mmb=mmb;
	}

	public void handleChanged(String buildername,int number) {
		// method checks if this really something valid
		// and we should signal a new version
		
		boolean dirty=false;
        	for (Enumeration e=whens.elements();e.hasMoreElements();) {
		      	VersionCacheWhenNode whennode=(VersionCacheWhenNode)e.nextElement();
			Vector types=whennode.getTypes();

			// check if im known in the types part 
			if (types.contains(buildername)) {
				// is there only 1 builder type ?
				System.out.println("types="+types.toString());
				if (types.size()==1) {
					dirty=true;
				} else {
					// so multiple prepare a multilevel !
					Vector nodes=whennode.getNodes();
					//System.out.println("nodes="+nodes.toString());

					Vector fields=new Vector();
					fields.addElement(buildername+".number");
					Vector ordervec=new Vector();
					Vector dirvec=new Vector();

					MultiRelations multirelations=(MultiRelations)mmb.getMMObject("multirelations");		

					Vector vec=multirelations.searchMultiLevelVector(nodes,fields,"YES",types,buildername+".number=="+number,ordervec,dirvec);
					System.out.println("VEC="+vec);
					if (vec!=null && vec.size()>0) {	
						dirty=true;
					}
				}
			}
		}

		if (dirty) {
			// add one to the version of this counter
			int version=versionnode.getIntValue("version");
			versionnode.setValue("version",version+1);
			versionnode.commit();	
			System.out.println("Yeah Im Changed = "+(version+1));
		}
	}

	public void setVersionNode(MMObjectNode versionnode) {
		this.versionnode=versionnode;
	}

	public void addWhen(VersionCacheWhenNode when) {
		whens.addElement(when);
	}

}
