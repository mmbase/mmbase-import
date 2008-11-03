===================================================================
                     MMBase 1.9.0 - Readme
===================================================================  
  
MMBase is an Open Source content management system that can be used
for publishing websites (among other things like email, sms,
etc). MMBase  coveres the whole spectrum needed to manage and maintain
information. In other words, MMBase has editors for editorial people
to insert, manipulate, and delete information. 
MMBase uses jsp/taglib as a language which enables web designers to
publish the information dynamically. 

It includes the following contents:

LICENSE                     MPL-1.0 license
readme.txt                  This file
releasenotes.txt            Releasenotes for this distro (contains
                            changes/bugfixes/know issues) 
installation.txt            Instructions for installing MMBase
upgrade.txt                 Instructions for upgrading existing MMBase
                            installations
mmbase-webapp/              Webapplication dir, this dir goes into
                            your applicationserver/servlet engine
  WEB-INF/                  Webapplication config dir, contains both the
                            config for the webapp and for MMBase
    config/                 MMBase config dir
      accounts.properties   File with accounts/passwords (deprecated)
      applications/         Applications that can be installed
      builders/             Builder config files
        core/               Core builder files
        applications/       Application builder files, contains
                            builders of default installed
                            applications.
      caches.xml            Configuration file for cache configuration
      databases/            Databaseconfig for databases that can be
                            used with MMBase
      dtd/                  dtds which have no place elsewhere :)
                            Other dtds are placed inside one of the
                            jars
      log/                  Logging configuration
      magic.xml             File with information about mimetypes
      modules/              All modules that can be used, including
                            core modules and optional modules. Not all
                            modules are activated by default
      security/             Configuration for the security
      utils/                All config related to org.mmbase.util-classes 
      xslt/                 Some xslt's for displaying config files in
                            adminpages
    lib/                    All jars needed by MMBase
      legal		            Contains licenses for external jars which
                            are distributed with MMBase 
    web.xml                 a default configuration file for MMBase as
                            a webapp, should be sufficient for most
                            basic configurations
  mmbase/                   All mmbase-related templates                 
    admin/                  Admin pages
    edit/                   
      basic/                Basic jsp-editors
      mmeditors/            Jsp-editors based on old (and deprecated)
                            scan editors
      my_editors/           Alternative my_editors
      wizards/              Editwizards
    index.jsp               
    mpl-1.0.jsp             License
    style                 
  mmexamples/               Examples directory

MMBase is distributed in two different 'flavours', mmbase-src and
mmbase. First is the source distribution and the second is the binary
distribution. 

The binary distribution's target users are end-users and first-time
users. It contains a working hsqldb database, applications that can be
deployed and a few working examples.
The logging for the binary distro is going to the standardout, you
have to find out which log file of your Servlet-engine is logging
standard out, for Tomcat 4/5 ie catalina.out. 

The source distribution's target users are developers who like to start
building their own MMBase.
