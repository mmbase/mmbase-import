This is the 'old' frontend for the Editwizards.

You might need it to support backwards compatibility.


How to install.

  Copy all directories to your web-app.
  Suggestions:
  /mmbase/edit/wizard.deprectated  (next to the 'new' front end)
  /mmapps/editwizard               (original location of 1.6)

  Then, you also need the 'jsp' directory and the 'media' directory, which
  do not have 2 versions.

  You could copy them from /mmbase/edit/wizard/jsp and  /mmbase/edit/wizard/media (or templates/ if
  from CVS), perhaps with a symlink:

  /home/mmbase/mmbase-app/mmbase/edit/wizard.deprecated$ ln -s  ~/mmbase/head/applications/editwizard/templates/media
  /home/mmbase/mmbase-app/mmbase/edit/wizard.deprecated$ ln -s  ~/mmbase/head/applications/editwizard/templates/jsp 

  And now use this location. Perhaps you somewhere in you editor entrance page have a setting like:

  for old frontend:
  <mm:import id="jsps">/mmbase/edit/wizard.deprecated/jsp/</mm:import> 
  for new frontend:
  <mm:import id="jsps">/mmbase/edit/wizard/jsp/</mm:import> 

  If you do not have such a setting, and you are upgrading from an 1.6 installation you might simply 
  want to go for placing the whole lot in /mmapps/editwizard.


$Id: README.txt,v 1.1 2004-01-20 15:48:05 michiel Exp $
 
