===================================================================
                     MMBase 1.7.0 - Readme
===================================================================  
  
MMBase is an Open Source content management system that can be used
for publishing websites (among other things like email, sms,
etc). MMBase  coveres the whole spectrum needed to manage and maintain
information. In other words, MMBase has editors for editorial people
to insert, manipulate, and delete information. 
MMBase uses jsp/taglib as a language which enables web designers to
publish the information dynamically. And MMBase has knowledge of
connected devices. This means that publishing a CD, or an image
(placed on a scanner), or a live stream involves minimal work.

It includes the following contents:

LICENSE                     MPL-1.0 license
building.txt                Instruction for building MMBase from
                            source-distro (only included in
			                source-distro) 
readme.txt                  This file
releasenotes-1.6.0.txt      Releasenotes for this distro (contains
                            changes/bugfixes/know issues) 
installation.txt            Instructions for installing MMBase
mmbase-webapp/              Webapplication dir, this dir goes into
                            your applicationserver/servlet engine
  WEB-INF/                  Webapplication config dir, contains both the
                            config for the webapp and for MMBase
    config/                 MMBase config dir
      accounts.properties   File with accounts/passwords (deprecated)
      applications/         Applications that can be installed
      builders/             All builders that can be used, including
                            core builders and optional builders. Not
			                all builders are activated by default.
      caches.xml            Configuration file for cache configuration
      databases/            Databaseconfig for databases that can be
                            used with MMBase
      dtd/                  A few dtds, used in some xslt-transformation
      log/                  Logging configuration
      modules/              All modules that can be used, including
                            core modules and optional modules. Not all
			                modules are activated by default 
      security/             Configuration for the security
      xslt/                 Some xslt's for displaying config files in
                            adminpages
    lib/                    All jars needed by MMBase
      legal		            Contains licenses for external jars MMBase
                            is redistributing
    web.xml                 a default configuration file for MMBase as
                            a webapp, should be sufficient for most
			                basic configurations
  mmadmin/                  (scan) Admin pages (deprecated)
    jsp/                    (jsp) Admin pages
  mmapps/                   Directory for jsp/html/etc for MMBase-applications
    xmlimporter             Jsps needed by xmlimporter
  mmdocs/                   Documentation for different groups of
                            users (templaters/administrators/etc)  
  mmeditors/                (scan) Editors (deprecated)
    jsp/                    (jsp) Editors
  mmexamples/               Examples directory
    share/                  Directory for shared files between examples
    jsp/                    Jsp-examples
    scan/                   Scan-examples (deprecated)
     

MMBase is distributed in two different 'flavours', mmbase-src and
mmbase. First is the source distribution and the second is the binary
distribution. 

The binary distribution's target users are end-users and first-time
users. It contains a working hsqldb database, applications that can be
deployed and a few working examples.
The logging for the binary distro is going to the standardout, you
have to find out which log file of your Servlet-engine is logging
standard out, for Tomcat 4 ie catalina.out. 

The source distribution's target users are developers who like to start
building their own MMBase.

      
