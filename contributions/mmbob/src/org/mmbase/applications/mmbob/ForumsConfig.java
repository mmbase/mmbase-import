/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.mmbob;

import org.w3c.dom.*;

import java.io.*;
import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.*;
import org.mmbase.storage.search.*;


/**
 * forumManager
 * ToDo: Write docs!
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class ForumsConfig {
   private static Logger log = Logging.getLoggerInstance(ForumsConfig.class);
   private HashMap fieldaliases=new HashMap();
    private HashMap subs ;//=new HashMap();
   private String defaultaccount, defaultpassword;
   private String accountcreationtype="open";
   private String accountremovaltype="open";
   private String loginmodetype="open";
   private String logoutmodetype="open";
   private String guestreadmodetype="open";
   private String guestwritemodetype="open";

    private String avatarsUploadEnabled = "true";
    private String avatarsGalleryEnabled = "true";

    private String xsltpostingsodd = "xslt/posting2xhtmlDark.xslt";
    private String xsltpostingseven = "xslt/posting2xhtmlLight.xslt";

    private String contactInfoEnabled = "true";
    private String smileysEnabled = "true";
    private String privateMessagesEnabled = "true";
    private int postingsPerPage = 10;

   private int quotamax = 100;
   private int quotasoftwarning = 60;
   private int quotawarning = 80;

    public ForumsConfig (XMLBasicReader reader,Element n) {
        subs = null;
        subs = new HashMap();
        log.debug("subhasmap cleared");
	decodeConfig(reader,n);
    }

    private boolean decodeConfig(XMLBasicReader reader,Element n) {
                    NamedNodeMap nm = n.getAttributes();
                    if (nm != null) {
                        String id = "default";
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

                        //get xslt configuration 
                        Element xsltElement = reader.getElementByPath("mmbobconfig.forums.xslts.postings");
			if (xsltElement!=null) {
                         	xsltpostingsodd = xsltElement.getAttribute("odd");
                         	xsltpostingseven = xsltElement.getAttribute("even");
			}

                        //get avatar configuration 
                        Element avatarsElement = reader.getElementByPath("mmbobconfig.forums.avatars");
                        Element avatarsUploadElement = reader.getElementByPath(avatarsElement,"avatars.upload");
                        avatarsUploadEnabled = avatarsUploadElement.getAttribute("enable");
                        Element avatarsGalleryElement = reader.getElementByPath(avatarsElement,"avatars.gallery");
                        avatarsGalleryEnabled = avatarsGalleryElement.getAttribute("enable");

                        for (Enumeration ns2 = reader.getChildElements(n, "generatedata"); ns2.hasMoreElements();) {
                            Element n2 = (Element) ns2.nextElement();
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

                        for (Enumeration ns2 = reader.getChildElements(n, "quota"); ns2.hasMoreElements();) {
                            Element n2 = (Element) ns2.nextElement();
                            nm = n2.getAttributes();
                            if (nm != null) {
                                String max = null;
                                String softwarning = null;
                                String warning = null;
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



			
                        accountcreationtype = getAttributeValue(reader,n,"accountcreation","type");
                        accountremovaltype = getAttributeValue(reader,n,"accountremoval","type");
                        loginmodetype = getAttributeValue(reader,n,"loginmode","type");
                        logoutmodetype = getAttributeValue(reader,n,"logoutmode","type");
                        guestreadmodetype = getAttributeValue(reader,n,"guestreadmode","type");
                        guestwritemodetype = getAttributeValue(reader,n,"guestwritemode","type");

                        contactInfoEnabled = getAttributeValue(reader,n,"contactinfo","enable");
                        smileysEnabled = getAttributeValue(reader,n,"smileys","enable");
                        privateMessagesEnabled = getAttributeValue(reader,n,"privatemessages","enable");
                        String inttemp = getAttributeValue(reader,n,"postingsperpage","value");
                        if (inttemp != null) {
                            postingsPerPage = (Integer.valueOf(inttemp)).intValue();
                        }

                        for(Enumeration ns2=reader.getChildElements(n,"alias");ns2.hasMoreElements(); ) {
                                   	Element n2=(Element)ns2.nextElement();
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
                                                		object=n3.getNodeValue();                                        		}
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
							id="default."+object+"."+field;
							FieldAlias fa=new FieldAlias(id);
							fa.setObject(object);
							fa.setExtern(extern);
							fa.setField(field);
							fa.setExternField(externfield);
							fa.setKey(key);
							fa.setExternKey(externkey);
							fieldaliases.put(id,fa);
						}
					}

		                        for(Enumeration ns2=reader.getChildElements(n,"forum");ns2.hasMoreElements(); ) {
                                   		Element n2=(Element)ns2.nextElement();
						ForumConfig config = new ForumConfig(reader,n2);
						subs.put(config.getId(),config);
					}

                    }
		return true;
	}

	public HashMap getFieldaliases() {
		return fieldaliases;
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
     * @return
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

   public ForumConfig getForumConfig(String id) {
	Object o = subs.get(id);
	if (o != null) {
		return (ForumConfig)o;	
	}
	return null;
   }

   private String getAttributeValue(XMLBasicReader reader,Element n,String itemname,String attribute) {
       for (Enumeration ns2 = reader.getChildElements(n, itemname); ns2.hasMoreElements();) {
           Element n2 = (Element) ns2.nextElement();
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

   public String getLogoutModeType() {
        return logoutmodetype;
   }

   public String getGuestReadModeType() {
        return guestreadmodetype;
   }

   public String getGuestWriteModeType() {
        return guestwritemodetype;
   }

    public String getAvatarsUploadEnabled() {
        return avatarsUploadEnabled;
    }

    public String getAvatarsGalleryEnabled() {
        return avatarsGalleryEnabled;
    }

    public String getXSLTPostingsOdd() {
        return xsltpostingsodd;
    }

    public String getXSLTPostingsEven() {
        return xsltpostingseven;
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


}
