NatMM - readme.txt
Version: 06.04.24
Author: H. Hangyi

NatMM is an MMBase application which is based on LeoCMS, which is specially geared for the use in
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
4. editwizards in templates/mmbase/edit/wizard/data/config

See install\install.txt for installation.
See install\license.txt for the license.
