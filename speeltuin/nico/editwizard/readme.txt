These files are the frontend of the editwizards.
They are for the mmbase 1.7 release.
You have to use the jsp's and java files from the applications/editwizard directory.

The 1.7 backend supports some extra features which can quite easily be
removed from the frontend.
- The saveonly button is added
- The security checks for maywrite and maydelete are added

Only the wizard.xsl has to be modified to use this on the 1.6 release.
I will create a version for the 1.6 release soon.

Steps to get it working
-----------------------
Copy files from /applications/editwizard to /speeltuin/nico/editwizard
- templates/jsp/* to templates/jsp/*
- templates/data/i18n/* to templates/data/i18n/*
  NOTE: the prompts in the i18n dir are not optimized for the new frontend
        so some things will look ugly when used.

Compile java source files from /applications/editwizard/src and create 
the mmbase-editwizard.jar (see /applications/editwizard/build.xml).

Add all files from /speeltuin/nico/editwizard/templates/ to your editwizard
 dir in your webapp dir (usually this is <web-app>/mmapps/editwizard)
Add the class files to the <wb-app>/WEB-INF/classes and the 
mmbase-editwizard.jar to the <wb-app>/WEB-INF/lib

If you want to use the dtmin and dtmax field attributes of the wizard-schema's
then you need the wizard-schema_1_0.dtd.

I added to the examples dir te look&feel we use at finalist. It shows how to
change colors and override the xsl's and javascript files.

Nico Klasens

Finalist IT Group
Java Specialist.

(nico@finalist.com or mmbase@klasens.net)