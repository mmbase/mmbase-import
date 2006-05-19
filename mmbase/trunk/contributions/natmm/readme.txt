NatMM - readme.txt
Version: 06.05.19
Author: H. Hangyi

NatMM is an MMBase application which is based on LeoCMS, which is specially geared for use in
small to medium size organisations. I.e. organisations for which simplicity and ease of publishing, is
more important than workflow, staging and live clouds, etc.

Features of LeoCMS (currently) not used in NatMM:
1. one click editing
2. workflow
3. notification
4. remote publishing (staging and live clouds)

What is in it for you?

1. All the other features of LeoCMS, like the 
   (a) page-tree, 
   (b) authorisation model,
   (c) nice-looking editwizards,
   (d) content-library,
   (e) url-rewriting for Google friendly urls, 
   (f) versioning
   and much more. The original version of LeoCMS was build by Finalist.
	Have a look at the user-manual /templates/doc/GebruikershandleidingEditors.doc (in Dutch unfortunately) for the basic functionality.
2. The event-database in use at www.natuurmonumenten.nl. For the event-database both a back-office booking system
   and booking on the website is implemented (Struts). The event-database contains export to Excel and functionality to 
   generate statistics. See templates/natmm/doc/DatamodelCAD.doc for the datamodel and the GebruikershandleidingCAD*.doc
   for the user manuals. Unfortunately the user manuals are in Dutch.
3. Preview functionality based on OSCache
4. Image cropping (see natmm\templates\editors\util\image_crop.jsp). Build by C. Brands.
5. Image bulk upload (see natmm\templates\editors\util\image_upload.jsp). Build by N. Bukharev.
6. Creating navigation structure from Excel file. Build by A. Zemskov.
7. Check on email addresses, Dutch zipcodes and bankaccounts (see natmm\src\nl\leocms\forms\MembershipForm.java)
8. Example templates (see natmm\templates\natmm and nmintra) 
   and the accompanying editwizards (see natmm\templates\mmbase\edit\wizard\data\config)

What is the basic structure of NatMM?

Underlying NatMM is the LeoCMS objectmodel, which each application in the NatMM application uses.
This objectmodel can be found in config/application/LeoCMS.xml.

Each website in the NatMM application, e.g. MySite, should consist of the following parts:
1. a folder with templates e.g. templates/mysite
2. a configuration file config/applications/MySite.xml and builders in config/applications/MySite/builders
   IMPORTANT NOTE: in the NatMM application each builder should be stored in CVS only ONCE. So builders reused
   from LeoCMS or any of the other applications should be copied to the config/applications/builders directory
   before install.
3. a java class with application specific settings in src/nl/mmatch/MySiteConfig.java
   Important information provided by MySiteConfig.java is  CONTENTELEMENTS and PATHS_FROM_PAGE_TO_ELEMENTS.
   These arrays tell LeoCMS how content is related to the pages in the MySite application. This information 
   is used in several places:
   a. to find the page if you only provide the id of a contentelement in the url
      (see nl.leocms.util.PaginaHelper.findIDs )
   b. to do url-rewriting
      (see see nl.leocms.util.PaginaHelper.createItemUrl and nl.leocms.servlets.UrlConverter.convertUrl )
   c. to see if a page still contains contentelements
      (see nl.leocms.pagina.PaginaUtil.doesPageContainContentElements)
4. editwizards in templates/mmbase/edit/wizard/data/config
5. user manuals and technical documentaion in templates/mysite/doc

How to add your own application to NatMM?

The minimal steps you have to carry out to add your own application to NatMM are:
1. install LeoCMS (see install.txt)
2. add paginatemplate nodes to MMBase (use /mmbase/edit/my_editors for this)
3. enter rubrieken and paginas by using the Pagina-editor and relate the pages to the paginatemplates created in step 2
4. add the objects and relations you need in your application. Deploying an application config file like NatMM.xml
   is the easiest way to do this. Read the MMBase documentation if you need more information on this topic.
5. implement the jsp-templates that correspond to the paginatemplate nodes created in step 2
   (e.g. in a subfolder templates/mysite). Please adhere to the coding-standards as much as possible.
	
	One of the most complex parts of your templates will probably the navigation. Because NatMM allows
	for a website tree of any depth, your navigation should also take this into account. 
	See templates/nmintra/include/nav.jsp and templates/natmm/includes/top3_nav.jsp for examples on how to do this.
	
6. add MySiteConfig.java to make sure the editors and url-rewriting work properly

The current verion of NatMM is tested with MMBase 1.7.4 / JDK 1.4 and 1.5 / MySQL 5.1.
The migration of NatMM to MMBase 1.8.x is planned for Q4, 2006.

See install\install.txt for installation.
See install\license.txt for the license.

In case you find issues in the NatMM version of LeoCMS, you can post them in http://www.mmbase.org/bug
Please make sure you select 'Contrib: NatMM' as Area. Thanks a lot for your contribution!

