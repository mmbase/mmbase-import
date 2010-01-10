/*
 
 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.
 
 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.mmbob;

import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * forumManager ToDo: Write docs!
 * 
 * @author Daniel Ockeloen (MMBased)
 */
public class ForumConfig {
    private static Logger log = Logging.getLoggerInstance(ForumConfig.class);
    private ArrayList fieldaliases = new ArrayList();
    private HashMap subs = new HashMap();
    private HashMap setguieditvalues = new HashMap();
    private String defaultaccount, defaultpassword, alias;
    private String accountcreationtype, accountremovaltype;
    private String loginsystemtype, loginmodetype, logoutmodetype;
    private String guestreadmodetype, guestwritemodetype, threadstartlevel;
    /**
     * This when this is false, first and last name are hidden. only the nick name is shown
     * if there is no nick, the account is shown 
     */
    private String id = "unkown";
    private String xsltpostingsodd = "xslt/posting2xhtmlDark.xslt";
    private String xsltpostingseven = "xslt/posting2xhtmlLight.xslt";

    private String avatarsUploadEnabled = "true";
    private String avatarsGalleryEnabled = "true";

    private String contactInfoEnabled = "true";
    private String smileysEnabled = "true";
    private String privateMessagesEnabled = "true";
    private int postingsPerPage = -1;
    private String fromEmailAddress = "";
    private String htmlHeaderPath = "header.jsp";
    private String htmlFooterPath = "footer.jsp";
    private HashMap profiledefs = new HashMap();
    private String navigationmethod = "list";

    private int speedposttime = 10;
    private int postingsoverflowpostarea = 4;
    private int postingsoverflowthreadpage = 4;
    private boolean clonemaster = false;

    private boolean replyoneachpage = false;

    private int quotamax = 100;
    private int quotasoftwarning = 60;
    private int quotawarning = 80;
    
    /**
     * properties key value sets that can be edited in the web interface of the forum.
     */
    private Map properties = new HashMap(); 

    public ForumConfig(DocumentReader forumConfigReader, Element n) {
        decodeConfig(forumConfigReader, n);
        //now let's check if we have to add a 'nick' field
        log.info("login type: "+getLoginSystemType());
        if(profiledefs.get("nick") == null && 
                (getLoginSystemType().equals("entree") || getLoginSystemType().equals("entree-ng")) ){
            log.debug("no nick profile entry def found for forum " + getId()+". it will be added");
            ProfileEntryDef nickProfileEntryDef = new ProfileEntryDef();
            nickProfileEntryDef.setEdit(false);
            nickProfileEntryDef.setRequired(true);
            //TOD: changeable is not implemented yet
            nickProfileEntryDef.setChangeable(false);
            nickProfileEntryDef.setName("nick");
            nickProfileEntryDef.setGuiName("Nick");
            nickProfileEntryDef.setType("string");
            profiledefs.put("nick", nickProfileEntryDef);
        }
    }

    public ForumConfig(String id) {
        this.id = id;
    }

    /**
     * reads the config for a specific forum from xml.
     * @param reader
     * @param forum
     * @return
     */
    private boolean decodeConfig(DocumentReader reader, Element forum) {
        NamedNodeMap nm = forum.getAttributes();
        if (nm != null) {
            String account = "admin";
            String password = "admin2k";
            // decode name
            org.w3c.dom.Node n3 = nm.getNamedItem("id");
            if (n3 != null) {
                id = n3.getNodeValue();
            }
            // decode account
            n3 = nm.getNamedItem("account");
            if (n3 != null) {
                account = n3.getNodeValue();
            }
            // decode password
            n3 = nm.getNamedItem("password");
            if (n3 != null) {
                password = n3.getNodeValue();
            }
            if (id.equals("default")) {
                defaultaccount = account;
                defaultpassword = password;
            }
            n3 = nm.getNamedItem("alias");
            if (n3 != null) {
                alias = n3.getNodeValue();
            }
            n3 = nm.getNamedItem("clonemaster");
            if (n3 != null) {
                if (n3.getNodeValue().equals("true")) {
                    clonemaster = true;
                }
            }
            
//          decode properties
            Element n2 = reader.getElementByPath(forum, "forum.properties");
            if(n2 != null){
                for(Iterator ns2 = reader.getChildElements(n2, "property"); ns2.hasNext(); ){
                     n2 = (Element)ns2.next();
                     NamedNodeMap attributes = n2.getAttributes();
                    properties.put(attributes.getNamedItem("name").getNodeValue(), attributes.getNamedItem("value").getNodeValue());
                }
            }

            accountcreationtype = getAttributeValue(reader, forum, "accountcreation", "type");
            accountremovaltype = getAttributeValue(reader, forum, "accountremoval", "type");
            loginsystemtype = getAttributeValue(reader, forum, "loginsystem", "type");
            setGuiEdit("loginsystem", getAttributeValue(reader, forum, "loginsystem", "guiedit"));
            loginmodetype = getAttributeValue(reader, forum, "loginmode", "type");
            setGuiEdit("loginmode", getAttributeValue(reader, forum, "loginmode", "guiedit"));
            logoutmodetype = getAttributeValue(reader, forum, "logoutmode", "type");
            setGuiEdit("logoutmode", getAttributeValue(reader, forum, "logoutmode", "guiedit"));
            guestreadmodetype = getAttributeValue(reader, forum, "guestreadmode", "type");
            setGuiEdit("guestreadmode", getAttributeValue(reader, forum, "guestreadmode", "guiedit"));
            guestwritemodetype = getAttributeValue(reader, forum, "guestwritemode", "type");
            setGuiEdit("guestwritemode", getAttributeValue(reader, forum, "guestwritemode", "guiedit"));
            threadstartlevel = getAttributeValue(reader, forum, "threadstart", "level");

            contactInfoEnabled = getAttributeValue(reader, forum, "contactinfo", "enable");
            smileysEnabled = getAttributeValue(reader, forum, "smileys", "enable");
            privateMessagesEnabled = getAttributeValue(reader, forum, "privateMessages", "enable");
            String inttemp = getAttributeValue(reader, forum, "postingsperpage", "value");
            if (inttemp != null) {
                postingsPerPage = (Integer.valueOf(inttemp)).intValue();
            }

            inttemp = getAttributeValue(reader, forum, "postingsoverflowpostarea", "value");
            if (inttemp != null) {
                postingsoverflowpostarea = (Integer.valueOf(inttemp)).intValue();
            }

            inttemp = getAttributeValue(reader, forum, "postingsoverflowthreadpage", "value");
            if (inttemp != null) {
                postingsoverflowthreadpage = (Integer.valueOf(inttemp)).intValue();
            }

            inttemp = getAttributeValue(reader, forum, "speedposttime", "value");
            if (inttemp != null) {
                speedposttime = (Integer.valueOf(inttemp)).intValue();
            }
            
            String stmp = getAttributeValue(reader, forum, "replyoneachpage", "value");
            if (stmp != null) {
                if (stmp.equals("true")) {
                    replyoneachpage = true;
                } else {
                    replyoneachpage = false;
                }
            }

            fromEmailAddress = getAttributeValue(reader, forum, "email", "from");

            String tmp = getAttributeValue(reader, forum, "navigation", "method");
            if (tmp != null) navigationmethod = tmp;
            setGuiEdit("navigationmethod", getAttributeValue(reader, forum, "navigation", "guiedit"));

            for (Iterator ns2 = reader.getChildElements(forum, "layout"); ns2.hasNext();) {
                n2 = (Element) ns2.next();
                org.w3c.dom.NodeList layoutList = n2.getElementsByTagName("footer");
                if (layoutList.getLength() > 0) {
                    Element footerNode = (Element) layoutList.item(0);
                    htmlFooterPath = footerNode.getAttribute("path");
                }
                layoutList = n2.getElementsByTagName("header");
                if (layoutList.getLength() > 0) {
                    Element headerNode = (Element) layoutList.item(0);
                    htmlHeaderPath = headerNode.getAttribute("path");
                }
            }

            for (Iterator ns2 = reader.getChildElements(forum, "avatars"); ns2.hasNext();) {
                n2 = (Element) ns2.next();
                org.w3c.dom.NodeList avatarsList = n2.getElementsByTagName("upload");
                if (avatarsList.getLength() > 0) {
                    Element uploadNode = (Element) avatarsList.item(0);
                    avatarsUploadEnabled = uploadNode.getAttribute("enable");
                    setGuiEdit("avatarsupload", uploadNode.getAttribute("guiedit"));
                }
                avatarsList = n2.getElementsByTagName("gallery");
                if (avatarsList.getLength() > 0) {
                    Element galleryNode = (Element) avatarsList.item(0);
                    avatarsGalleryEnabled = galleryNode.getAttribute("enable");
                    setGuiEdit("avatarsgallery", galleryNode.getAttribute("guiedit"));
                }

            }

            for (Iterator ns2 = reader.getChildElements(forum, "profileentry"); ns2.hasNext();) {
                n2 = (Element) ns2.next();

                nm = n2.getAttributes();
                if (nm != null) {
                    String name = null;
                    String guiname = null;
                    int guipos = -1;
                    int size = -1;
                    String external = null;
                    String externalname = null;
                    String type = null;
                    boolean edit = true;
                    boolean required = false;
                    boolean changeable = true;

                    // decode name
                    n3 = nm.getNamedItem("name");
                    if (n3 != null) {
                        name = n3.getNodeValue();
                    }

                    // decode guiname
                    n3 = nm.getNamedItem("guiname");
                    if (n3 != null) {
                        guiname = n3.getNodeValue();
                    }

                    // decode guipos
                    n3 = nm.getNamedItem("guipos");
                    if (n3 != null) {
                        try {
                            guipos = Integer.parseInt(n3.getNodeValue());
                        } catch (Exception e) {}
                    }

                    // decode size
                    n3 = nm.getNamedItem("size");
                    if (n3 != null) {
                        try {
                            size = Integer.parseInt(n3.getNodeValue());
                        } catch (Exception e) {}
                    }

                    // decode edit
                    n3 = nm.getNamedItem("edit");
                    if (n3 != null) {
                        if (n3.getNodeValue().equals("false")) edit = false;
                    }
                    
                    //decode required
                    n3 = nm.getNamedItem("required");
                    if (n3 != null) {
                        if (n3.getNodeValue().equals("true")) required = true;
                    }
                    
                    //decode changeable
                    n3 = nm.getNamedItem("changeable");
                    if (n3 != null) {
                        if (n3.getNodeValue().equals("false")) changeable = false;
                    }

                    // decode external
                    n3 = nm.getNamedItem("external");
                    if (n3 != null) {
                        external = n3.getNodeValue();
                    }

                    // decode externalname
                    n3 = nm.getNamedItem("externalname");
                    if (n3 != null) {
                        externalname = n3.getNodeValue();
                    }

                    // decode type
                    n3 = nm.getNamedItem("type");
                    if (n3 != null) {
                        type = n3.getNodeValue();
                    }

                    if (name != null) {
                        ProfileEntryDef pe = new ProfileEntryDef();
                        pe.setName(name);
                        pe.setGuiPos(guipos);
                        pe.setSize(size);
                        pe.setEdit(edit);
                        pe.setRequired(required);
                        pe.setChangeable(changeable);
                        if (external != null && !external.equals("")) pe.setExternal(external);
                        if (externalname != null) pe.setExternalName(externalname);
                        if (type != null) pe.setType(type);
                        if (guiname != null) pe.setGuiName(guiname);
                        profiledefs.put(name, pe);
                    }
                }
            }

            for (Iterator ns2 = reader.getChildElements(forum, "generatedata"); ns2.hasNext();) {
                n2 = (Element) ns2.next();
                nm = n2.getAttributes();
                if (nm != null) {
                    String role = null;
                    String dfile = null;
                    String tokenizer = null;
                    n3 = nm.getNamedItem("role");
                    if (n3 != null) {
                        role = n3.getNodeValue();
                    }
                    n3 = nm.getNamedItem("file");
                    if (n3 != null) {
                        dfile = n3.getNodeValue();
                    }
                    n3 = nm.getNamedItem("tokenizer");
                    if (n3 != null) {
                        tokenizer = n3.getNodeValue();
                    }
                    org.mmbase.applications.mmbob.generate.Handler.setGenerateFile(role, dfile, tokenizer);
                }
            }

            for (Iterator ns2 = reader.getChildElements(forum, "quota"); ns2.hasNext();) {
                n2 = (Element) ns2.next();
                nm = n2.getAttributes();
                if (nm != null) {
                    n3 = nm.getNamedItem("max");
                    if (n3 != null) {
                        setQuotaMax(n3.getNodeValue());
                    }
                    n3 = nm.getNamedItem("softwarning");
                    if (n3 != null) {
                        setQuotaSoftWarning(n3.getNodeValue());
                    }
                    n3 = nm.getNamedItem("warning");
                    if (n3 != null) {
                        setQuotaWarning(n3.getNodeValue());
                    }
                }
            }

            for (Iterator ns2 = reader.getChildElements(forum, "alias"); ns2.hasNext();) {
                n2 = (Element) ns2.next();
                nm = n2.getAttributes();
                if (nm != null) {
                    String object = null;
                    String extern = null;
                    String field = null;
                    String externfield = null;
                    String key = null;
                    String externkey = null;
                    n3 = nm.getNamedItem("object");
                    if (n3 != null) {
                        object = n3.getNodeValue();
                    }
                    n3 = nm.getNamedItem("extern");
                    if (n3 != null) {
                        extern = n3.getNodeValue();
                    }
                    n3 = nm.getNamedItem("field");
                    if (n3 != null) {
                        field = n3.getNodeValue();
                    }
                    n3 = nm.getNamedItem("externfield");
                    if (n3 != null) {
                        externfield = n3.getNodeValue();
                    }
                    n3 = nm.getNamedItem("key");
                    if (n3 != null) {
                        key = n3.getNodeValue();
                    }
                    n3 = nm.getNamedItem("externkey");
                    if (n3 != null) {
                        externkey = n3.getNodeValue();
                    }
                    String kid = "default." + object + "." + field;
                    FieldAlias fa = new FieldAlias(kid);
                    fa.setObject(object);
                    fa.setExtern(extern);
                    fa.setField(field);
                    fa.setExternField(externfield);
                    fa.setKey(key);
                    fa.setExternKey(externkey);
                    fieldaliases.add(fa);
                }
            }
            for (Iterator ns2 = reader.getChildElements(forum, "postarea"); ns2.hasNext();) {
                n2 = (Element) ns2.next();
                PostAreaConfig config = new PostAreaConfig(reader, n2);
                subs.put(config.getId(), config);
            }

        }
        return true;
    }

    public Iterator getFieldaliases() {
        return fieldaliases.iterator();
    }

    public String getDefaultPassword() {
        return defaultpassword;
    }

    public String getDefaultAccount() {
        return defaultaccount;
    }
    
    

    /**
     * ToDo: Write docs!
     * @param id
     */
    protected Map getNamePassword(String id) {
        Map user = new HashMap();
        if (id.equals("default")) {
            user.put("username", defaultaccount);
            user.put("password", defaultpassword);
        }
        return user;
    }

    public void setQuotaMax(String maxs) {
        try {
            quotamax = Integer.parseInt(maxs);
        } catch (Exception e) {
            log.error("illegal (non number) value set for quota max");
        }
    }

    public void setQuotaMax(int max) {
        quotamax = max;
    }

    public void setQuotaSoftWarning(String sws) {
        try {
            quotasoftwarning = Integer.parseInt(sws);
        } catch (Exception e) {
            log.error("illegal (non number) value set for quota softwarning");
        }
    }

    public void setQuotaWarning(String ws) {
        try {
            quotawarning = Integer.parseInt(ws);
        } catch (Exception e) {
            log.error("illegal (non number) value set for quota warning");
        }
    }

    public int getQuotaMax() {
        return quotamax;
    }

    public int getQuotaSoftWarning() {
        return quotasoftwarning;
    }

    public int getQuotaWarning() {
        return quotawarning;
    }

    public String getId() {
        return id;
    }

    public PostAreaConfig getPostAreaConfig(String id) {
        Object o = subs.get(id);
        if (o != null) {
            return (PostAreaConfig) o;
        }
        return null;
    }

    private String getAttributeValue(DocumentReader reader, Element n, String itemname, String attribute) {
        for (Iterator ns2 = reader.getChildElements(n, itemname); ns2.hasNext();) {
            Element n2 = (Element) ns2.next();
            NamedNodeMap nm = n2.getAttributes();
            if (nm != null) {
                org.w3c.dom.Node n3 = nm.getNamedItem(attribute);
                if (n3 != null) {
                    return n3.getNodeValue();
                }
            }
        }
        return null;
    }

    public String getAccountCreationType() {
        return accountcreationtype;
    }

    public String getAccountRemovalType() {
        return accountremovaltype;
    }

    public String getLoginModeType() {
        return loginmodetype;
    }

    public String getLoginSystemType() {
        return loginsystemtype;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setLoginModeType(String type) {
        loginmodetype = type;
    }

    public void setLoginSystemType(String system) {
        loginsystemtype = system;
    }

    public String getLogoutModeType() {
        return logoutmodetype;
    }

    public void setLogoutModeType(String type) {
        logoutmodetype = type;
    }

    public String getGuestReadModeType() {
        return guestreadmodetype;
    }

    public String getThreadStartLevel() {
        return threadstartlevel;
    }

    public void setPostingsPerPage(int count) {
        postingsPerPage = count;
    }

    public void setPostingsOverflowPostArea(int count) {
        postingsoverflowpostarea = count;
    }

    public void setPostingsOverflowThreadPage(int count) {
        postingsoverflowthreadpage = count;
    }

    public int getPostingsOverflowThreadPage() {
        return postingsoverflowthreadpage;
    }

    public int getPostingsOverflowPostArea() {
        return postingsoverflowpostarea;
    }

    public void setReplyOnEachPage(boolean value) {
        replyoneachpage = value;
    }

    public boolean getReplyOnEachPage() {
        return replyoneachpage;
    }

    public void setSpeedPostTime(int delay) {
        speedposttime = delay;
    }

    public int getSpeedPostTime() {
        return speedposttime;
    }

    public void setGuestReadModeType(String type) {
        guestreadmodetype = type;
    }

    public void setAvatarsUploadEnabled(String mode) {
        avatarsUploadEnabled = mode;
    }

    public void setNavigationMethod(String navigationmethod) {
        this.navigationmethod = navigationmethod;
    }

    public void setContactInfoEnabled(String mode) {
        contactInfoEnabled = mode;
    }

    public void setAvatarsGalleryEnabled(String mode) {
        avatarsGalleryEnabled = mode;
    }

    public String getGuestWriteModeType() {
        return guestwritemodetype;
    }

    public void setGuestWriteModeType(String type) {
        guestwritemodetype = type;
    }

    public String getXSLTPostingsOdd() {
        return xsltpostingsodd;
    }

    public String getXSLTPostingsEven() {
        return xsltpostingseven;
    }

    public String getAvatarsUploadEnabled() {
        return avatarsUploadEnabled;
    }

    public String getAvatarsGalleryEnabled() {
        return avatarsGalleryEnabled;
    }

    public String getContactInfoEnabled() {
        return contactInfoEnabled;
    }

    public String getSmileysEnabled() {
        return smileysEnabled;
    }

    public String getPrivateMessagesEnabled() {
        return privateMessagesEnabled;
    }

    public int getPostingsPerPage() {
        return postingsPerPage;
    }

    public String getFromEmailAddress() {
        return fromEmailAddress;
    }

    public String getHeaderPath() {
        return htmlHeaderPath;
    }

    public String getFooterPath() {
        return htmlFooterPath;
    }

    public String getNavigationMethod() {
        return navigationmethod;
    }

    public PostAreaConfig addPostAreaConfig(String name) {
        PostAreaConfig config = new PostAreaConfig(name);
        subs.put(config.getId(), config);
        return config;
    }

    public Iterator getProfileDefs() {
        return profiledefs.values().iterator();
    }

    public void addProfileDef(ProfileEntryDef cm) {
        ProfileEntryDef pe = new ProfileEntryDef();
        pe.setName(cm.getName());
        pe.setGuiPos(cm.getGuiPos());
        pe.setSize(cm.getSize());
        pe.setEdit(cm.getEdit());
        if (cm.getExternal() != null && !cm.getExternal().equals("")) pe.setExternal(cm.getExternal());
        if (cm.getExternalName() != null && !cm.getExternalName().equals("")) pe.setExternalName(cm.getExternalName());
        if (cm.getType() != null) pe.setType(cm.getType());
        if (cm.getGuiName() != null) pe.setGuiName(cm.getGuiName());
        profiledefs.put(cm.getName(), pe);
    }

    public ProfileEntryDef getProfileDef(String name) {
        Object o = profiledefs.get(name);
        if (o != null) return (ProfileEntryDef) o;
        return null;
    }

    public boolean getCloneMaster() {
        return clonemaster;
    }

    private void setGuiEdit(String key, String value) {
        if (value == null || value.equals("")) {
            setguieditvalues.put(key, "true");
        } else {
            setguieditvalues.put(key, value);
        }
    }

    public String getGuiEdit(String key) {
        String result = (String) setguieditvalues.get(key);
        if (result == null || result.equals("")) return "true";
        return result;
    }

    /**
     * @param name
     * @return the property value or null if the property is not there
     */
    public String getPropety(String name) {
        return (String) properties.get(name);
    }
    
    public void setProperty(String name, String value){
        properties.put(name, value);
    }
    
    public boolean hasProperties(){
        return properties.size() > 0;
    }
    
    public Iterator getPropertyNames(){
        return properties.keySet().iterator();
    }
    
    
}
