/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.gui;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.applications.packaging.installhandlers.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;
import org.mmbase.applications.packaging.projects.creators.*;
import org.mmbase.applications.packaging.projects.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;

/**
 * @author Daniel Ockeloen
 * @version $Id: guiController.java
 */
public class Controller {

    private static Logger log = Logging.getLoggerInstance(Controller.class);
    private static Cloud cloud;
           NodeManager manager;
    CloudContext context;


    public Controller() {
        cloud=LocalContext.getCloudContext().getCloud("mmbase");

        // hack needs to be solved
            manager=cloud.getNodeManager("typedef");
        if (manager==null) log.error("Can't access builder typedef");
        context=LocalContext.getCloudContext();
        if (!InstallManager.isRunning()) InstallManager.init();
    }

    public boolean changeSettings(String providername,String callbackurl) {

        ShareManager.setCallbackUrl(callbackurl);
        ShareManager.setProviderName(providername);
        ShareManager.writeShareFile();
        ShareManager.signalRemoteClients();

        return true;
    }


    public boolean changeUserSettings(String account,String password,String method,String host) {
        ShareUser user=ShareManager.getShareUser(account);
        if (user!=null) {
            user.setPassword(password);
            user.setMethod(method);
            if (host!=null && !host.equals("none")) {
                user.setHost(host);
            }
        }
        ShareManager.writeShareFile();
        ShareManager.signalRemoteClients();
        return true;
    }

    public List getPackageInstallSteps(String id,String wv,String wp,String slogid) {

                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        try {
        int logid=Integer.parseInt(slogid);

        PackageInterface p=null;
        if (wv.equals("best")) {
            p=(PackageInterface)PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            p=(PackageInterface)PackageManager.getPackage(id,wv,wp);
        }
        if (p!=null) {

            Iterator steps=null;
            if (logid==-1) {
                steps=p.getInstallSteps();
            } else {
                steps=p.getInstallSteps(logid);
            }

            while (steps.hasNext()) {
                installStep step=(installStep)steps.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("userfeedback",step.getUserFeedBack());
                virtual.setValue("timestamp",step.getTimeStamp());
                virtual.setValue("errorcount",step.getErrorCount());
                virtual.setValue("warningcount",step.getWarningCount());
                virtual.setValue("id",step.getId());
                virtual.setValue("parent",step.getParent());
                if (step.hasChilds()) {
                    virtual.setValue("haschilds","true");
                } else {
                    virtual.setValue("haschilds","false");
                }
                list.add(virtual);
            }
        }
        } catch (Exception e) {
        }
        return list;
    }



    public List getBundleInstallSteps(String id,String wv,String wb,String slogid) {

                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        try {
        int logid=Integer.parseInt(slogid);

        BundleInterface b=null;
        if (wv.equals("best")) {
            b=(BundleInterface)BundleManager.getBundle(id);
        } else {
            // ok lets decode the version and provider we want
            b=(BundleInterface)BundleManager.getBundle(id,wv,wb);
        }
        if (b!=null) {

            Iterator steps=null;
            if (logid==-1) {
                steps=b.getInstallSteps();
            } else {
                steps=b.getInstallSteps(logid);
            }

            // create a result list

            while (steps.hasNext()) {
                installStep step=(installStep)steps.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("userfeedback",step.getUserFeedBack());
                virtual.setValue("timestamp",step.getTimeStamp());
                virtual.setValue("errorcount",step.getErrorCount());
                virtual.setValue("warningcount",step.getWarningCount());
                virtual.setValue("id",step.getId());
                virtual.setValue("parent",step.getParent());
                if (step.hasChilds()) {
                    virtual.setValue("haschilds","true");
                } else {
                    virtual.setValue("haschilds","false");
                }
                list.add(virtual);
            }
        }
        } catch (Exception e) {
        }
        return list;
    }




    public List havePackageLog(String id,String wv,String wp) {
            List list = new ArrayList();
             VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
             MMObjectNode virtual = builder.getNewNode("admin");

        PackageInterface p=null;
        if (wv.equals("best")) {
            p=(PackageInterface)PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            p=(PackageInterface)PackageManager.getPackage(id,wv,wp);
        }
        if (p!=null) {

            Iterator steps=p.getInstallSteps();
            
            if (steps!=null) {
                virtual.setValue("log","true");
            } else {
                virtual.setValue("log","false");
            }
        }
        list.add(virtual);
        return list;
    }



    public List haveBundleLog(String id,String wv,String wb) {
            List list = new ArrayList();
             VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

             MMObjectNode virtual = builder.getNewNode("admin");

        BundleInterface b=null;
        if (wv.equals("best")) {
            b=(BundleInterface)BundleManager.getBundle(id);
        } else {
            // ok lets decode the version and provider we want
            b=(BundleInterface)BundleManager.getBundle(id,wv,wb);
        }
        if (b!=null) {
            Iterator steps=b.getInstallSteps();
            if (steps!=null) {
                virtual.setValue("log","true");
            } else {
                virtual.setValue("log","false");
            }
        }
        list.add(virtual);
        return list;
    }

    public List getPackageInfo(String id,String wv,String wp) {
            List list = new ArrayList();
             VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

             MMObjectNode virtual = builder.getNewNode("admin");

        PackageInterface p=null;
        if (wv.equals("best")) {
            p=(PackageInterface)PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            p=(PackageInterface)PackageManager.getPackage(id,wv,wp);
        }
        if (p!=null) {
            virtual.setValue("name",p.getName());
            virtual.setValue("version",p.getVersion());
            virtual.setValue("maintainer",""+p.getMaintainer());
            virtual.setValue("type",p.getType());
            virtual.setValue("creation-date",p.getCreationDate());
            ProviderInterface provider=p.getProvider();
            if (provider!=null) {
                virtual.setValue("provider",provider.getName());
            }
            virtual.setValue("state",p.getState());
            virtual.setValue("description",p.getDescription());
            virtual.setValue("releasenotes",p.getReleaseNotes());
            virtual.setValue("installationnotes",p.getInstallationNotes());
            virtual.setValue("licensename",p.getLicenseName());
            virtual.setValue("licensetype",p.getLicenseType());
            virtual.setValue("licenseversion",p.getLicenseVersion());
            virtual.setValue("licensebody",p.getLicenseBody());
        }
        list.add(virtual);
        return list;
    }


    public List getPackagePeople(String id,String wv,String wp,String type) {
            List list = new ArrayList();
             VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());


        PackageInterface p=null;
        if (wv.equals("best")) {
            p=(PackageInterface)PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            p=(PackageInterface)PackageManager.getPackage(id,wv,wp);
        }
        if (p!=null) {
            List people=p.getRelatedPeople(type);
            if (people!=null) {
                    for (Iterator i = people.iterator(); i.hasNext();) {
                Person pr=(Person)i.next();
                     MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("name",pr.getName());
                virtual.setValue("company",pr.getCompany());
                virtual.setValue("reason",pr.getReason());
                virtual.setValue("mailto",pr.getMailto());
                list.add(virtual);
            }
            }
        }
        return list;
    }


    public List getBundlePeople(String id,String wv,String wp,String type) {
            List list = new ArrayList();
             VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());


        BundleInterface b=null;
        if (wv.equals("best")) {
            b=(BundleInterface)BundleManager.getBundle(id);
        } else {
            // ok lets decode the version and provider we want
            b=(BundleInterface)BundleManager.getBundle(id,wv,wp);
        }
        if (b!=null) {
            List people=b.getRelatedPeople(type);
            if (people!=null) {
                    for (Iterator i = people.iterator(); i.hasNext();) {
                Person pr=(Person)i.next();
                     MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("name",pr.getName());
                virtual.setValue("company",pr.getCompany());
                virtual.setValue("reason",pr.getReason());
                virtual.setValue("mailto",pr.getMailto());
                list.add(virtual);
            }
            }
        }
        return list;
    }

    public List getBundleInfo(String id,String wv,String wp) {
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

                MMObjectNode virtual = builder.getNewNode("admin");

        BundleInterface b=null;
        if (wv.equals("best")) {
            b=(BundleInterface)BundleManager.getBundle(id);
        } else {
            // ok lets decode the version and provider we want
            b=(BundleInterface)BundleManager.getBundle(id,wv,wp);
        }
        if (b!=null) {
            virtual.setValue("name",b.getName());
            virtual.setValue("version",b.getVersion());
            virtual.setValue("maintainer",""+b.getMaintainer());
            virtual.setValue("type",b.getType());
            virtual.setValue("creation-date",b.getCreationDate());
            ProviderInterface provider=b.getProvider();
            if (provider!=null) {
                virtual.setValue("provider",provider.getName());
            }
            virtual.setValue("state",b.getState());
                        virtual.setValue("description",b.getDescription());
                        virtual.setValue("releasenotes",b.getReleaseNotes());
                        virtual.setValue("installationnotes",b.getInstallationNotes());
                        virtual.setValue("licensename",b.getLicenseName());
                        virtual.setValue("licensetype",b.getLicenseType());
                        virtual.setValue("licenseversion",b.getLicenseVersion());
                        virtual.setValue("licensebody",b.getLicenseBody());
                        virtual.setValue("bundleprogressbarvalue",b.getProgressBarValue());
                        virtual.setValue("packageprogressbarvalue",b.getPackageProgressBarValue());
        }
        list.add(virtual);
        return list;
    }



    public List getPackageManagerSettings() {
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

                MMObjectNode virtual = builder.getNewNode("admin");

        // get the current providers
        virtual.setValue("providername",ShareManager.getProviderName());
        virtual.setValue("callbackurl",ShareManager.getCallbackUrl());
        list.add(virtual);
        return list;
    }

    public boolean createPackage(String id,String wv,String wp,String newversion) {
        PackageInterface p=null;
        if (wv.equals("best")) {
            p=(PackageInterface)PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            p=(PackageInterface)PackageManager.getPackage(id,wv,wp);
        }
        if (p!=null) {
            //InstallManager.installPackage(p);
            log.info("Create package");
        }
        return true;
    }


    public void signalRemoteChange(String id) {
        ProviderInterface provider=ProviderManager.get(id);
        if (provider!=null) {
            provider.getPackages();    
                    PackageManager.removeOfflinePackages(provider);
        } else {
            log.error("no provider : "+id);
        }
    }


    public String getRelatedPeopleString(List people,String type) {
        String body="";
        if (people!=null) {
                   for (Iterator i = people.iterator(); i.hasNext();) {
            Person pr=(Person)i.next();
                if (type.equals("initiators")) {
                    body+="\t\t\t<initiator name=\""+pr.getName()+"\" company=\""+pr.getCompany()+"\" />\n";
                } else if (type.equals("developers")) {
                    body+="\t\t\t<developer name=\""+pr.getName()+"\" company=\""+pr.getCompany()+"\" mailto=\""+pr.getMailto()+"\" />\n";
                } else if (type.equals("contacts")) {
                    body+="\t\t\t<contact reason=\""+pr.getReason()+"\" name=\""+pr.getName()+"\" mailto=\""+pr.getMailto()+"\" />\n";
                } else if (type.equals("supporters")) {
                    body+="\t\t\t<supporter company=\""+pr.getCompany()+"\" />\n";
                }
            }
        }
        return body;
    }

}
