NatMM - readme.txt
Version: 06.06.20
Author: H. Hangyi (www.mmatch.nl)

NatMM is an MMBase application which is based on LeoCMS, which is specially geared for use in
small to medium size organisations. I.e. organisations for which simplicity and ease of publishing, is
more important than workflow, staging and live clouds, etc.

Features of LeoCMS (currently) not used in NatMM:
1. one click editing
2. workflow
3. notification
4. remote publishing (staging and live clouds)

WHAT IS IN IT FOR YOU?

1. All the other features of LeoCMS, like the 
   (a) support for multiple websites in one environment,
   (b) rubriek and page-tree,
   (c) authorisation model,
   (d) nice-looking editwizards,
   (e) content-library with garbage can functionality,
   (f) url-rewriting for Google friendly urls,
   (g) versioning
   and much more. The original version of LeoCMS was build by Finalist.
	Have a look at the user-manual /templates/doc/GebruikershandleidingEditors.doc (in Dutch unfortunately) for the basic functionality.
2. The event-database in use at www.natuurmonumenten.nl. For the event-database both a back-office booking system
   and booking on the website is implemented (Struts). The event-database contains export to Excel and functionality to 
   generate statistics. See templates/natmm/doc/DatamodelCAD.doc for the datamodel and the GebruikershandleidingCAD*.doc
   for the user manuals. Unfortunately the user manuals are in Dutch.
3. Preview functionality based on OSCache
4. In the NatMM version of LeoCMS paragraph functionality has been added. So each article can contain any number of paragraphs.
5. Carbage can functionality, showing the users which of their contentelements are no longer in use and could be removed.
6. Image cropping (see natmm\templates\editors\util\image_crop.jsp). Build by C. Brands.
7. Image bulk upload (see natmm\templates\editors\util\image_upload.jsp). Build by N. Bukharev.
8. Creating navigation structure from Excel file. Build by A. Zemskov.
9. Check on email addresses, Dutch zipcodes and bankaccounts (see natmm\src\nl\leocms\forms\MembershipForm.java)
10 Integration of Lucene for searching on the website (sourceforge implementation)
11. Example templates (the most basic one is templates\natnh) 
   and the accompanying editwizards (see natmm\templates\mmbase\edit\wizard\data\config)

WHAT IS THE BASIC STRUCTURE OF NATMM?

Underlying NatMM is the LeoCMS objectmodel, which each application in the NatMM application uses.
This objectmodel can be found in config/application/LeoCMS.xml.

Each website in the NatMM application, e.g. MySite, should consist of the following parts:
1. a folder with templates e.g. templates/mysite
2. a configuration file config/applications/MySite.xml and builders in config/applications/MySite/builders
   IMPORTANT NOTE: in the NatMM application each builder should be stored in CVS only ONCE. So builders reused
   from LeoCMS or any of the other applications should be copied to the config/applications/builders directory
   before install.
3. a java class with application specific settings in src/leocms/applications/MySiteConfig.java. See NatNHConfig.java
	for an example of what this class should at least contain.
   Important information provided by MySiteConfig.java is CONTENTELEMENTS, OBJECTS and PATHS_FROM_PAGE_TO_OBJECTS.
   These arrays tell LeoCMS how content is related to the pages in the MySite application. This information 
   is used in several places:
   a. to find the page if you only provide the id of a contentelement in the url
      (see nl.leocms.util.PaginaHelper.findIDs )
   b. to do url-rewriting
      (see see nl.leocms.util.PaginaHelper.createItemUrl and nl.leocms.servlets.UrlConverter.convertUrl )
   c. to see if a page still contains contentelements
      (see nl.leocms.pagina.PaginaUtil.doesPageContainContentElements)
4. editwizards in templates/mmbase/edit/wizard/data/config
5. user manuals and technical documentation in templates/mysite/doc

HOW TO ADD YOUR OWN APPLICATION TO NATMM?

The minimal steps you have to carry out to add your own application to NatMM are:
1. install LeoCMS (see install.txt)
2. add paginatemplate nodes to MMBase (use /mmbase/edit/my_editors for this)
3. enter rubrieken and paginas by using the Pagina-editor and relate the pages to the paginatemplates created in step 2
4. add the objects and relations you need in your application. Deploying an application config file like NatMM.xml
   is the easiest way to do this. Read the MMBase documentation if you need more information on this topic.
5. implement the jsp-templates that correspond to the paginatemplate nodes created in step 2
   (e.g. in a subfolder templates/mysite). Please adhere to the coding-standards as much as possible.
	
	One of the most complex parts of your templates will probably the navigation. Because NatMM allows
	for a website tree of any depth. In the ideal case, your navigation should also take this into account. 
	See templates/nmintra/include/nav.jsp and templates/natmm/includes/top3_nav.jsp for examples on how to do this.
	
6. add MySiteConfig.java to make sure the editors and url-rewriting work properly
7. if your application uses crontabs (see config/modules/crontab.xml), please make sure that the implementation
	checks on the applications for which the crontab should apply (see as an example nl.mmatch.CSVReader.java).

PAGINA, THE CONTAINER FOR CONTENTELEMENTS

The pagina objects play a key role in LeoCMS, the are the leaves of the rubrieken tree and contain the objects and
contentelements to be shown on the page. In different files the paths from pagina to related objects are specified.
1. in nl.leocms.applications.*Config.PATHS_FROM_PAGE_TO_OBJECTS
2. in templates/mmbase/edit/wizard/data/config/pagina/load_pagina*.xml
3. in config/lucenedatadefinition.xml
4. and of course in the templates

HINTS ON HOW TO DEVELOP WITH NATMM

After installing NatMM you want to quickly develop your templates. The first step is of course to create the object model you
need, but once you created and tested an application file it comes to building templates and sometimes adding java classes. 
One way of managing this is explained below. Of course there are other ways of developing, so feel free to add your best practices.

For building you checked out het NatMM contribution from CVS. After installation you can copy the template dirs in which you plan to
work from the CVS checkout to your Tomcat installation. Most of the time you will only be changing (a) files in these templates dir and (b)
sources in the /natmm/src tree.

1. For doing an UPDATE FROM CVS in this situation you have to both update the natmm contribution and the template dirs.
2. For CHANGES IN THE SRC, create a new jar (onlyjar.bat) and place this it in the WEB-INF/lib of the webapp in which NatMM is running
   (to save you copy and paste time you can add the copy task to the batch job that is starting up your application server)
3. CHANGES TO THE TEMPLATES can be directly applied to the templates in your Tomcat installation
4. For CREATING A BUILD carry out the following steps:
   a. commit the changes from the templating dirs to CVS
	b. update the NatMM contribution
	c. create the build (work.bat). Note that the resulting war is not deployable as a webapp, but should be copied over an MMBase installation.

SOME LAST REMARKS

The current verion of NatMM is tested with MMBase 1.7.4 / JDK 1.4 and 1.5 / MySQL 5.1.
The migration of NatMM to MMBase 1.8.x is planned for Q4, 2006.

See install\install.txt for installation.
See install\license.txt for the license.

In case you find issues in the NatMM version of LeoCMS, you can post them in http://www.mmbase.org/bug
Please make sure you select 'Contrib: NatMM' as Area. Thanks a lot for your contribution!

