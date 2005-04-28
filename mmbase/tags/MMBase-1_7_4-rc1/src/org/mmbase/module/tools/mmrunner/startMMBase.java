/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module.tools.mmrunner;

import java.io.*;

public class startMMBase extends Object {
    
    static String runnerVersion="0.6";
    static String appserverVersion="Orion 1.4.5";
    static String cmsVersion="MMBase 1.2.3";
    static String databaseVersion="Hypersonic 1.4.3";
    static boolean config=false;
    
    
    public static void main(String[] args) {
        String mode;
        System.out.println("\nStarting MMBase (runner version "+runnerVersion+")");
        if (args.length>0) {
            mode=args[0];
            if (mode.equals("config")) {
                config=true;
                mode="loop";
            }
        } else {
            mode="loop";
        }
        
        // should be moved to own method in time
        String curdir=System.getProperty("user.dir");
        if (curdir.endsWith("orion")) {
            curdir=curdir.substring(0,curdir.length()-6);
        }
        
        // detect if this is the first startup
        if (firstContact(curdir) || config) {
            System.out.println("\nDetecting this is first run, need to ask a few questions.");
            setupMMRunner(curdir);
        }
        
        System.out.println("\n----- starting java with parameters  -----");
        String configdir="-Dmmbase.config="+curdir+"/config/ ";
        System.out.println(configdir);
        String htmlrootdir="-Dmmbase.htmlroot="+curdir+"/html/ ";
        System.out.println(htmlrootdir);
        String logdir="-Dmmbase.outputfile="+curdir+"/log/mmbase.log ";
        System.out.println(logdir);
        String orion="-jar orion/orion.jar -config orion/config/server.xml";
        System.out.println(orion);
        String startupstring="java "+configdir+htmlrootdir+logdir+orion;
        System.out.println("-------------------------------------------\n");
        System.out.println("Starting Application server : "+appserverVersion);
        System.out.println("Loading  CMS : "+cmsVersion);
        System.out.println("Loading  JDBC Database : "+databaseVersion+"\n");
        
        String activehost=getSetting(curdir+"/config/modules/mmbaseroot.xml","\"host\">","<");
        String activeport=getSetting(curdir+"/orion/config/default-web-site.xml","port=\"","\"");
        if (mode.equals("loop")) {
            System.out.println("Logfiles active in the log dir (log/mmbase.log for example)");
            System.out.println("Within seconds server can be found at http://"+activehost+":"+activeport);
            while (1==1) {
                String reply=execute(startupstring);
            }
        } else if (mode.equals("bg")) {
            System.out.println("Logfiles active in the log dir (log/mmbase.log for example)");
            System.out.println("Within seconds server can be found at http://"+activehost+":"+activeport);
            String reply=executeSpawn(startupstring);
        }
        
    }
    
    
    static String execute(String command) {
        Process p=null;
        String s="",tmp="";
        
        BufferedReader	dip= null;
        BufferedReader	dep= null;
        
        try {
            p = (Runtime.getRuntime()).exec(command,null);
            p.waitFor();
        }
        catch (Exception e) {
            s+=e.toString();
            return s;
        }
        
        dip = new BufferedReader( new InputStreamReader(p.getInputStream()));
        dep = new BufferedReader( new InputStreamReader(p.getErrorStream()));
        
        try {
            while ((tmp = dip.readLine()) != null) {
                s+=tmp+"\n";
            }
            while ((tmp = dep.readLine()) != null) {
                s+=tmp+"\n";
            }
        }
        catch (Exception e) {
            return s;
        }
        return s;
    }
    
    
    static String executeSpawn(String command) {
        Process p=null;
        String s="",tmp="";
        
        BufferedReader	dip= null;
        BufferedReader	dep= null;
        
        try {
            p = (Runtime.getRuntime()).exec(command,null);
            //p.waitFor();
        }
        catch (Exception e) {
            s+=e.toString();
            return s;
        }
        return("");
        
    }
    
    static private boolean firstContact(String curdir) {
        File f=new File(curdir+"/config/.timestamp");
        return (!f.exists() || !f.isFile());
    }
    
    static void setupMMRunner(String curdir) {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        
        // first get a valid hostname
        String hostname=null;
        while (hostname==null) {
            hostname=getConsoleQuestion(reader,"The ipname of your machine for example mybox.myisp.com","Hostname of this machine [localhost]");
            if (hostname.equals("")) {
                hostname="localhost";
            }
        }
        
        
        // first get a valid machinename
        String machinename=null;
        while (machinename==null) {
            machinename=getConsoleQuestion(reader,"The name of this mmbase in your cloud cluster","Name of this mmbase machine [mmbase1]");
            if (machinename.equals("")) {
                machinename="mmbase1";
            }
        }
        
        
        int port=-1;
        while (port==-1) {
            String tmp=getConsoleQuestion(reader,"The ip port the webserver needs to be running (80 - 65535)","Port [4242]");
            if (tmp.equals("")) {
                port=4242;
            } else {
                try {
                    port=Integer.parseInt(tmp);
                    if (port<80 || port>65535) {
                        port=-1;
                    }
                    
                } catch(Exception e) {
                    port=-1;
                }
            }
        }
        
        
                        /*
                        int mport=-1;
                        while (mport==-1) {
                                String tmp=getConsoleQuestion(reader,"The multicast port used to talk to other mmbase nodes (10000 - 65535)","Port [42420]");
                                if (tmp.equals("")) {
                                        mport=42420;
                                } else {
                                        try {
                                                mport=Integer.parseInt(tmp);
                                                if (mport<10000 || mport>65535 || port==mport) {
                                                        mport=-1;
                                                }
                         
                                        } catch(Exception e) {
                                                mport=-1;
                                        }
                                }
                        }
                         * @rename StartMMBase
                         */
        
        
        // first get new admin password
        String password=null;
        while (password==null) {
            password=getConsoleQuestion(reader,"The admin password for mmbase","password [admin2k]");
            if (password.equals("")) {
                password="admin2k";
            }
        }
        
        // first get new mailing host
        String mailhost=null;
        while (mailhost==null) {
            mailhost=getConsoleQuestion(reader,"Your mailing host for example smtp.mmbase.org","mailhost [smtp.mmbase.org]");
            if (mailhost.equals("")) {
                mailhost="smtp.mmbase.org";
            }
        }
        
        
        // oke now set all these new setting in each of the files
        String activeport=getSetting(curdir+"/orion/config/default-web-site.xml","port=\"","\"");
        updateConfigFile(curdir+"/orion/config/default-web-site.xml","port=\""+activeport+"\"","port=\""+port+"\"");
        //updateConfigFile(curdir+"/config/modules/mmbaseroot.xml","42420",""+mport);
        updateConfigFile(curdir+"/config/modules/mmbaseroot.xml","mmbase1",machinename);
        String activehost=getSetting(curdir+"/config/modules/mmbaseroot.xml","\"host\">","<");
        updateConfigFile(curdir+"/config/modules/mmbaseroot.xml",activehost,hostname);
        updateConfigFile(curdir+"/config/accounts.properties","admin2k",password);
        updateConfigFile(curdir+"/config/modules/sendmail.xml","smtp.mmbase.org",mailhost);
        saveFile(curdir+"/config/.timestamp","mmbase time stamp");
    }
    
    static String getConsoleQuestion(BufferedReader reader,String help, String question) {
        try {
            System.out.println("\n"+help);
            System.out.print(question+" : ");
            return(reader.readLine());
        } catch(Exception e) {
            return(null);
        }
    }
    
    private static boolean updateConfigFile(String file,String oldstring,String newstring) {
        String body=loadFile(file);
        int len=oldstring.length();
        int pos=body.indexOf(oldstring);
        if (pos!=-1) {
            String newbody=body.substring(0,pos);
            newbody+=newstring;
            newbody+=body.substring(pos+len);
            saveFile(file,newbody);
        }
        return(true);
    }
    
    private static String getSetting(String file,String setting,String endtoken) {
        String body=loadFile(file);
        int len=setting.length();
        int pos=body.indexOf(setting);
        if (pos!=-1) {
            String value=body.substring(pos+len);
            pos=value.indexOf(endtoken);
            if (pos!=-1) {
                value=value.substring(0,pos);
                return(value);
            }
        }
        return("");
    }
    
    
    static boolean saveFile(String filename,String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return(true);
    }
    
    
    static String loadFile(String filename) {
        try {
            File sfile = new File(filename);
            FileInputStream scan =new FileInputStream(sfile);
            int filesize = (int)sfile.length();
            byte[] buffer=new byte[filesize];
            int len=scan.read(buffer,0,filesize);
            if (len!=-1) {
                String value=new String(buffer,0);
                return(value);
            }
            scan.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return(null);
    }
}
