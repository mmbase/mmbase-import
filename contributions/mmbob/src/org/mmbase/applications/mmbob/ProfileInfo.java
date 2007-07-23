/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */

package org.mmbase.applications.mmbob;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import org.w3c.dom.*;
import org.xml.sax.*;


/**
 * this class represents extra profile information attached to a poster.
 * It contains a ProfielEntry instance for every profiel entry line in the xml
 * configuration for the given forum
 * @author Daniel Ockeloen
 * 
 */
public class ProfileInfo {

    // logger
    static private Logger log = Logging.getLoggerInstance(ProfileInfo.class);

    private int id = -1;
    private Poster parent;
    private Forum forum;
    private String xml;
    private String external;
    private int synced;
    
    // map with ProfileEntry objects
    private HashMap entries = new HashMap();

    public static final String DTD_PROFILEINFO_1_0 = "profileinfo_1_0.dtd";
    public static final String PUBLIC_ID_PROFILEINFO_1_0 = "-//MMBase//DTD mmbob profileinfo 1.0//EN";

    /**
     * Register the Public Ids for DTDs used by DatabaseReader This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_PROFILEINFO_1_0, DTD_PROFILEINFO_1_0, ProfileInfo.class);
    }

    public ProfileInfo(Poster parent) {
        this.parent = parent;
        this.forum = parent.getParent();
        // syncExternals();
    }

    /**
     * this method is called for each poster when the forum starts up
     * @param parent the poster this profileinfo object belongs to
     * @param id the id of the corresponding node in the cloud
     * @param xml the profile info from the mmbase node encoded as xml
     * @param external 
     * @param synced ?
     */
    public ProfileInfo(Poster parent, int id, String xml, String external, int synced) {
        this.parent = parent;
        this.id = id;
        this.xml = xml;
        this.external = external;
        this.synced = synced;
        this.parent = parent;
        decodeXML();
        this.forum = parent.getParent();
        createEntriesForDefs();
        // syncExternals();
    }

    public int getId() {
        return id;
    }

    /**
     * stores the external profile fields in the corresponding profileInfo node
     * in the cloud.
     * @return
     */
    boolean save() {
        if (id != -1) {
            org.mmbase.bridge.Node node = ForumManager.getCloud().getNode(id);
            node.setValue("xml", encodeXML());
            node.setIntValue("synced", synced);
            node.commit();
        } else {
            NodeManager man = ForumManager.getCloud().getNodeManager("profileinfo");
            org.mmbase.bridge.Node node = man.createNode();
            node.setValue("xml", encodeXML());
            node.setIntValue("synced", synced);
            node.commit();

            RelationManager rm = ForumManager.getCloud().getRelationManager("posters", "profileinfo", "related");
            if (rm != null) {
                org.mmbase.bridge.Node rel = rm.createRelation(ForumManager.getCloud().getNode(parent.getId()), node);
                rel.commit();
            }
            id = node.getNumber();
        }
        return true;
    }

    /**
     * reads the xml in which the extra profile information is stored in the cloud, and creates
     * a number of ProfileInfo objects for it.
     */
    private void decodeXML() {
        if (xml != null && !xml.equals("")) {
            try {
                DocumentReader reader = new DocumentReader(new InputSource(new StringReader(xml)), ProfileInfo.class);
                if (reader != null) {
                    for (Iterator ns = reader.getChildElements("profileinfo", "entry"); ns.hasNext();) {
                        Element n = (Element) ns.next();
                        NamedNodeMap nm = n.getAttributes();
                        if (nm != null) {
                            String name = null;
                            boolean synced = false;
                            

                            // decode name
                            org.w3c.dom.Node n2 = nm.getNamedItem("name");
                            if (n2 != null) {
                                name = n2.getNodeValue();
                            }
                            
                            // decode synced
                            n2 = nm.getNamedItem("synced");
                            if (n2 != null) {
                                if (n2.getNodeValue().equals("true")) synced = true;
                            }
                            if (name != null) {
                                ProfileEntry profileEntry = new ProfileEntry();
                                profileEntry.setName(name);
                                org.w3c.dom.Node n4 = n.getFirstChild();
                                if (n4 != null) {
                                    profileEntry.setValue(n4.getNodeValue());
                                } else {
                                    profileEntry.setValue("");
                                }
                                
                                profileEntry.setSynced(synced);
                                entries.put(name, profileEntry);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Decode problem with : " + xml);
            }
        }
    }

    /**
     * 
     * @return the ProfileEntry instances this profileInfo contains.
     */
    public Iterator getValues() {
        return entries.values().iterator();
    }

    public ProfileEntry getValue(String name) {
        Object o = entries.get(name);
        if (o != null) return (ProfileEntry) o;
        return null;
    }

    /**
     * change the value of or create a new ProfielEntry. Then synced is set to 'false'.
     * Finally this profileInfo instance is added to the ExternalProfileManager's sync que;
     * @param name
     * @param value
     * @return
     */
    public String setValue(String name, String value) {
        ProfileEntry profileEntry = (ProfileEntry) entries.get(name);
        if (profileEntry == null) {
            profileEntry = new ProfileEntry();
            entries.put(name, profileEntry);
        }
        profileEntry.setName(name);
        String oldvalue = getValue(name).getValue();
        if (oldvalue == null || !oldvalue.equals(value)) {
            profileEntry.setValue(value);
            profileEntry.setSynced(false);
            log.info("setting 'synched' false for profileentry "+profileEntry.getName());
            setSynced(false);
            save();
            ProfileEntryDef profileEntryDef = forum.getProfileDef(name);
            if (profileEntryDef != null) {
                String external = profileEntryDef.getExternal();
//                String externalname = profileEntryDef.getExternalName();

                if (external != null && !external.equals("")) {
                    ExternalProfilesManager.addToSyncQueue(this);
                }
            }
        }
        return null;
    }

    private String encodeXML() {
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        body += "<!DOCTYPE profileinfo PUBLIC \"-//MMBase/DTD mmbob profileinfo 1.0//EN\" \"http://www.mmbase.org/dtd/mmbobprofileinfo_1_0.dtd\">\n";
        body += "<profileinfo>\n";

        Iterator pi = entries.values().iterator();
        while (pi.hasNext()) {
            ProfileEntry pe = (ProfileEntry) pi.next();
            body += "\t<entry name=\"" + pe.getName() + "\"><![CDATA[" + pe.getValue() + "]]></entry>\n";
        }
        body += "</profileinfo>\n";
        return body;
    }
    
    /**
     * this method creates profile entry instances for each profiledef in the current forum
     * if there is no profile entry for it yet. If the profile entrydef has an 'external' value,
     * this profileinfo will be set to 'unsyched' and the relevant profile entry as well.
     */
    public void createEntriesForDefs() {
        log.info("checking for missing profile entries");
        boolean added = false;
        if (forum.getProfileDefs() != null) {
            for (Iterator i = forum.getProfileDefs(); i.hasNext();) {
                ProfileEntryDef profileDef = (ProfileEntryDef) i.next();
                String name = profileDef.getName();
                if (entries.get(name) == null) {
                    log.info("adding empty profile entry for (externally synched) entry def " + name);
                    // do the buisiness
                    
                    added = true;
                    ProfileEntry profileEntry = new ProfileEntry();
                    entries.put(name, profileEntry);
                    profileEntry.setName(name);
                    profileEntry.setValue("");
                    if(profileDef.getExternal() != null && "".equals(profileDef.getExternal())){
                        profileEntry.setSynced(false);
                        setSynced(false);
                    }
                }
            }
        }
        if (added) {
            save();
        }
    }
    
    

    /**
     * This private method is never used!
     */
    private void syncExternals() {
        Iterator pdi = forum.getProfileDefs();
        if (pdi != null) {
            while (pdi.hasNext()) {
                ProfileEntryDef pd = (ProfileEntryDef) pdi.next();
                String external = pd.getExternal();
                String externalname = pd.getExternalName();
                if (external != null && !external.equals("")) {
                    ExternalProfileInterface ci = ExternalProfilesManager.getHandler(external);
                    if (ci != null) {
                        String name = pd.getName();
                        String account = parent.getAccount();
                        if (externalname != null && !externalname.equals("")) {
                            String rvalue = ci.getValue(account, externalname);
                            if (rvalue != null) setValue(name, rvalue);
                        } else {
                            String rvalue = ci.getValue(account, name);
                            if (rvalue != null) setValue(name, rvalue);
                        }
                    }
                }
            }
        }
    }

    /**
     * returns the ProfileEntryDef from the parent Forum with the given name.
     * @param name
     * @return
     */
    public ProfileEntryDef getProfileDef(String name) {
        return forum.getProfileDef(name);
    }

    /**
     * return the account of the poster this object belongs to
     * @return
     */
    public String getAccount() {
        return parent.getAccount();
    }

    public void loginTrigger() {
        ExternalProfilesManager.addToCheckQueue(this);
    }

    public void setSynced(boolean value) {
        if (value) {
            synced = 1;
        } else {
            synced = -1;
        }
    }
    
    public String toString(){
        return entries.toString();
    }

}
