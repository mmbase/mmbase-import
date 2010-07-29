/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;


/**
 * An BuilderReader system event is and should be issued after one or more builders are added to the system.
 */
public class BuildersRead extends SystemEvent.Collectable {
    private final String uri;
    private final String name;
    public BuildersRead() {
        this(org.mmbase.bridge.ContextProvider.getDefaultCloudContext());
    }
    public BuildersRead(org.mmbase.bridge.CloudContext cc){
        this(cc.getUri(), cc.getCloudNames().get(0));
    }
    public BuildersRead(String u, String n) {
        uri  = u;
        name = n;
    }
    public String getUri() {
        return uri;
    }
    public String getName() {
        return name;
    }

}
