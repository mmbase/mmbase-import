MMBase Readme File
------------------

Contents
--------
Introduction
What is MMBase?
New features
Known Bugs/issues
Installation Requirements
Installation
Starting MMBase
Directory Structure
Compiling
Additional Information


Introduction
------------
If this is your first encounter with MMBase, you'd better try MMRunner. 
MMRunner, the MMBase demo, is a very ease to install version of MMBase.
MMRunner contains a webserver (Orion), a database (Hypersonic) and MMBase 
and it runs on various different Operating Systems. You can easily install 
and uninstall MMRunner.

What is MMBase?
---------------
MMBase is an Open Source content management system that can be used for publishing websites.
MMBase coveres the whole spectrum needed to manage and maintain information. In other words,
MMBase has editors for editorial people to insert, manipulate, and delete information. 
MMBase has a language which enables web designers to publish the information dynamically. 
And MMBase has knowledge of connected devices. This means that publishing a CD, or an image 
(placed on a scanner), or a live stream involves minimal work.

New features
------------
There are a lot of new features since the previous release, here's a list which is probably
not complete :)
- logging 1.0
- mmci 1.0
- taglibs 0.8
- relations now include directionality
- remote builders major update
- lots of updates on javadocs
- better multilevel cache
- email-support (also for bulkmail)
- security 1.0
- admin-tool (support for emailqueue and cache)
- documentation is included in the release
- improved install/build facilities (ant build script)
- communities (chat/forum)

For some of the new features there's documentation in the mmbase/html/mmdocs-dir.

Known Bugs/issues
-----------------
- not all examples are finished
- documentation isn't organized
- Transactions do not have rollback
Other bugs can be found at <http://www.mmbase.org/bug>

Installation Requirements
-------------------------
- A Java Runtime Environment (JRE), tested are:
    - JDK1.2
    - JDK1.3
- A database, in which MMBase can store its information, tested are:
    - HyperSonic
    - MySQL
    - Informix
    - Postgress
- A Webserver, for publishing information and the editors, tested are:
    - Orion
    - Apache with Tomcat
- A few libs:
    - Apache Xerces (required)
    - Apache Log4J
    - A JDBC-driver for the used database (required)
    - Apache Xalan (optional)
All the above libs can be found in the 'lib'-dir, except for the jdbc-driver, 
which depends on the database you're using. 

Installation
------------
For installing MMBase some knowledge about Java and XML is needed.
Because of the different setups MMBase can use, the installation documentation
isn't included here. You can find all the information at <http://www.mmbase.org>.
Here are a few links to the installation documentation:

- Installation guide (Orion) 
  <http://www.mmbase.org/mmbasenew/index2.shtml?download+3309>
- Installion guide (Apache Tomcat)
  <http://www.mmbase.org/mmbasenew/documentation.shtml?documentation+483+1686>
- Other information about installation and configuration of MMBase
  <http://www.mmbase.org/mmbasenew/index2.shtml?documentation+483>

If you have any problems installing MMBase, try running 'ant config-test' in the 'scripts'-dir.
If there are problems with you're configuration, it will print an errormessage.

Starting MMBase
---------------
You can find Orion startup scripts and Tomcat startup scripts in the scripts 
directory. More information about starting MMBase you'll find in the documentation
mentioned in the Installation part above.

Directory Structure
-------------------
The distribution has the following directory structure:

- mmbase-<dateofdistro>
  |-- config (the configuration files)
  |   |--default (the default configuration)
  |-- docs 
  |   |--api (javadocs)
  |-- html (html-sources)
  |   |--WEB-INF (sample web.xml for Tomcat and Orion)
  |   |--mmadmin (adminpages)
  |   |--mmeditors (editorpages)
  |   |--mmdocs (some documentation about MMBase)
  |   |--mmdemo (files needed for examples and MMRunner)
  |   |  |--examples (examples, this is the old place for examples)
  |   |--mmexamples (new place of examples)
  |-- lib (mmbase.jar and other needed and optional jars)
  |-- log (dir where you'll find the mmbase.log after startup)
  |-- scripts (scripts for starting up MMBase and compiling MMBase)
  |   |--tomcat (startup scripts for MMBase with Tomcat)
  |   |--orion (startup scripts for MMBase with Orion) 
  |-- src (javasources)
      |-- org
          |-- mmbase

Compiling
---------
If you want to compile your own MMBase, you'll need Apache Ant. The ant-lib is included 
in the 'lib'-directory and the ant-scripts in de 'scripts' directory.
Add the libs from the 'lib'-dir to your classpath and go to the scripts dir. 
'./ant' (or 'ant' on windows) will display a list of possible targets. 
You can test if you're ready to compile MMBase with 'ant compile-check'.

Additional Information
----------------------
<http://www.mmbase.org>


