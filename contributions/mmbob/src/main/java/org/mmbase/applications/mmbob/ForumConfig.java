/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.mmbob;

import org.w3c.dom.*;
import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.xml.*;
import org.mmbase.util.logging.*;


/**
 * forumManager
 * ToDo: Write docs!
 * MM: I think it would be a bit more OO of the respective object are responsible for their own
 * configuration-handling (even if the all use mmbob.xml). I did that for the sync times alreqdy.
 *
 * @author Daniel Ockeloen (MMBased)
 * @version $Id$
 */
public class ForumConfig {
    private static final Logger log = Logging.getLoggerInstance(ForumConfig.class);
    private List<FieldAlias> fieldAliases = new ArrayList<FieldAlias>();
    private Map<String, PostAreaConfig> subs = new HashMap<String, PostAreaConfig>();
    private Map<String, String> setguieditvalues = new HashMap<String, String>();
    private String defaultaccount, defaultpassword,alias;
    private String accountcreationtype, accountremovaltype;
    private String loginsystemtype, loginmodetype, logoutmodetype;
    private String guestreadmodetype, guestwritemodetype, threadstartlevel;
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
    private Map<String, ProfileEntryDef> profiledefs = new HashMap<String, ProfileEntryDef>();
    private String navigationmethod = "list";

    private int speedposttime = 10;
    private int postingsoverflowpostarea = 4;
    private int postingsoverflowthreadpage = 4;
    private boolean clonemaster =  false;

    private boolean replyoneachpage = false;

    private int quotamax = 100;
    private int quotasoftwarning = 60;
    private int quotawarning = 80;

    private final Element element;
    private final DocumentReader reader;

    public ForumConfig(DocumentReader reader, Element n) {
        element = n;
        this.reader = reader;
	decodeConfig();
    }

    public ForumConfig(String id) {
        element = null;
        reader = null;
	this.id = id;
    }
    public Element getElement() {
        return element;
    }

    public DocumentReader getReader() {
        return reader;
    }

    private boolean decodeConfig() {
        NamedNodeMap nm = element.getAttributes();
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

            accountcreationtype = getAttributeValue(reader, element, "accountcreation","type");
            accountremovaltype = getAttributeValue(reader,element, "accountremoval","type");
            loginsystemtype = getAttributeValue(reader, element, "loginsystem","type");
            setGuiEdit("loginsystem",getAttributeValue(reader, element, "loginsystem","guiedit"));
            loginmodetype = getAttributeValue(reader, element, "loginmode","type");
            setGuiEdit("loginmode",getAttributeValue(reader, element, "loginmode","guiedit"));
            logoutmodetype = getAttributeValue(reader, element, "logoutmode","type");
            setGuiEdit("logoutmode",getAttributeValue(reader, element, "logoutmode","guiedit"));
            guestreadmodetype = getAttributeValue(reader, element, "guestreadmode","type");
            setGuiEdit("guestreadmode",getAttributeValue(reader, element, "guestreadmode","guiedit"));
            guestwritemodetype = getAttributeValue(reader, element, "guestwritemode","type");
            setGuiEdit("guestwritemode",getAttributeValue(reader, element, "guestwritemode","guiedit"));
            threadstartlevel = getAttributeValue(reader, element, "threadstart","level");


            contactInfoEnabled = getAttributeValue(reader, element, "contactinfo","enable");
            smileysEnabled = getAttributeValue(reader, element, "smileys","enable");
            privateMessagesEnabled = getAttributeValue(reader, element, "privateMessages","enable");
            String inttemp = getAttributeValue(reader, element, "postingsperpage","value");
            if (inttemp != null) {
                postingsPerPage = (Integer.valueOf(inttemp)).intValue();
            }

            inttemp = getAttributeValue(reader, element, "postingsoverflowpostarea","value");
            if (inttemp != null) {
                postingsoverflowpostarea = (Integer.valueOf(inttemp)).intValue();
            }

            inttemp = getAttributeValue(reader, element, "postingsoverflowthreadpage","value");
            if (inttemp != null) {
                postingsoverflowthreadpage = (Integer.valueOf(inttemp)).intValue();
            }

            inttemp = getAttributeValue(reader, element, "speedposttime","value");
            if (inttemp != null) {
                speedposttime = (Integer.valueOf(inttemp)).intValue();
            }

            String stmp = getAttributeValue(reader, element, "replyoneachpage","value");
            if (stmp!=null) {
                if (stmp.equals("true")) {
                    replyoneachpage = true;
                } else {
                    replyoneachpage = false;
                }
            }

            fromEmailAddress = getAttributeValue(reader, element, "email","from");

            String tmp = getAttributeValue(reader, element, "navigation","method");
            if (tmp!=null) navigationmethod = tmp;
            setGuiEdit("navigationmethod",getAttributeValue(reader, element, "navigation","guiedit"));

            for(Element n2 : ForumsConfig.list(reader.getChildElements(element, "layout"))) {
                org.w3c.dom.NodeList layoutList = n2.getElementsByTagName("footer");
                if (layoutList.getLength() > 0) {
                    Element footerNode = (Element)layoutList.item(0);
                    htmlFooterPath = footerNode.getAttribute("path");
                }
                layoutList = n2.getElementsByTagName("header");
                if (layoutList.getLength() > 0) {
                    Element headerNode = (Element)layoutList.item(0);
                    htmlHeaderPath = headerNode.getAttribute("path");
                }
            }


            for(Element n2 : ForumsConfig.list(reader.getChildElements(element, "avatars"))) {
                org.w3c.dom.NodeList avatarsList = n2.getElementsByTagName("upload");
                if (avatarsList.getLength() > 0) {
                    Element uploadNode = (Element)avatarsList.item(0);
                    avatarsUploadEnabled = uploadNode.getAttribute("enable");
                    setGuiEdit("avatarsupload",uploadNode.getAttribute("guiedit"));
                }
                avatarsList = n2.getElementsByTagName("gallery");
                if (avatarsList.getLength() > 0) {
                    Element galleryNode = (Element)avatarsList.item(0);
                    avatarsGalleryEnabled =galleryNode.getAttribute("enable");
                    setGuiEdit("avatarsgallery",galleryNode.getAttribute("guiedit"));
                }
                            
            }


            for(Element n2 : ForumsConfig.list(reader.getChildElements(element, "profileentry"))) {
                nm = n2.getAttributes();
                if (nm != null) {
                    String name = null;
                    String guiname = null;
                    int guipos = -1;
                    int size = -1;
                    String external = null;
                    String externalname = null;
                    String type = null;
                    boolean edit = false;

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
                        } catch (Exception e) { }
                    }


                    // decode size
                    n3 = nm.getNamedItem("size");
                    if (n3 != null) {
                        try {
                            size = Integer.parseInt(n3.getNodeValue());
                        } catch (Exception e) { }
                    }

                    // decode edit
                    n3 = nm.getNamedItem("edit");
                    if (n3 != null) {
                        if (n3.getNodeValue().equals("true")) edit = true;
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

                    if (name!=null) {
                        ProfileEntryDef pe = new ProfileEntryDef();
                        pe.setName(name);
                        pe.setGuiPos(guipos);
                        pe.setSize(size);
                        pe.setEdit(edit);
                        if (external!=null && !external.equals("")) pe.setExternal(external);
                        if (externalname!=null) pe.setExternalName(externalname);
                        if (type!=null) pe.setType(type);
                        if (guiname!=null) pe.setGuiName(guiname);
                        profiledefs.put(name, pe);
                    }
                }
            }

            for (Element n2 : ForumsConfig.list(reader.getChildElements(element, "generatedata"))) {
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

            for (Element n2 : ForumsConfig.list(reader.getChildElements(element, "quota"))) {
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

            for(Element n2 : ForumsConfig.list(reader.getChildElements(element,"alias"))) {
                nm=n2.getAttributes();
                if (nm!=null) {
                    String object=null;
                    String extern=null;
                    String field=null;
                    String externfield=null;
                    String key=null;
                    String externkey=null;
                    n3=nm.getNamedItem("object");
                    if (n3!=null) {
                        object=n3.getNodeValue();
                    }
                    n3=nm.getNamedItem("extern");
                    if (n3!=null) {
                        extern=n3.getNodeValue();
                    }
                    n3=nm.getNamedItem("field");
                    if (n3!=null) {
                        field=n3.getNodeValue();
                    }
                    n3=nm.getNamedItem("externfield");
                    if (n3!=null) {
                        externfield=n3.getNodeValue();
                    }
                    n3=nm.getNamedItem("key");
                    if (n3!=null) {
                        key=n3.getNodeValue();
                    }
                    n3=nm.getNamedItem("externkey");
                    if (n3!=null) {
                        externkey=n3.getNodeValue();
                    }
                    String kid = "default." + object + "." + field;
                    FieldAlias fa = new FieldAlias(kid);
                    fa.setObject(object);
                    fa.setExtern(extern);
                    fa.setField(field);
                    fa.setExternField(externfield);
                    fa.setKey(key);
                    fa.setExternKey(externkey);
                    fieldAliases.add(fa);
                }
            }

            for(Element n2 : ForumsConfig.list(reader.getChildElements(element, "postarea"))) {
                PostAreaConfig config = new PostAreaConfig(reader,n2);
                subs.put(config.getId(), config);
            }

        }
        return true;
    }

    public Iterator<FieldAlias> getFieldaliases() {
        return fieldAliases.iterator();
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
            quotamax=Integer.parseInt(maxs);
	} catch (Exception e) {
            log.error("illegal (non number) value set for quota max");
	}
    }


    public void setQuotaMax(int max) {
	quotamax=max;
    }

    public void setQuotaSoftWarning(String sws) {
	try {
            quotasoftwarning=Integer.parseInt(sws);
	} catch (Exception e) {
            log.error("illegal (non number) value set for quota softwarning");
	}
    }


    public void setQuotaWarning(String ws) {
	try {
            quotawarning=Integer.parseInt(ws);
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
            return (PostAreaConfig)o;
        }
        return null;
    }


    private String getAttributeValue(DocumentReader reader, Element n, String itemname, String attribute) {
        for (Element n2 : ForumsConfig.list(reader.getChildElements(n, itemname))) {
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
        postingsPerPage =  count;
    }


    public void setPostingsOverflowPostArea(int count) {
        postingsoverflowpostarea =  count;
    }

    public void setPostingsOverflowThreadPage(int count) {
        postingsoverflowthreadpage =  count;
    }

    public int getPostingsOverflowThreadPage() {
        return postingsoverflowthreadpage;
    }

    public int getPostingsOverflowPostArea() {
        return postingsoverflowpostarea;
    }

    public void setReplyOnEachPage(boolean value) {
        replyoneachpage =  value;
    }

    public boolean getReplyOnEachPage() {
        return replyoneachpage;
    }


    public void setSpeedPostTime(int delay) {
        speedposttime =  delay;
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

    public Iterator<ProfileEntryDef> getProfileDefs() {
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
	return profiledefs.get(name);
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
	String result = setguieditvalues.get(key);
	if (result == null || result.equals("")) return "true";
	return result;
    }


}
