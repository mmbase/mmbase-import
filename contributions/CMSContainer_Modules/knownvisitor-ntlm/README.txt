DESCRIPTION:
Provides direct NTLM authentication for known visitors

CONFIGURATION:
You will need the following properties, if they are not created when making a new application, 
create them yourselves in the mmbase editors (login with an admin user on:
http://[url]:[port]/[application]/mmbase/edit/my_editors/). Make sure you create these properties
with the module: "knownvisitor-ntlm", when creating them, you can keep them empty and edit them
from the modules/NTLM edit screen.
* knownvisitor-ntlm.enabled
* knownvisitor-ntlm.searchDN
* knownvisitor-ntlm.field.username
* knownvisitor-ntlm.field.email
* knownvisitor-ntlm.field.realname
* knownvisitor-ntlm.domaincontroller
* knownvisitor-ntlm.logonname
* knownvisitor-ntlm.logonpassword

When editing from the modules/NTLM edit screen:
* knownvisitor-ntlm.enabled            --> is the module enabled?
* knownvisitor-ntlm.searchDN           --> the search DN for the users
* knownvisitor-ntlm.field.realname     --> the name of the field in LDAP holding the username (typical:"cn") 
* knownvisitor-ntlm.field.email        --> the email field (typical: "mail")
* knownvisitor-ntlm.field.username     --> the real name field (typical: "sAMAccountName")
* knownvisitor-ntlm.domaincontroller   --> the name or IP of the domain controller
* knownvisitor-ntlm.logonname          --> the real name of an account in the same searchDN
* knownvisitor-ntlm.logonpassword      --> the login password of this user

! Do not forget to publish the properties when done editing these settings

Information about how to configure NTLM authentication in FireFox:
http://adam.theficus.com/archives/2004/09/firefox_tutoria.html


TODO:
Translate extra information in Dutch about this module to English, written in ITM-131 by Nico.