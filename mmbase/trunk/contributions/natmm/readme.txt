NatMM - readme.txt
Version: 06.04.12
Author: H. Hangyi

NatMM is an MMBase application which is based on LeoCMS, which is specially geared for the use in
small to medium size organisations. I.e. organisations that do not need workflow, staging and live clouds, etc.

Features of LeoCMS (currently) not used in NatMM:
1. one click editing
2. workflow
3. versioning
4. notification
5. remote publishing (staging and live clouds)

What is in it for you?

1. All the other features of LeoCMS, like the page-tree, authorisation model, nice-looking editwizards, content-library,
   and much more. LeoCMS has been build by Finalist.
2. The event-database in use at www.natuurmonumenten.nl. For the event-database both a back-office booking system
   and booking on the website is implemented (Struts). The event-database contains export to Excel and functionality to 
   generate statistics. See templates/natmm/doc/DatamodelCAD.doc for the datamodel and the GebruikershandleidingCAD*.doc
   for the user manuals. Unfortunately the user manuals are in Dutch.
3. Preview functionality based on OSCache
4. Image cropping (see natmm\templates\editors\util\image_crop.jsp). Build by C. Brands.
5. Image bulk upload (see natmm\templates\editors\util\image_upload.jsp). Build by N. Bukharev.
6. Example templates (see natmm\templates\natmm and nmintra) 
   and the accompanying editwizards (see natmm\templates\mmbase\edit\wizard\data\config)

See install\install.txt for installation.
See install\license.txt for the license.
