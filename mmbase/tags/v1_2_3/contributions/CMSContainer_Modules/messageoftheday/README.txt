DESCRIPTION:
Shows message of the day on the dashboard

CONFIGURATION:
You will need the following properties, if they are not created when making a new application, 
create them yourselves in the mmbase editors (login with an admin user on:
http://[url]:[port]/[application]/mmbase/edit/my_editors/). Make sure you create these properties
with the module: "messageoftheday", when creating them, you can keep them empty and edit them
from the modules/NTLM edit screen.
* dashboard.welcome.message
* dashboard.welcome.header

When editing from the modules/message of the day edit screen:
* dashboard.welcome.message        --> the message to show, keep empty if you do not want a message at the moment
* dashboard.welcome.header         --> the header of the message

TODO:
* the message showing is hardcoded in the dashboard, move that to an include
