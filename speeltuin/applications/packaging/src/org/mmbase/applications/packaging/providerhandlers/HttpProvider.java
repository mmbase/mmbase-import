/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.providerhandlers;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * HttpProvider, Handler for Http Providers. gets packages and bundles from
 * the provider and feeds them to the package and bundle managers.
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class HttpProvider extends BasicProvider implements ProviderInterface,Runnable {
    private static Logger log = Logging.getLoggerInstance(HttpProvider.class.getName());
    
    private String name;
    private String method;
    private String maintainer;
    private String account="guest";
    private String password="guest";
    private Thread kicker;


    /** DTD resource filename of the sharedpackages DTD version 1.0 */
    public static final String DTD_SHAREDPACKAGES_1_0 = "sharedpackages_1_0.dtd";

    /** Public ID of the sharedpackages DTD version 1.0 */
    public static final String PUBLIC_ID_SHAREDPACKAGES_1_0 = "-//MMBase//DTD sharedpackages config 1.0//EN";

    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_SHAREDPACKAGES_1_0, DTD_SHAREDPACKAGES_1_0, HttpProvider.class);
    }



    public HttpProvider() {
    }

    public void init(org.w3c.dom.Node n,String name,String method,String maintainer) {
       super.init(n,name,method,maintainer);
       org.w3c.dom.Node n2=xmlnode.getFirstChild();
       while (n2!=null) {
         if (n2.getNodeName().equals("path")) {
              org.w3c.dom.Node n3=n2.getFirstChild();
              path=n3.getNodeValue();
         }
         if (n2.getNodeName().equals("description")) {
              org.w3c.dom.Node n3=n2.getFirstChild();
              if (n3!=null) description=n3.getNodeValue();
         }
         if (n2.getNodeName().equals("account")) {
              org.w3c.dom.Node n3=n2.getFirstChild();
              account=n3.getNodeValue();
         }
         if (n2.getNodeName().equals("password")) {
              org.w3c.dom.Node n3=n2.getFirstChild();
              password=n3.getNodeValue();
         }
         n2=n2.getNextSibling();
       }
	baseScore=2000;
	start();
    }

    public void init(String name,String method,String maintainer,String path) {
       super.init(name,method,maintainer,path);
       // this.account=account;
       // this.password=password;
       baseScore=2000;
       start();
    }


    /**
     * Starts the main Thread.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"http provider thread");
            kicker.start();
        }
    }

    public void stop() {
	kicker=null;
    }

    public void setAccount(String account) {
	this.account=account;
    }

    public String getAccount() {
	return account;
    }

    public void setPassword(String password) {
	this.password=password;
    }

    public String getPassword() {
	return password;
    }

    public void getPackages() {
       signalUpdate();

	String url=path+"?account="+account+"&password="+password;
	if (ShareManager.getCallbackUrl()!=null) url+="&callbackurl="+URLParamEscape.escapeurl(ShareManager.getCallbackUrl());
	try {
        	URL includeURL = new URL(url);
        	HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection();
		BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
            	XMLBasicReader reader = new XMLBasicReader(new InputSource(input),HttpProvider.class);
            	if(reader!=null) {
            		for(Enumeration ns=reader.getChildElements("sharedpackages","package");ns.hasMoreElements(); ) {
       				Element e=(Element)ns.nextElement();


   		                NamedNodeMap nm=e.getAttributes();
                    		if (nm!=null) {
					String name=null;
					String type=null;
					String version=null;
					String date=null;

					// decode name
                        		org.w3c.dom.Node n2=nm.getNamedItem("name");
                        		if (n2!=null) {
						name=n2.getNodeValue();
					}

					// decode the type
                        		n2=nm.getNamedItem("type");
                        		if (n2!=null) {
						type=n2.getNodeValue();
					}

					// decode the maintainer 
                        		n2=nm.getNamedItem("maintainer");
                        		if (n2!=null) {
						maintainer=n2.getNodeValue();
					}

					// decode the version
                        		n2=nm.getNamedItem("version");
                        		if (n2!=null) {
						version=n2.getNodeValue();
					}

					// decode the creation date
                        		n2=nm.getNamedItem("creation-date");
                        		if (n2!=null) {
						date=n2.getNodeValue();
					}

            				Element e2=reader.getElementByPath(e,"package.path");
					org.w3c.dom.Node pathnode=e2.getFirstChild();
					String pkgpath=pathnode.getNodeValue();
					if (type.indexOf("bundle/")==0) {
						BundleInterface bun=BundleManager.foundBundle(this,e,name,type,maintainer,version,date,pkgpath);
	                                        // check for included packages in the bundle
       	                                 	findIncludedPackages(bun,e,pkgpath,date);
					} else {
						PackageManager.foundPackage(this,e,name,type,maintainer,version,date,pkgpath);
					}
				}
			}
		}
		setState("up");
	} catch (Exception e) {
		// ignoring errors since well that servers are down is
		// not a error in this concept.
		//log.error("can't get sharedpackagefile : "+url);
		//e.printStackTrace();
		setState("down");
	}
    }


    public JarFile getIncludedPackageJarFile(String path,String id,String version,String packageid,String packageversion) {
	// well first the whole bundle
	getJarFile(path,id,version);

	// it should now be in our import dir for us to get the package from
	try {
		String localname=getImportPath()+id+"_"+version+".mmb";
       		JarFile jarFile = new JarFile(localname);
       		JarEntry je = jarFile.getJarEntry(packageid+"_"+packageversion+".mmp");
		try {
			InputStream in=jarFile.getInputStream(je);	
			BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(getImportPath()+".temp_"+packageid+"_"+packageversion+".mmp"));
			int val;
                   		while ((val = in.read()) != -1) {
                     			out.write(val);
			}
			out.close();
		} catch(Exception e) {
			log.error("can't load : "+path);
			e.printStackTrace();
		}
       		JarFile tmpjarfile = new JarFile(getImportPath()+".temp_"+packageid+"_"+packageversion+".mmp");
		return tmpjarfile;
		} catch(Exception e) {
			log.error("can't load : "+path);
			e.printStackTrace();
		}
	return null;
    }

    public JarFile getJarFile(String path,String id,String version) {
		// since this needs to load a package from a remote site, it uses a url connection
		// but since we don't want to reload it each time we create a copy in our
		// import dir, this means if something fails on a install the next install
		// will use the local copy instead of the remote copy keeping network
		// traffic down.
		try {
			log.info("WOO="+path);
            		URL includeURL = new URL(path);
         	   	HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection();
			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			int buffersize = 10240;
			byte[] buffer = new byte[buffersize];

			// create a new name in the import dir
			String localname=getImportPath()+id+"_"+version+".mmp";
			log.info("WOO1="+id+" localname="+localname);
			// not a very nice way should we have sepr. extentions ?
			if (id.indexOf("_bundle_")!=-1) localname=getImportPath()+id+"_"+version+".mmb";
			log.info("WOO2="+id+" localname="+localname);

			BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(localname));
			StringBuffer string = new StringBuffer();
			int len;
			while ((len = in.read(buffer, 0, buffersize)) != -1) {
       		              	out.write(buffer,0,len);
			}
			out.close();

       			JarFile jarFile = new JarFile(localname);
			return jarFile;
		} catch(Exception e) {
			log.error("can't load : "+path);
			e.printStackTrace();
		}
		return null;
    }


    /**
     * Main loop, exception protected
     */
    public void run () {
        kicker.setPriority(Thread.MIN_PRIORITY+1);  
        while (kicker!=null) {
            try {
                doWork();
            } catch(Exception e) {
                log.error("run(): ERROR: Exception in http provider thread!");
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /**
     * Main work loop
     */
    public void doWork() {
        kicker.setPriority(Thread.MIN_PRIORITY+1);  

        try {Thread.sleep(2*1000);} catch (InterruptedException e){}
        while (kicker!=null) {
	    getPackages();
	    PackageManager.removeOfflinePackages(this);
            try {Thread.sleep(10*1000);} catch (InterruptedException e){}
        }
    }

    public boolean close() {
        stop();
        return super.close();
    }


    private void findIncludedPackages(BundleInterface bun,org.w3c.dom.Node n,String realpath,String date) {

	org.w3c.dom.Node n2=n.getFirstChild();
        while (n2!=null) {
		String name=n2.getNodeName();
		// this should me one way defined (remote or local)
		if (name.equals("includedpackages")) {
			org.w3c.dom.Node n3=n2.getFirstChild();
               	 	while (n3!=null) {
				name=n3.getNodeName();
   		               	NamedNodeMap nm=n3.getAttributes();
                    		if (nm!=null) {
					String maintainer=null;
					String type=null;
					String version=null;
					boolean packed=false;
				
					// decode name
                        		org.w3c.dom.Node n5=nm.getNamedItem("name");
                        		if (n5!=null) {
						name=n5.getNodeValue();
					}

					// decode the type
                        		n5=nm.getNamedItem("type");
                        		if (n5!=null) {
						type=n5.getNodeValue();
					}

					// decode the maintainer 
                        		n5=nm.getNamedItem("maintainer");
                        		if (n5!=null) {
						maintainer=n5.getNodeValue();
					}

					// decode the version
                        		n5=nm.getNamedItem("version");
                        		if (n5!=null) {
						version=n5.getNodeValue();
					}

					// decode the included
                        		n5=nm.getNamedItem("packed");
                        		if (n5!=null) {
						if (n5.getNodeValue().equals("true")) packed=true;
					}

					// done	
					if (packed) {
						PackageInterface pack=PackageManager.foundPackage(this,(Element)n3,name,type,maintainer,version,date,realpath);
						// returns a package if new one
						if (pack!=null) {
							pack.setParentBundle(bun);
						}
					}
				}
				n3=n3.getNextSibling();
			}
		}
		n2=n2.getNextSibling();
	}
    }

   public String getImportPath() {
        String path=MMBaseContext.getConfigPath()+File.separator+"packaging"+File.separator+"import"+File.separator;
        File dir=new File(path);
        if (!dir.exists()) {
                dir.mkdir();
        }
        return path;
   }
}
