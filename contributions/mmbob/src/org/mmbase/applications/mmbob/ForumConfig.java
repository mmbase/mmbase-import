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
import org.mmbase.util.logging.*;


/**
 * forumManager
 * ToDo: Write docs!
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class ForumConfig {
    private static Logger log = Logging.getLoggerInstance(ForumConfig.class);
    private ArrayList fieldaliases=new ArrayList();
    private HashMap subs=new HashMap();
    private String defaultaccount, defaultpassword;
    private String accountcreationtype,accountremovaltype;
    private String loginmodetype,logoutmodetype;
    private String guestreadmodetype,guestwritemodetype;
    private String id="unkown";
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


    private int quotamax = 100;
    private int quotasoftwarning = 60;
    private int quotawarning = 80;

    public ForumConfig(XMLBasicReader reader,Element n) {
	decodeConfig(reader,n);
    }

    public ForumConfig(String id) {
	this.id = id;
    }

    private boolean decodeConfig(XMLBasicReader reader,Element n) {
                    NamedNodeMap nm = n.getAttributes();
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

                        accountcreationtype = getAttributeValue(reader,n,"accountcreation","type");
                        accountremovaltype = getAttributeValue(reader,n,"accountremoval","type");
                        loginmodetype = getAttributeValue(reader,n,"loginmode","type");
                        logoutmodetype = getAttributeValue(reader,n,"logoutmode","type");
                        guestreadmodetype = getAttributeValue(reader,n,"guestreadmode","type");
                        guestwritemodetype = getAttributeValue(reader,n,"guestwritemode","type");


                        contactInfoEnabled = getAttributeValue(reader,n,"contactinfo","enable");
                        smileysEnabled = getAttributeValue(reader,n,"smileys","enable");
                        privateMessagesEnabled = getAttributeValue(reader,n,"privateMessages","enable");
                        String inttemp = getAttributeValue(reader,n,"postingsperpage","value");
                        if (inttemp != null) {
                            postingsPerPage = (Integer.valueOf(inttemp)).intValue();
                        }

                        fromEmailAddress = getAttributeValue(reader,n,"email","from");

                        for(Enumeration ns2=reader.getChildElements(n,"layout");ns2.hasMoreElements(); ) {
                            Element n2=(Element)ns2.nextElement();
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


                        for(Enumeration ns2=reader.getChildElements(n,"avatars");ns2.hasMoreElements(); ) {
                            Element n2=(Element)ns2.nextElement();
                            org.w3c.dom.NodeList avatarsList = n2.getElementsByTagName("upload");
                            if (avatarsList.getLength() > 0) {
                                Element uploadNode = (Element)avatarsList.item(0);
                                avatarsUploadEnabled = uploadNode.getAttribute("enable");
                            }
                            avatarsList = n2.getElementsByTagName("gallery");
                            if (avatarsList.getLength() > 0) {
                                Element galleryNode = (Element)avatarsList.item(0);
                                avatarsGalleryEnabled =galleryNode.getAttribute("enable");
                            }
                            
                        }


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
							String kid="default."+object+"."+field;
							FieldAlias fa=new FieldAlias(kid);
							fa.setObject(object);
							fa.setExtern(extern);
							fa.setField(field);
							fa.setExternField(externfield);
							fa.setKey(key);
							fa.setExternKey(externkey);
							fieldaliases.add(fa);
						}
					}
                                        for(Enumeration ns2=reader.getChildElements(n,"postarea");ns2.hasMoreElements(); ) {
                                                Element n2=(Element)ns2.nextElement();
                                                PostAreaConfig config = new PostAreaConfig(reader,n2);
                                                subs.put(config.getId(),config);
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

   public void setLoginModeType(String type) {
        loginmodetype = type;
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

   public void setGuestReadModeType(String type) {
        guestreadmodetype = type;
   }

   public void setAvatarsUploadEnabled(String mode) {
        avatarsUploadEnabled = mode;
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

}
